public class Course {
    private String courseTitle;
    private String courseCode;
    private int[] ltpsc; // Lecture, Tutorial, Practical, Self Study, Credits
    private String faculty;
    private String labAssistance;
    private String department;
    private String classroom;
    private int studentCount;
    private String[] preferredDays; // Faculty preferred days for teaching
    private int[] preferredTimeSlots; // Faculty preferred time slots

    public Course(String courseTitle, String courseCode, int[] ltpsc, String faculty, String labAssistance) {
        this.courseTitle = courseTitle;
        this.courseCode = courseCode;
        this.ltpsc = ltpsc;
        this.faculty = faculty;
        this.labAssistance = labAssistance;
        this.preferredDays = new String[0];
        this.preferredTimeSlots = new int[0];
    }

    // Enhanced constructor with additional parameters
    public Course(String courseTitle, String courseCode, int[] ltpsc, String faculty, String labAssistance,
            String department, String classroom, int studentCount) {
        this(courseTitle, courseCode, ltpsc, faculty, labAssistance);
        this.department = department;
        this.classroom = classroom;
        this.studentCount = studentCount;
    }

    // Getters and setters
    public String getCourseTitle() {
        return courseTitle;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int[] getLtpsc() {
        return ltpsc;
    }

    public String getDetails() {
        return (ltpsc[0] + "-" + ltpsc[1] + "-" + ltpsc[2] + "-" + ltpsc[3] + "-" + ltpsc[4]);
    }

    public String getFaculty() {
        return faculty;
    }

    public String getLabAssistance() {
        return labAssistance;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public String[] getPreferredDays() {
        return preferredDays;
    }

    public void setPreferredDays(String[] preferredDays) {
        this.preferredDays = preferredDays;
    }

    public int[] getPreferredTimeSlots() {
        return preferredTimeSlots;
    }

    public void setPreferredTimeSlots(int[] preferredTimeSlots) {
        this.preferredTimeSlots = preferredTimeSlots;
    }

    @Override
    public String toString() {
        return "\nCourse Code: " + courseCode + "\nCourse Title: " + courseTitle +
                "\nL-T-P-S-C: " + ltpsc[0] + "-" + ltpsc[1] + "-" + ltpsc[2] + "-" + ltpsc[3] + "-" + ltpsc[4] +
                "\nFaculty: " + faculty + "\nLab Assistance: " + labAssistance +
                (department != null ? "\nDepartment: " + department : "") +
                (classroom != null ? "\nClassroom: " + classroom : "") +
                (studentCount > 0 ? "\nStudent Count: " + studentCount : "");
    }
}
