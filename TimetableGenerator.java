import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimetableGenerator {

    private boolean areSlotsAvailable(ArrayList<ArrayList<Timetable.TimeSlot>> slots, int day, int startSlot,
            int duration) {
        for (int i = 0; i < duration; i++) {
            if (!slots.get(day).get(startSlot + i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void assignLectures(Timetable timetable, Course course, ClassroomManager classroomManager) {
        int lectureHours = course.getLtpsc()[0];
        int lectureSessions = (int) Math.ceil((double) lectureHours / 1.5);
        int duration = 3;
        int total = 0;
        ArrayList<ArrayList<Timetable.TimeSlot>> slots = timetable.getSlots();
        Set<Integer> assignedDays = new HashSet<>();

        outerLoop: for (int day = 0; day < 5; day++) {
            if (assignedDays.contains(day))
                continue;
            for (int slot = 0; slot <= 5; slot++) {
                if (total == 2)
                    break;
                if (areSlotsAvailable(slots, day, slot, duration)) {
                    Classroom classroom = classroomManager.findSuitableClassroom(course.getStudentCount(), false,
                            CourseManager.getDayName(day), slot, duration);
                    if (classroom != null) {
                        System.out.println("Assigning lecture for " + course.getCourseCode() + " on day " + day
                                + " at slot " + slot);
                        timetable.assignCourseToSlot(day, slot, duration, course.getCourseCode(), "Lecture",
                                course.getFaculty(), classroom.getName());
                        classroomManager.bookClassroom(classroom.getName(), CourseManager.getDayName(day), slot,
                                duration);
                        assignedDays.add(day);
                        total++;
                        lectureSessions--;
                        if (lectureSessions == 0)
                            break outerLoop;
                        break;
                    }
                }
            }
            total = 0;
        }
    }

    private void assignTutorials(Timetable timetable, Course course, ClassroomManager classroomManager) {
        int tutorialHours = course.getLtpsc()[1];
        int duration = 2;
        if (tutorialHours == 0)
            return;
        ArrayList<ArrayList<Timetable.TimeSlot>> slots = timetable.getSlots();
        Set<Integer> assignedDays = new HashSet<>();

        outerLoop: for (int day = 0; day < 5; day++) {
            if (assignedDays.contains(day))
                continue;
            if (areSlotsAvailable(slots, day, 6, duration)) {
                Classroom classroom = classroomManager.findSuitableClassroom(course.getStudentCount(), false,
                        CourseManager.getDayName(day), 6, duration);
                if (classroom != null) {
                    System.out.println(
                            "Assigning tutorial for " + course.getCourseCode() + " on day " + day + " at slot 6");
                    timetable.assignCourseToSlot(day, 6, duration, course.getCourseCode(), "Tutorial",
                            course.getFaculty(), classroom.getName());
                    classroomManager.bookClassroom(classroom.getName(), CourseManager.getDayName(day), 6, duration);
                    break outerLoop;
                }
            }
        }
    }

    private void assignLabs(Timetable timetable, Course course, ClassroomManager classroomManager) {
        int labHours = course.getLtpsc()[2];
        int duration = 4;
        if (labHours == 0)
            return;
        ArrayList<ArrayList<Timetable.TimeSlot>> slots = timetable.getSlots();
        boolean labAssigned = false;

        outerLoop: for (int day = 0; day < 5; day++) {
            if (day == 1)
                continue;
            for (int slot = 11; slot <= 13; slot++) {
                if (slot + duration <= slots.get(day).size() && areSlotsAvailable(slots, day, slot, duration)) {
                    Classroom classroom = classroomManager.findSuitableClassroom(course.getStudentCount(), true,
                            CourseManager.getDayName(day), slot, duration);
                    if (classroom != null) {
                        System.out.println(
                                "Assigning lab for " + course.getCourseCode() + " on day " + day + " at slot " + slot);
                        timetable.assignCourseToSlot(day, slot, duration, course.getCourseCode(), "Lab",
                                course.getFaculty(), classroom.getName());
                        classroomManager.bookClassroom(classroom.getName(), CourseManager.getDayName(day), slot,
                                duration);
                        labAssigned = true;
                        break outerLoop;
                    }
                }
            }
        }

        if (!labAssigned) {
            for (int day = 0; day < 5; day++) {
                for (int slot = 11; slot <= 13; slot++) {
                    if (slot + 3 <= slots.get(day).size() && areSlotsAvailable(slots, day, slot, 3)) {
                        Classroom classroom = classroomManager.findSuitableClassroom(course.getStudentCount(), false,
                                CourseManager.getDayName(day), slot, 3);
                        if (classroom != null) {
                            System.out.println("Assigning extra lecture for " + course.getCourseCode() + " on day "
                                    + day + " at slot " + slot);
                            timetable.assignCourseToSlot(day, slot, 3, course.getCourseCode(), "Lecture",
                                    course.getFaculty(), classroom.getName());
                            classroomManager.bookClassroom(classroom.getName(), CourseManager.getDayName(day), slot, 3);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void generateTimetable(List<Course> courseList, Timetable timetable, ClassroomManager classroomManager) {
        for (Course course : courseList) {
            assignLectures(timetable, course, classroomManager);
            assignTutorials(timetable, course, classroomManager);
            assignLabs(timetable, course, classroomManager);
        }
    }
}
