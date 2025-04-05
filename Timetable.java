import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Timetable {
    private ArrayList<ArrayList<TimeSlot>> slots;
    private Map<String, String> assignedClassrooms; // courseCode -> classroomName
    private String batchName;

    public static class TimeSlot {
        private String courseCode;
        private String type; // "Lecture", "Tutorial", "Lab", "Break"
        private String faculty;
        private String classroom;

        public TimeSlot() {
            this.courseCode = "";
            this.type = "";
            this.faculty = "";
            this.classroom = "";
        }

        public TimeSlot(String courseCode, String type, String faculty, String classroom) {
            this.courseCode = courseCode;
            this.type = type;
            this.faculty = faculty;
            this.classroom = classroom;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getType() {
            return type;
        }

        public String getFaculty() {
            return faculty;
        }

        public String getClassroom() {
            return classroom;
        }

        public boolean isEmpty() {
            return courseCode == null || courseCode.isEmpty();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "";
            return courseCode + " (" + type + ")";
        }
    }

    public Timetable(String batchName) {
        this.batchName = batchName;
        slots = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ArrayList<TimeSlot> daySlots = new ArrayList<>();
            for (int j = 0; j < 18; j++) {
                daySlots.add(new TimeSlot());
            }
            slots.add(daySlots);
        }
        assignedClassrooms = new HashMap<>();
    }

    public void assignCourseToSlot(int day, int startSlot, int duration, String courseCode, String type, String faculty,
            String classroom) {
        for (int i = 0; i < duration; i++) {
            slots.get(day).set(startSlot + i, new TimeSlot(courseCode, type, faculty, classroom));
        }
        assignedClassrooms.put(courseCode, classroom);
    }

    public ArrayList<ArrayList<TimeSlot>> getSlots() {
        return slots;
    }

    public String getBatchName() {
        return batchName;
    }

    public String getAssignedClassroom(String courseCode) {
        return assignedClassrooms.get(courseCode);
    }
}
