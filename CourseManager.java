import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CourseManager {
    // Map to track faculty schedules across all batches
    private static Map<String, List<ScheduleEntry>> facultyScheduleMap = new HashMap<>();

    // Class to track faculty schedule entries
    public static class ScheduleEntry {
        private String day;
        private int startSlot;
        private int endSlot;
        private String courseCode;

        public ScheduleEntry(String day, int startSlot, int endSlot, String courseCode) {
            this.day = day;
            this.startSlot = startSlot;
            this.endSlot = endSlot;
            this.courseCode = courseCode;
        }

        public String getDay() {
            return day;
        }

        public int getStartSlot() {
            return startSlot;
        }

        public int getEndSlot() {
            return endSlot;
        }

        public String getCourseCode() {
            return courseCode;
        }
    }

    public static List<Course> loadCourses(String fileName) {
        List<Course> courses = new ArrayList<>();
        String batchName = new File(fileName).getName().replace(".csv", "");

        try (Scanner scanner = new Scanner(new File(fileName))) {
            String headerLine = scanner.nextLine(); // Read header line
            String[] headers = headerLine.split(",");

            // Map column indices
            Map<String, Integer> columnMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                columnMap.put(headers[i].trim(), i);
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Split handling quoted values

                    // Extract data with fallbacks for missing columns
                    String courseTitle = getValueOrDefault(parts, columnMap, "Course Title", 0);
                    String courseCode = getValueOrDefault(parts, columnMap, "Course Code", 1);

                    // Parse LTPSC values safely
                    int lecture = parseIntSafely(getValueOrDefault(parts, columnMap, "Lecture Hours", 2));
                    int tutorial = parseIntSafely(getValueOrDefault(parts, columnMap, "Tutorial Hours", 3));
                    int practical = parseIntSafely(getValueOrDefault(parts, columnMap, "Practical Hours", 4));
                    int selfStudy = parseIntSafely(getValueOrDefault(parts, columnMap, "Self Study", 5));
                    int credits = parseIntSafely(getValueOrDefault(parts, columnMap, "Credits", 6));
                    int[] ltpsc = { lecture, tutorial, practical, selfStudy, credits };

                    String faculty = getValueOrDefault(parts, columnMap, "Faculty", 7);
                    String labAssistance = columnMap.containsKey("Lab Assistance")
                            && parts.length > columnMap.get("Lab Assistance")
                                    ? parts[columnMap.get("Lab Assistance")].trim()
                                    : "";

                    // Extract department from batch name
                    String department = extractDepartment(batchName);

                    // Create course with additional metadata
                    Course course = new Course(courseTitle, courseCode, ltpsc, faculty, labAssistance, department, "",
                            0);
                    courses.add(course);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing file: " + fileName);
            e.printStackTrace();
        }

        return courses;
    }

    // Helper method to extract department from batch name
    private static String extractDepartment(String batchName) {
        if (batchName.startsWith("CSE"))
            return "Computer Science";
        else if (batchName.startsWith("DSAI"))
            return "Data Science & AI";
        else if (batchName.startsWith("ECE"))
            return "Electronics & Communication";
        return batchName; // Default to batch name if unknown
    }

    // Safe value extraction with index fallback
    private static String getValueOrDefault(String[] parts, Map<String, Integer> columnMap, String columnName,
            int defaultIndex) {
        if (columnMap.containsKey(columnName) && parts.length > columnMap.get(columnName)) {
            return parts[columnMap.get(columnName)].trim();
        } else if (parts.length > defaultIndex) {
            return parts[defaultIndex].trim();
        }
        return "";
    }

    // Safe integer parsing
    private static int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Track faculty schedule across all batches
    public static void addFacultySchedule(String faculty, String day, int startSlot, int endSlot, String courseCode) {
        if (faculty == null || faculty.isEmpty() || faculty.equals("Not Applicable")
                || faculty.equals("Not Provided")) {
            return;
        }

        facultyScheduleMap.putIfAbsent(faculty, new ArrayList<>());
        facultyScheduleMap.get(faculty).add(new ScheduleEntry(day, startSlot, endSlot, courseCode));
    }

    // Check faculty availability
    public static boolean isFacultyAvailable(String faculty, String day, int startSlot, int endSlot) {
        if (faculty == null || faculty.isEmpty() || faculty.equals("Not Applicable")
                || faculty.equals("Not Provided")) {
            return true; // No faculty assigned, so no conflict
        }

        if (!facultyScheduleMap.containsKey(faculty)) {
            return true; // Faculty has no other assignments yet
        }

        // Check for time conflicts
        for (ScheduleEntry entry : facultyScheduleMap.get(faculty)) {
            if (entry.getDay().equals(day)) {
                // Check for overlap
                if ((startSlot <= entry.getEndSlot() && endSlot >= entry.getStartSlot())) {
                    return false; // Conflict found
                }

                // Check for consecutive classes requirement (REQ-10)
                if ((Math.abs(startSlot - entry.getEndSlot()) < 3) ||
                        (Math.abs(endSlot - entry.getStartSlot()) < 3)) {
                    return false; // Consecutive classes without enough gap
                }
            }
        }
        return true;
    }

    // Get faculty's busy slots for a day
    public static List<int[]> getFacultyBusySlots(String faculty, String day) {
        List<int[]> busySlots = new ArrayList<>();
        if (faculty == null || faculty.isEmpty() || !facultyScheduleMap.containsKey(faculty)) {
            return busySlots;
        }

        for (ScheduleEntry entry : facultyScheduleMap.get(faculty)) {
            if (entry.getDay().equals(day)) {
                busySlots.add(new int[] { entry.getStartSlot(), entry.getEndSlot() });
            }
        }
        return busySlots;
    }

    // Clear faculty schedule map (for testing or restarting)
    public static void clearFacultyScheduleMap() {
        facultyScheduleMap.clear();
    }

    // Get day index from name
    public static int getDayIndex(String dayName) {
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(dayName)) {
                return i;
            }
        }
        return -1;
    }

    public static String getDayName(int dayIndex) {
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        if (dayIndex >= 0 && dayIndex < days.length) {
            return days[dayIndex];
        }
        return "Invalid Day";
    }

}
