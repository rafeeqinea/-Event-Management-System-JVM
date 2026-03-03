package userinterface;

import LogicHandling.Event;
import LogicHandling.HandlingServices.ValidationHandler;
import LogicHandling.Venue;
import LogicHandling.HandlingServices.EventManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import javax.swing.SortOrder;

/**
 * Panel for creating and managing events
 * UI layout is defined in EventPanel.form
 */
public class EventPanel extends JPanel {
    // Components bound from .form file
    private JPanel mainPanel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JScrollPane descriptionScrollPane;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> venueComboBox;
    private JTextField estimatedAttendeesField;
    private JTextField cityField;
    private JTextField startDateField;
    private JTextField startTimeField;
    private JTextField endDateField;
    private JTextField endTimeField;
    private JButton createButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton viewButton;
    private JButton editDetailsButton;
    private JButton findSlotButton;
    private JButton generateScheduleButton;
    private JTable eventTable;
    private JScrollPane tableScrollPane;

    private final EventManager facade;
    private final ValidationHandler validationService;
    private DefaultTableModel tableModel;
    private JTabbedPane parentTabbedPane;
    private LogicHandling.DataPersistence dataPersistence;

    // Map to store venue name to venue object
    private final java.util.Map<String, Venue> venueMap = new java.util.HashMap<>();

    public EventPanel(EventManager facade) {
        this.facade = facade;
        this.validationService = new ValidationHandler();

        // Add the mainPanel from the form to this panel
        setLayout(new java.awt.BorderLayout());
        add(mainPanel, java.awt.BorderLayout.CENTER);

        // Initialize combo boxes
        initializeComboBoxes();

        // Initialize table
        initializeTable();

        // Setup button listeners
        setupListeners();

        // Setup click-to-deselect behavior
        setupDeselectOnClick();

        // Load data
        refreshEventList();
    }

    private void initializeComboBoxes() {
        // Initialize event type combo box
        typeComboBox.addItem("General");
        typeComboBox.addItem("Conference");
        typeComboBox.addItem("Workshop");
        typeComboBox.addItem("Seminar");
        typeComboBox.addItem("Meeting");
        typeComboBox.addItem("Social");
        typeComboBox.setEditable(true);

        // Populate venue combo box (now defined in form)
        refreshVenueComboBox();
    }

    /**
     * Refresh the venue combo box with current venues
     */
    private void refreshVenueComboBox() {
        if (venueComboBox == null) return;

        venueComboBox.removeAllItems();
        venueMap.clear();

        // Add "No Venue" option
        venueComboBox.addItem("(No Venue)");

        // Add all available venues
        List<Venue> venues = facade.getAllVenues();
        for (Venue venue : venues) {
            String displayName = venue.getName() + " (Capacity: " + venue.getCapacity() + ")";
            venueComboBox.addItem(displayName);
            venueMap.put(displayName, venue);
        }
    }

