import java.util.*;
import java.io.*;

public class ClassroomManager {
    private Map<String, Classroom> classrooms;
    private Map<String, Map<String, boolean[]>> classroomAvailability; // classroom -> day -> slots

    public ClassroomManager() {
        classrooms = new HashMap<>();
        classroomAvailability = new HashMap<>();
        initDefaultClassrooms();
    }

    private void initDefaultClassrooms() {
        // Initialize with some default classrooms if no configuration is loaded
        addClassroom(new Classroom("LH-101", 120, false, "Main Building"));
        addClassroom(new Classroom("LH-102", 120, false, "Main Building"));
        addClassroom(new Classroom("LH-103", 80, false, "Main Building"));
        addClassroom(new Classroom("LH-104", 80, false, "Main Building"));
        addClassroom(new Classroom("Lab-201", 60, true, "Lab Building"));
        addClassroom(new Classroom("Lab-202", 60, true, "Lab Building"));
        addClassroom(new Classroom("Lab-203", 40, true, "Lab Building"));
    }

    public void loadClassrooms(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    String name = parts[0].trim();
                    int capacity = Integer.parseInt(parts[1].trim());
                    boolean hasLabEquipment = Boolean.parseBoolean(parts[2].trim());
                    String building = parts.length > 3 ? parts[3].trim() : "Main Building";
                    addClassroom(new Classroom(name, capacity, hasLabEquipment, building));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Classroom configuration file not found: " + fileName);
            System.out.println("Using default classroom configuration");
        }
    }

    public void addClassroom(Classroom classroom) {
        classrooms.put(classroom.getName(), classroom);
        // Initialize availability for all days
        Map<String, boolean[]> daySlots = new HashMap<>();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        for (String day : days) {
            boolean[] slots = new boolean[18]; // 18 slots per day, false means available
            daySlots.put(day, slots);
        }

        classroomAvailability.put(classroom.getName(), daySlots);
    }

    public Classroom findSuitableClassroom(int requiredCapacity, boolean needsLabEquipment, String day, int startSlot,
            int duration) {
        for (Classroom classroom : classrooms.values()) {
            if (classroom.getCapacity() >= requiredCapacity &&
                    (!needsLabEquipment || classroom.hasLabEquipment()) &&
                    isClassroomAvailable(classroom.getName(), day, startSlot, duration)) {
                return classroom;
            }
        }
        return null;
    }

    public boolean isClassroomAvailable(String classroomName, String day, int startSlot, int duration) {
        if (!classrooms.containsKey(classroomName) ||
                !classroomAvailability.containsKey(classroomName) ||
                !classroomAvailability.get(classroomName).containsKey(day)) {
            return false;
        }

        boolean[] slots = classroomAvailability.get(classroomName).get(day);
        for (int i = 0; i < duration; i++) {
            if (startSlot + i >= slots.length || slots[startSlot + i]) {
                return false; // Slot is already booked or out of range
            }
        }
        return true;
    }

    public void bookClassroom(String classroomName, String day, int startSlot, int duration) {
        if (!classrooms.containsKey(classroomName) ||
                !classroomAvailability.containsKey(classroomName) ||
                !classroomAvailability.get(classroomName).containsKey(day)) {
            return;
        }

        boolean[] slots = classroomAvailability.get(classroomName).get(day);
        for (int i = 0; i < duration; i++) {
            if (startSlot + i < slots.length) {
                slots[startSlot + i] = true; // Mark as booked
            }
        }
    }

    public List<String> getAvailableClassrooms(boolean needsLab, String day, int startSlot, int duration) {
        List<String> available = new ArrayList<>();
        for (Classroom classroom : classrooms.values()) {
            if ((!needsLab || classroom.hasLabEquipment()) &&
                    isClassroomAvailable(classroom.getName(), day, startSlot, duration)) {
                available.add(classroom.getName());
            }
        }
        return available;
    }

    public Classroom getClassroom(String name) {
        return classrooms.get(name);
    }

    public Collection<Classroom> getAllClassrooms() {
        return classrooms.values();
    }
}
