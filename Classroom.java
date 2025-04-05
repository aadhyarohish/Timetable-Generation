public class Classroom {
    private String name;
    private int capacity;
    private boolean hasLabEquipment;
    private String building;

    public Classroom(String name, int capacity, boolean hasLabEquipment, String building) {
        this.name = name;
        this.capacity = capacity;
        this.hasLabEquipment = hasLabEquipment;
        this.building = building;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean hasLabEquipment() {
        return hasLabEquipment;
    }

    public String getBuilding() {
        return building;
    }

    @Override
    public String toString() {
        return name + " (Capacity: " + capacity + ", " + (hasLabEquipment ? "Lab" : "Theory") + ")";
    }
}
