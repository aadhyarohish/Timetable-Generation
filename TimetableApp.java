import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableApp {
    public static void main(String[] args) {
        File coursesFolder = new File("./data");

        if (!coursesFolder.exists() || !coursesFolder.isDirectory()) {
            System.out.println("Courses folder not found!");
            return;
        }

        File[] csvFiles = coursesFolder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            System.out.println("No CSV files found in the Courses folder.");
            return;
        }

        TimetableGenerator generator = new TimetableGenerator();
        ClassroomManager classroomManager = new ClassroomManager();
        classroomManager.loadClassrooms("data/classrooms.csv");

        Map<String, Timetable> timetableMap = new HashMap<>();
        Map<String, List<Course>> courseMap = new HashMap<>();

        for (File csvFile : csvFiles) {
            String batchName = csvFile.getName().replace(".csv", "");
            try {
                List<Course> courses = CourseManager.loadCourses(csvFile.getPath());
                Timetable timetable = new Timetable(batchName);
                generator.generateTimetable(courses, timetable, classroomManager);
                timetableMap.put(batchName, timetable);
                courseMap.put(batchName, courses);

                // Save timetable to CSV
                TimetableGUI.saveTimetableToCSV(timetable, batchName);
                System.out.println("Timetable saved for batch: " + batchName);
            } catch (Exception e) {
                System.out.println("Error generating timetable for batch " + batchName + ": " + e.getMessage());
            }
        }

        // Launch GUI
        javax.swing.SwingUtilities.invokeLater(() -> new TimetableGUI(timetableMap, courseMap));
    }
}