    private void initializeTable() {
        String[] columnNames = {"ID", "Title", "Type", "Start Time", "End Time", "Venue", "Estimated Attendees", "Registered"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable.setModel(tableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add table sorting functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        eventTable.setRowSorter(sorter);

        // Create custom comparator for date/time columns
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Comparator<String> dateTimeComparator = (s1, s2) -> {
            try {
                LocalDateTime dt1 = LocalDateTime.parse(s1, formatter);
                LocalDateTime dt2 = LocalDateTime.parse(s2, formatter);
                return dt1.compareTo(dt2);
            } catch (Exception e) {
                // If parsing fails, fall back to string comparison
                return s1.compareTo(s2);
            }
        };

        // Create custom comparator for numeric columns
        Comparator<Object> numericComparator = (o1, o2) -> {
            try {
                Integer num1 = Integer.parseInt(o1.toString());
                Integer num2 = Integer.parseInt(o2.toString());
                return num1.compareTo(num2);
            } catch (Exception e) {
                // If parsing fails, fall back to string comparison
                return o1.toString().compareTo(o2.toString());
            }
        };

        // Apply date/time comparator to Start Time (column 3) and End Time (column 4)
        sorter.setComparator(3, dateTimeComparator);
        sorter.setComparator(4, dateTimeComparator);

        // Apply numeric comparator to Estimated Attendees (column 6) and Registered (column 7)
        sorter.setComparator(6, numericComparator);
        sorter.setComparator(7, numericComparator);

        // Set default sort by Start Time (column 3) in ascending order
        java.util.List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Hide ID column
        eventTable.getColumnModel().getColumn(0).setMinWidth(0);
        eventTable.getColumnModel().getColumn(0).setMaxWidth(0);
        eventTable.getColumnModel().getColumn(0).setWidth(0);
    }

    private void setupListeners() {
        createButton.addActionListener(e -> createEvent());
        deleteButton.addActionListener(e -> deleteSelectedEvent());
        clearButton.addActionListener(e -> clearForm());
        viewButton.addActionListener(e -> viewSelectedEvent());
        editDetailsButton.addActionListener(e -> editSelectedEvent());
        findSlotButton.addActionListener(e -> findNextAvailableSlot());
        generateScheduleButton.addActionListener(e -> generateSchedule());
    }

    private void setupDeselectOnClick() {
        // Create mouse listener to clear table selection when clicking outside the table
        java.awt.event.MouseAdapter deselectListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                eventTable.clearSelection();
            }
        };

        // Add listener to main panel and form components
        addDeselectListenerRecursively(mainPanel, deselectListener);
    }

    private void addDeselectListenerRecursively(java.awt.Component component, java.awt.event.MouseAdapter listener) {
        // Don't add listener to the table itself or its scroll pane
        if (component == eventTable || component == tableScrollPane) {
            return;
        }

        // Don't add listener to buttons (they have their own actions)
        if (component instanceof JButton) {
            return;
        }

        // Add listener to this component
        component.addMouseListener(listener);

        // Recursively add to child components
        if (component instanceof Container container) {
            for (java.awt.Component child : container.getComponents()) {
                addDeselectListenerRecursively(child, listener);
            }
        }
    }

