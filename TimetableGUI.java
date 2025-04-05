import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TimetableGUI extends JFrame {
    private Map<String, Timetable> timetableMap;
    private Map<String, List<Course>> courseMap;
    private JComboBox<String> batchSelector;
    private JPanel timetablePanel;

    public TimetableGUI(Map<String, Timetable> timetableMap, Map<String, List<Course>> courseMap) {
        this.timetableMap = timetableMap;
        this.courseMap = courseMap;

        setTitle("Timetable Viewer");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        JPanel topPanel = new JPanel();
        batchSelector = new JComboBox<>();
        for (String batchName : timetableMap.keySet()) {
            batchSelector.addItem(batchName);
        }

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> {
            String selectedBatch = (String) batchSelector.getSelectedItem();
            if (selectedBatch != null) {
                try {
                    saveTimetableToCSV(timetableMap.get(selectedBatch), selectedBatch);
                    JOptionPane.showMessageDialog(this,
                            "Timetable exported successfully to " + selectedBatch + "_timetable.csv");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting timetable: " + ex.getMessage(), "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        topPanel.add(new JLabel("Select Batch: "));
        topPanel.add(batchSelector);
        topPanel.add(exportButton);

        // Create timetable panel
        timetablePanel = new JPanel(new BorderLayout());

        // Add listener to batch selector
        batchSelector.addActionListener(e -> updateTimetableDisplay());

        // Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(timetablePanel, BorderLayout.CENTER);

        // Initial display
        if (batchSelector.getItemCount() > 0) {
            updateTimetableDisplay();
        }

        setVisible(true);
    }

    private void updateTimetableDisplay() {
        timetablePanel.removeAll();

        String selectedBatch = (String) batchSelector.getSelectedItem();
        if (selectedBatch == null)
            return;

        Timetable timetable = timetableMap.get(selectedBatch);
        if (timetable == null)
            return;

        // Create timetable grid
        JPanel grid = new JPanel(new GridLayout(9, 6));

        // Add header row
        grid.add(new JLabel("Time/Day"));
        grid.add(new JLabel("Monday"));
        grid.add(new JLabel("Tuesday"));
        grid.add(new JLabel("Wednesday"));
        grid.add(new JLabel("Thursday"));
        grid.add(new JLabel("Friday"));

        // Time slots - Updated with standard class durations
        String[] timeSlots = {
                "9:00 - 10:30", // 1.5 hour lecture slot
                "10:30 - 12:00", // 1.5 hour lecture slot
                "12:00 - 13:00", // Lunch break
                "13:00 - 14:00", // 1 hour tutorial slot
                "14:00 - 15:00", // 1 hour tutorial slot
                "15:00 - 17:00", // 2 hour lab slot
                "17:00 - 18:00", // 1 hour extra slot
                "18:00 - 19:00" // 1 hour extra slot
        };

        // Add time slots and course information
        for (int slot = 0; slot < 8; slot++) {
            grid.add(new JLabel(timeSlots[slot]));

            for (int day = 0; day < 5; day++) {
                // Convert from standard time slots to the original 30-minute slots
                int startSlot = getOriginalSlotIndex(slot);
                int endSlot = getOriginalSlotEndIndex(slot);

                // Create a panel for this time slot
                JPanel cellPanel = new JPanel();
                cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.Y_AXIS));

                // Check if there's a course scheduled in this slot
                boolean hasContent = false;

                for (int i = startSlot; i < endSlot; i++) {
                    if (i < timetable.getSlots().get(day).size()) {
                        Timetable.TimeSlot timeSlot = timetable.getSlots().get(day).get(i);
                        if (!timeSlot.isEmpty()) {
                            JLabel courseLabel = new JLabel(timeSlot.getCourseCode());
                            JLabel typeLabel = new JLabel(timeSlot.getType());
                            JLabel facultyLabel = new JLabel(timeSlot.getFaculty());
                            JLabel roomLabel = new JLabel(timeSlot.getClassroom());

                            cellPanel.add(courseLabel);
                            cellPanel.add(typeLabel);
                            cellPanel.add(facultyLabel);
                            cellPanel.add(roomLabel);

                            // Set background color based on course type
                            if (timeSlot.getType().equals("Lecture")) {
                                cellPanel.setBackground(new Color(230, 255, 230)); // Light green
                            } else if (timeSlot.getType().equals("Tutorial")) {
                                cellPanel.setBackground(new Color(255, 255, 230)); // Light yellow
                            } else if (timeSlot.getType().equals("Lab")) {
                                cellPanel.setBackground(new Color(230, 230, 255)); // Light blue
                            } else if (timeSlot.getType().equals("Break")) {
                                cellPanel.setBackground(new Color(200, 200, 200)); // Light gray
                            }

                            hasContent = true;
                            break; // Only show the first course in this time slot
                        }
                    }
                }

                if (!hasContent) {
                    // If it's lunch time, mark it as a break
                    if (slot == 2) {
                        JLabel breakLabel = new JLabel("LUNCH BREAK");
                        cellPanel.add(breakLabel);
                        cellPanel.setBackground(new Color(200, 200, 200)); // Light gray
                    } else {
                        cellPanel.add(new JLabel(""));
                    }
                }

                grid.add(cellPanel);
            }
        }

        timetablePanel.add(new JScrollPane(grid), BorderLayout.CENTER);
        timetablePanel.revalidate();
        timetablePanel.repaint();
    }

    // Helper method to convert from display time slots to original 30-minute slots
    private int getOriginalSlotIndex(int displaySlot) {
        switch (displaySlot) {
            case 0:
                return 0; // 9:00 - 10:30 (starts at original slot 0)
            case 1:
                return 3; // 10:30 - 12:00 (starts at original slot 3)
            case 2:
                return 6; // 12:00 - 13:00 (starts at original slot 6)
            case 3:
                return 8; // 13:00 - 14:00 (starts at original slot 8)
            case 4:
                return 10; // 14:00 - 15:00 (starts at original slot 10)
            case 5:
                return 12; // 15:00 - 17:00 (starts at original slot 12)
            case 6:
                return 16; // 17:00 - 18:00 (starts at original slot 16)
            case 7:
                return 18; // 18:00 - 19:00 (starts at original slot 18)
            default:
                return 0;
        }
    }

    // Helper method to get the end index for each display slot
    private int getOriginalSlotEndIndex(int displaySlot) {
        switch (displaySlot) {
            case 0:
                return 3; // 9:00 - 10:30 (ends at original slot 3)
            case 1:
                return 6; // 10:30 - 12:00 (ends at original slot 6)
            case 2:
                return 8; // 12:00 - 13:00 (ends at original slot 8)
            case 3:
                return 10; // 13:00 - 14:00 (ends at original slot 10)
            case 4:
                return 12; // 14:00 - 15:00 (ends at original slot 12)
            case 5:
                return 16; // 15:00 - 17:00 (ends at original slot 16)
            case 6:
                return 18; // 17:00 - 18:00 (ends at original slot 18)
            case 7:
                return 20; // 18:00 - 19:00 (ends at original slot 20)
            default:
                return 0;
        }
    }

    public static void saveTimetableToCSV(Timetable timetable, String batchName) {
        try (FileWriter writer = new FileWriter(batchName + "_timetable.csv")) {
            // Write header
            writer.write("Time Slot,Monday,Tuesday,Wednesday,Thursday,Friday\n");

            // Time slots - Updated with standard class durations
            String[] timeSlots = {
                    "9:00 - 10:30", // 1.5 hour lecture slot
                    "10:30 - 12:00", // 1.5 hour lecture slot
                    "12:00 - 13:00", // Lunch break
                    "13:00 - 14:00", // 1 hour tutorial slot
                    "14:00 - 15:00", // 1 hour tutorial slot
                    "15:00 - 17:00", // 2 hour lab slot
                    "17:00 - 18:00", // 1 hour extra slot
                    "18:00 - 19:00" // 1 hour extra slot
            };

            // Original 30-minute slot mappings
            int[][] slotMappings = {
                    { 0, 1, 2 }, // 9:00 - 10:30
                    { 3, 4, 5 }, // 10:30 - 12:00
                    { 6, 7 }, // 12:00 - 13:00
                    { 8, 9 }, // 13:00 - 14:00
                    { 10, 11 }, // 14:00 - 15:00
                    { 12, 13, 14, 15 }, // 15:00 - 17:00
                    { 16, 17 }, // 17:00 - 18:00
                    { 18, 19 } // 18:00 - 19:00
            };

            // Write each row
            for (int slot = 0; slot < timeSlots.length; slot++) {
                writer.write(timeSlots[slot]);

                for (int day = 0; day < 5; day++) {
                    writer.write(",");

                    // Special case for lunch break
                    if (slot == 2) {
                        writer.write("LUNCH BREAK");
                        continue;
                    }

                    // Check all original slots in this time range
                    boolean courseFound = false;
                    for (int originalSlot : slotMappings[slot]) {
                        if (originalSlot < timetable.getSlots().get(day).size()) {
                            Timetable.TimeSlot timeSlot = timetable.getSlots().get(day).get(originalSlot);
                            if (!timeSlot.isEmpty()) {
                                writer.write(timeSlot.getCourseCode() + " (" + timeSlot.getType() + ")");
                                if (!timeSlot.getFaculty().isEmpty()) {
                                    writer.write(" - " + timeSlot.getFaculty());
                                }
                                if (!timeSlot.getClassroom().isEmpty()) {
                                    writer.write(" [" + timeSlot.getClassroom() + "]");
                                }
                                courseFound = true;
                                break; // Only show the first course in this time slot
                            }
                        }
                    }
                }
                writer.write("\n");
            }

            System.out.println("Timetable exported to " + batchName + "_timetable.csv");
        } catch (IOException e) {
            System.err.println("Error exporting timetable: " + e.getMessage());
        }
    }
}