    private void findNextAvailableSlot() {
        int selectedRow = eventTable.getSelectedRow();

        // Try to get data from table selection OR form inputs
        int capacity;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        String city;

        if (selectedRow != -1) {
            // Mode 1: Use selected event from table
            try {
                // Convert view row index to model row index (important for sorting)
                int modelRow = eventTable.convertRowIndexToModel(selectedRow);
                String eventId = (String) tableModel.getValueAt(modelRow, 0);
                Event event = facade.getEvent(eventId);

                if (event == null) {
                    JOptionPane.showMessageDialog(this,
                            "Event not found.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                capacity = event.getEstimatedAttendees();
                startDateTime = event.getStartTime();
                endDateTime = event.getEndTime();
                city = event.getCity();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading selected event: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // Mode 2: Use form inputs
            try {
                String estimatedAttendeesStr = estimatedAttendeesField.getText().trim();
                String startDate = startDateField.getText().trim();
                String startTime = startTimeField.getText().trim();
                String endDate = endDateField.getText().trim();
                String endTime = endTimeField.getText().trim();
                city = cityField.getText().trim();

                // Validate required fields
                if (estimatedAttendeesStr.isEmpty() || startDate.isEmpty() || startTime.isEmpty()
                        || endDate.isEmpty() || endTime.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please either select an event from the table or fill in the form fields:\n" +
                                    "- Estimated Attendees\n" +
                                    "- Start Date & Time\n" +
                                    "- End Date & Time",
                            "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Parse inputs
                capacity = Integer.parseInt(estimatedAttendeesStr);
                startDateTime = validationService.parseDateTime(startDate, startTime);
                endDateTime = validationService.parseDateTime(endDate, endTime);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid number format for estimated attendees",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error parsing form inputs: " + ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            // Calculate duration of an event
            assert startDateTime != null;
            long durationHours = java.time.Duration.between(startDateTime, endDateTime).toHours();
            if (durationHours <= 0) {
                durationHours = 1; // Minimum 1 hour
            }

            // Format date/time for Scala
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedStartDate = startDateTime.format(dateFormatter);
            String formattedStartTime = startDateTime.format(timeFormatter);

            // Call Scala slot finder with city preference to find a venue within the same city
            String result = scala.SlotFinder$.MODULE$.findNextSlot(
                    capacity, (int) durationHours, formattedStartDate, formattedStartTime, city);

            // Check if a slot was found
            if (result.startsWith("No slot available:")) {
                JOptionPane.showMessageDialog(this, result,
                        "No Slot Available", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // If a table row is selected, you can assign a new earliest venue to that event
            if (selectedRow != -1) {
                // Ask user if they want to assign this venue
                int choice = JOptionPane.showConfirmDialog(this,
                        "Found available slot:\n\n" + result + "\n\nWould you like to assign this venue to the selected event?",
                        "Assign Venue?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    // Extract venue name from result and find the venue
                    String venueName = result.split("\n")[0].replace("Venue: ", "").split(" \\(")[0];

                    Venue selectedVenue = null;
                    for (Venue v : facade.getAllVenues()) {
                        if (v.getName().equals(venueName)) {
                            selectedVenue = v;
                            break;
                        }
                    }

                    if (selectedVenue != null) {
                        // Convert view row index to model row index (important for sorting)
                        int modelRow = eventTable.convertRowIndexToModel(selectedRow);
                        String eventId = (String) tableModel.getValueAt(modelRow, 0);
                        Event event = facade.getEvent(eventId);

                        if (event != null) {
                            boolean success = facade.updateEvent(
                                    event.getId(),
                                    event.getTitle(),
                                    event.getDescription(),
                                    event.getType(),
                                    event.getStartTime(),
                                    event.getEndTime(),
                                    selectedVenue.getId(),
                                    event.getEstimatedAttendees(),
                                    event.getCity()
                            );

                            if (success) {
                                if (dataPersistence != null) {
                                    dataPersistence.saveAll();
                                }
                                JOptionPane.showMessageDialog(this,
                                        "Venue assigned successfully!",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                refreshEventList();
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Failed to assign venue.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            } else {
                // Just show the result for form-based search
                JOptionPane.showMessageDialog(this,
                        "Found available slot:\n\n" + result,
                        "Available Slot", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error finding slot: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSchedule() {
        try {
            // Call the Scala scheduling algorithm
            String scheduleResult = scala.SchedulingAlgorithm$.MODULE$.generateScheduleForUI();

            // Display the result in a scrollable text area dialog
            JTextArea textArea = new JTextArea(scheduleResult);
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            textArea.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            // Ask if user wants to apply the schedule
            Object[] options = {"Apply Schedule", "Close"};
            int choice = JOptionPane.showOptionDialog(this,
                    scrollPane,
                    "Generated Schedule",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]);

            // If user wants to apply the schedule, update the events with assigned venues
            if (choice == 0) { // Apply Schedule
                applyGeneratedSchedule();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating schedule: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyGeneratedSchedule() {
        try {
            // Get assignments from Scala
            java.util.List<String[]> assignments = scala.SchedulingAlgorithm$.MODULE$.getAssignments();

            int updatedCount = 0;
            int failedCount = 0;

            for (String[] assignment : assignments) {
                String eventId = assignment[0];
                String venueIdStr = assignment[1];
                // String venueName = assignment[2]; // Not needed, just for reference

                try {
                    int venueId = Integer.parseInt(venueIdStr);
                    Event event = facade.getEvent(eventId);

                    if (event != null) {
                        boolean success = facade.updateEvent(
                                event.getId(),
                                event.getTitle(),
                                event.getDescription(),
                                event.getType(),
                                event.getStartTime(),
                                event.getEndTime(),
                                venueId,
                                event.getEstimatedAttendees(),
                                event.getCity()
                        );

                        if (success) {
                            updatedCount++;
                        } else {
                            failedCount++;
                        }
                    }
                } catch (NumberFormatException e) {
                    failedCount++;
                }
            }

            // Auto-save after applying schedule
            if (dataPersistence != null && updatedCount > 0) {
                dataPersistence.saveAll();
            }

            // Show results
            String message = String.format(
                    "Schedule applied successfully!\n\n" +
                            "Events updated: %d\n" +
                            "Failed updates: %d",
                    updatedCount, failedCount
            );

            JOptionPane.showMessageDialog(this, message,
                    "Schedule Applied", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the event list to show updated venues
            refreshEventList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error applying schedule: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to edit",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row index to model row index (important for sorting)
        int modelRow = eventTable.convertRowIndexToModel(selectedRow);
        String eventId = (String) tableModel.getValueAt(modelRow, 0);
        Event event = facade.getEvent(eventId);

        if (event == null) {
            JOptionPane.showMessageDialog(this, "Event not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create edit dialog
        JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Event Details", true);
        editDialog.setLayout(new java.awt.BorderLayout(10, 10));
        editDialog.setSize(500, 650);
        editDialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new java.awt.GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        JTextField titleEdit = new JTextField(event.getTitle(), 20);
        gbc.gridx = 1;
        formPanel.add(titleEdit, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        JTextArea descriptionEdit = new JTextArea(event.getDescription(), 3, 20);
        descriptionEdit.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionEdit);
        gbc.gridx = 1;
        formPanel.add(descScrollPane, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Type:"), gbc);
        JComboBox<String> typeEdit = new JComboBox<>(new String[]{"General", "Conference", "Workshop", "Seminar", "Meeting", "Social"});
        typeEdit.setSelectedItem(event.getType());
        typeEdit.setEditable(true);
        gbc.gridx = 1;
        formPanel.add(typeEdit, gbc);

        // Estimated Attendees
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Estimated Attendees:"), gbc);
        JTextField attendeesEdit = new JTextField(String.valueOf(event.getEstimatedAttendees()), 20);
        gbc.gridx = 1;
        formPanel.add(attendeesEdit, gbc);

        // City
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("City:"), gbc);
        JTextField cityEdit = new JTextField(event.getCity(), 20);
        gbc.gridx = 1;
        formPanel.add(cityEdit, gbc);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Start Date (dd/MM/yyyy):"), gbc);
        JTextField startDateEdit = new JTextField(event.getStartTime().format(dateFormatter), 20);
        gbc.gridx = 1;
        formPanel.add(startDateEdit, gbc);

        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Start Time (HH:mm):"), gbc);
        JTextField startTimeEdit = new JTextField(event.getStartTime().format(timeFormatter), 20);
        gbc.gridx = 1;
        formPanel.add(startTimeEdit, gbc);

        // End Date
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("End Date (dd/MM/yyyy):"), gbc);
        JTextField endDateEdit = new JTextField(event.getEndTime().format(dateFormatter), 20);
        gbc.gridx = 1;
        formPanel.add(endDateEdit, gbc);

        // End Time
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("End Time (HH:mm):"), gbc);
        JTextField endTimeEdit = new JTextField(event.getEndTime().format(timeFormatter), 20);
        gbc.gridx = 1;
        formPanel.add(endTimeEdit, gbc);

        // Venue
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(new JLabel("Venue:"), gbc);
        JComboBox<String> venueEdit = new JComboBox<>();
        venueEdit.addItem("(No Venue)");
        java.util.Map<String, Venue> editVenueMap = new java.util.HashMap<>();
        for (Venue v : facade.getAllVenues()) {
            venueEdit.addItem(v.getName());
            editVenueMap.put(v.getName(), v);
        }
        // Set current venue as selected
        if (event.getVenueId() != null) {
            Venue currentVenue = facade.getVenue(event.getVenueId());
            if (currentVenue != null) {
                venueEdit.setSelectedItem(currentVenue.getName());
            } else {
                venueEdit.setSelectedItem("(No Venue)");
            }
        } else {
            venueEdit.setSelectedItem("(No Venue)");
        }
        gbc.gridx = 1;
        formPanel.add(venueEdit, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String title = titleEdit.getText().trim();
                String description = descriptionEdit.getText().trim();
                String type = (String) typeEdit.getSelectedItem();
                String attendeesStr = attendeesEdit.getText().trim();
                String city = cityEdit.getText().trim();

                String startDate = startDateEdit.getText().trim();
                String startTime = startTimeEdit.getText().trim();
                String endDate = endDateEdit.getText().trim();
                String endTime = endTimeEdit.getText().trim();

                // Use ValidationHandler for centralized validation
                ValidationHandler.ValidationResult validationResult = validationService.validateEvent(
                        title, startDate, startTime, endDate, endTime, attendeesStr, true);

                if (!validationResult.isValid()) {
                    JOptionPane.showMessageDialog(editDialog, validationResult.getErrorMessage(),
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int estimatedAttendees = Integer.parseInt(attendeesStr);
                LocalDateTime startDateTime = validationService.parseDateTime(startDate, startTime);
                LocalDateTime endDateTime = validationService.parseDateTime(endDate, endTime);

                // Get selected venue ID
                Integer selectedVenueId = null;
                String selectedVenueName = (String) venueEdit.getSelectedItem();
                if (selectedVenueName != null && !selectedVenueName.equals("(No Venue)")) {
                    Venue selectedVenue = editVenueMap.get(selectedVenueName);
                    if (selectedVenue != null) {
                        selectedVenueId = selectedVenue.getId();
                    }
                }

                assert startDateTime != null;
                assert endDateTime != null;
                boolean success = facade.updateEvent(
                        eventId,
                        title,
                        description,
                        type != null ? type : "General",
                        startDateTime,
                        endDateTime,
                        selectedVenueId,
                        estimatedAttendees,
                        city
                );

                if (success) {
                    // Auto-save after updating event
                    if (dataPersistence != null) {
                        dataPersistence.saveAll();
                    }

                    JOptionPane.showMessageDialog(editDialog,
                            "Event updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    refreshEventList();
                } else {
                    JOptionPane.showMessageDialog(editDialog,
                            "Cannot update event - venue may not be available at the selected time",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(editDialog, ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Error updating event: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(formPanel, java.awt.BorderLayout.CENTER);
        editDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        editDialog.setVisible(true);
    }

    private void createEvent() {
        try {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            String estimatedAttendeesStr = estimatedAttendeesField.getText().trim();
            String city = cityField.getText().trim();
            String startDate = startDateField.getText().trim();
            String startTime = startTimeField.getText().trim();
            String endDate = endDateField.getText().trim();
            String endTime = endTimeField.getText().trim();

            // Check if all fields are empty
            if (title.isEmpty() && description.isEmpty() && estimatedAttendeesStr.isEmpty()
                    && startDate.isEmpty() && startTime.isEmpty() && endDate.isEmpty() && endTime.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in the fields to create an event",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use ValidationService for centralized validation
            ValidationHandler.ValidationResult validationResult = validationService.validateEvent(
                    title, startDate, startTime, endDate, endTime, estimatedAttendeesStr, true);

            if (!validationResult.isValid()) {
                JOptionPane.showMessageDialog(this, validationResult.getErrorMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int estimatedAttendees = Integer.parseInt(estimatedAttendeesStr);
            LocalDateTime startDateTime = validationService.parseDateTime(startDate, startTime);
            LocalDateTime endDateTime = validationService.parseDateTime(endDate, endTime);

            // Get selected venue ID from combo box
            Integer selectedVenueId = null;
            String selectedVenueName = (String) venueComboBox.getSelectedItem();
            if (selectedVenueName != null && !selectedVenueName.equals("(No Venue)")) {
                Venue selectedVenue = venueMap.get(selectedVenueName);
                if (selectedVenue != null) {
                    selectedVenueId = selectedVenue.getId();
                }
            }

            // Create event with selected venue ID (or null if no venue selected)
            assert endDateTime != null;
            assert startDateTime != null;
            Event event = facade.createEvent(title, description, type != null ? type : "General",
                    startDateTime, endDateTime, selectedVenueId, estimatedAttendees, city);

            if (event != null) {
                // Auto-save after creating event
                if (dataPersistence != null) {
                    dataPersistence.saveAll();
                }

                JOptionPane.showMessageDialog(this,
                        "Event created successfully!\nEstimated Attendees: " + estimatedAttendees,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshEventList();

                // Switch to Attendee Registration tab after creating event
                if (parentTabbedPane != null) {
                    parentTabbedPane.setSelectedIndex(1); // Attendee Registration tab is at index 1
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot create event",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating event: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row index to model row index (important for sorting)
        int modelRow = eventTable.convertRowIndexToModel(selectedRow);
        String eventId = (String) tableModel.getValueAt(modelRow, 0);
        Event event = facade.getEvent(eventId);

        if (event == null) {
            JOptionPane.showMessageDialog(this, "Event not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Count attendees registered to this event
        int attendeeCount = event.getAttendeeCount();
        String message = attendeeCount > 0
                ? "Are you sure you want to delete this event?\n" + attendeeCount + " attendee(s) will be unregistered from this event."
                : "Are you sure you want to delete this event?";

        int confirm = JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Unregister all attendees from this event (don't delete them)
            for (LogicHandling.Attendee attendee : event.getAttendees()) {
                facade.unregisterAttendeeFromEvent(attendee.getId(), eventId);
            }

            // Delete the event
            if (facade.removeEvent(eventId)) {
                String successMessage = attendeeCount > 0
                        ? "Event deleted successfully.\n" + attendeeCount + " attendee(s) unregistered."
                        : "Event deleted successfully";
                JOptionPane.showMessageDialog(this, successMessage,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshEventList();
            }
        }
    }

    private void viewSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to view",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row index to model row index (important for sorting)
        int modelRow = eventTable.convertRowIndexToModel(selectedRow);
        String eventId = (String) tableModel.getValueAt(modelRow, 0);
        Event event = facade.getEvent(eventId);

        if (event != null) {
            StringBuilder details = new StringBuilder();
            details.append(event.getDetailedInfo()).append("\n");
            details.append("\nRegistered Attendees:\n");
            if (event.getAttendees().isEmpty()) {
                details.append("  No attendees registered yet");
            } else {
                event.getAttendees().forEach(p ->
                        details.append("  - ").append(p.getName()).append(" (").append(p.getEmail()).append(")\n")
                );
            }

            JOptionPane.showMessageDialog(this, details.toString(),
                    "Event Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        typeComboBox.setSelectedIndex(0);
        venueComboBox.setSelectedIndex(0); // Reset to "(No Venue)"
        estimatedAttendeesField.setText("");
        cityField.setText("");
        startDateField.setText("");
        startTimeField.setText("");
        endDateField.setText("");
        endTimeField.setText("");
    }

    public void refreshEventList() {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Event event : facade.getAllEvents()) {
            String venueName = "No Venue";
            if (event.getVenueId() != null) {
                Venue venue = facade.getVenue(event.getVenueId());
                if (venue != null) {
                    venueName = venue.getName();
                }
            }
            tableModel.addRow(new Object[]{
                    event.getId(),
                    event.getTitle(),
                    event.getType(),
                    event.getStartTime().format(formatter),
                    event.getEndTime().format(formatter),
                    venueName,
                    event.getEstimatedAttendees(),
                    event.getAttendeeCount()
            });
        }
    }

    // Refresh venue list (call when venues are added/removed)
    public void refreshVenues() {
        refreshVenueComboBox();
    }


    // Set the parent tabbed pane to enable tab switching
    public void setParentTabbedPane(JTabbedPane tabbedPane) {
        this.parentTabbedPane = tabbedPane;
    }

    /**
     * Set the data persistence for auto-saving
     */
    public void setDataPersistence(LogicHandling.DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
    }
}

