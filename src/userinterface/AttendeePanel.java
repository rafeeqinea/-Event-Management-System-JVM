package userinterface;

import LogicHandling.Attendee;
import LogicHandling.Event;
import LogicHandling.Venue;
import LogicHandling.HandlingServices.ValidationHandler;
import LogicHandling.HandlingServices.EventManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.Component;
import java.util.List;

// Panel for managing attendees
// UI layout is defined in AttendeePanel.form
public class AttendeePanel extends JPanel {
    // Components bound from .form file
    private JPanel mainPanel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField organizationField;
    private JComboBox<Event> eventComboBox;
    private JButton addButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton viewEventsButton;
    private JButton editDetailsButton;
    private JButton registerToEventButton;
    private JButton unregisterButton;
    private JTable attendeeTable;

    private final EventManager facade;
    private final ValidationHandler validationService;
    private DefaultTableModel tableModel;
    private LogicHandling.DataPersistence dataPersistence;

    public AttendeePanel(EventManager facade) {
        this.facade = facade;
        this.validationService = new ValidationHandler();

        // Check if form was compiled
        if (mainPanel == null) {
            System.err.println("ERROR: AttendeePanel form not compiled!");
            System.err.println("Please rebuild the project in IntelliJ:");
            System.err.println("1. Go to Build -> Rebuild Project");
            System.err.println("2. Or check Settings -> Build -> Compiler -> GUI Designer");

            JLabel errorLabel = new JLabel("<html><h2>Form not compiled!</h2>" +
                "<p>Please rebuild the project:</p>" +
                "<p>Build -> Rebuild Project</p></html>");
            setLayout(new java.awt.BorderLayout());
            add(errorLabel, java.awt.BorderLayout.CENTER);
            return;
        }

        // Add the mainPanel from the form to this panel
        setLayout(new java.awt.BorderLayout());
        add(mainPanel, java.awt.BorderLayout.CENTER);

        // Initialize table
        initializeTable();

        // Setup button listeners
        setupListeners();

        // Load data
        refreshAttendeeList();
        refreshEventComboBox();
    }

    private void initializeTable() {
        String[] columnNames = {"Select", "ID", "Name", "Email", "Phone", "Organization", "Events Registered"};
        tableModel = new DefaultTableModel(columnNames, 0) {

            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox column is editable
            }


            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class; // Checkbox column
                }
                return String.class;
            }
        };
        attendeeTable.setModel(tableModel);
        attendeeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Add table sorting functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        attendeeTable.setRowSorter(sorter);

        // Disable sorting on the checkbox column (column 0)
        sorter.setSortable(0, false);

        // Set default sort by Name (column 2) in ascending order
        List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Hide ID column
        attendeeTable.getColumnModel().getColumn(1).setMinWidth(0);
        attendeeTable.getColumnModel().getColumn(1).setMaxWidth(0);
        attendeeTable.getColumnModel().getColumn(1).setWidth(0);

        // Set checkbox column width
        attendeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        attendeeTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private void setupListeners() {
        if (addButton != null) addButton.addActionListener(e -> addAttendee());
        if (deleteButton != null) deleteButton.addActionListener(e -> deleteSelectedAttendee());
        if (clearButton != null) clearButton.addActionListener(e -> clearForm());
        if (viewEventsButton != null) viewEventsButton.addActionListener(e -> viewAttendeeEvents());
        if (editDetailsButton != null) editDetailsButton.addActionListener(e -> editSelectedAttendee());
        if (registerToEventButton != null) registerToEventButton.addActionListener(e -> registerToEvent());
        if (unregisterButton != null) unregisterButton.addActionListener(e -> unregisterFromEvents());
    }

    // Get list of row indices for all checked attendees
    private List<Integer> getCheckedRows() {
        List<Integer> checkedRows = new java.util.ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isChecked = (Boolean) tableModel.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                checkedRows.add(i);
            }
        }
        return checkedRows;
    }

    private void editSelectedAttendee() {
        List<Integer> checkedRows = getCheckedRows();

        if (checkedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select one attendee by checking the checkbox",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (checkedRows.size() > 1) {
            JOptionPane.showMessageDialog(this,
                "Please select only one attendee to edit",
                "Multiple Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = checkedRows.get(0);
        String attendeeId = (String) tableModel.getValueAt(row, 1);
        Attendee attendee = facade.getAttendee(attendeeId);

        if (attendee == null) {
            JOptionPane.showMessageDialog(this, "Attendee not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create edit dialog
        JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Attendee Details", true);
        editDialog.setLayout(new java.awt.BorderLayout(10, 10));
        editDialog.setSize(450, 300);
        editDialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new java.awt.GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        JTextField nameEdit = new JTextField(attendee.getName(), 20);
        gbc.gridx = 1;
        formPanel.add(nameEdit, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        JTextField emailEdit = new JTextField(attendee.getEmail(), 20);
        gbc.gridx = 1;
        formPanel.add(emailEdit, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        JTextField phoneEdit = new JTextField(attendee.getPhone(), 20);
        gbc.gridx = 1;
        formPanel.add(phoneEdit, gbc);

        // Organization
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Organization:"), gbc);
        JTextField organizationEdit = new JTextField(attendee.getOrganization(), 20);
        gbc.gridx = 1;
        formPanel.add(organizationEdit, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameEdit.getText().trim();
                String email = emailEdit.getText().trim();
                String phone = phoneEdit.getText().trim();
                String organization = organizationEdit.getText().trim();

                // Use ValidationService for centralized validation
                ValidationHandler.ValidationResult validationResult = validationService.validateAttendee(name, email, phone);

                if (!validationResult.isValid()) {
                    JOptionPane.showMessageDialog(editDialog, validationResult.getErrorMessage(),
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = facade.updateAttendee(
                    attendeeId,
                    name,
                    email,
                    phone,
                    organization
                );

                if (success) {
                    // Auto-save after updating attendee
                    if (dataPersistence != null) {
                        dataPersistence.saveAll();
                    }

                    JOptionPane.showMessageDialog(editDialog, "Attendee updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    refreshAttendeeList();
                } else { // Error message regarding reusing emails
                    JOptionPane.showMessageDialog(editDialog,
                        "Failed to update attendee - email may already be in use by another attendee",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(editDialog, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Error updating attendee: " + ex.getMessage(),
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

    private void addAttendee() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String organization = organizationField.getText().trim();

            // ValidationHandler for centralized validation
            ValidationHandler.ValidationResult validationResult = validationService.validateAttendee(name, email, phone);

            if (!validationResult.isValid()) {
                JOptionPane.showMessageDialog(this, validationResult.getErrorMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Attendee attendee = facade.createAttendee(name, email, phone, organization);

            if (attendee != null) {
                // Auto-save after adding attendee
                if (dataPersistence != null) {
                    dataPersistence.saveAll();
                }

                JOptionPane.showMessageDialog(this, "Attendee added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshAttendeeList();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to add attendee - email may already exist",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding attendee: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedAttendee() {
        List<Integer> checkedRows = getCheckedRows();

        if (checkedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one attendee by checking the checkbox",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check which attendees are registered to events
        List<String> cannotDeleteList = new java.util.ArrayList<>();
        List<String> canDeleteList = new java.util.ArrayList<>();

        for (int row : checkedRows) {
            String attendeeId = (String) tableModel.getValueAt(row, 1);
            String attendeeName = (String) tableModel.getValueAt(row, 2);

            List<Event> attendeeEvents = facade.getEventsByAttendee(attendeeId);

            if (!attendeeEvents.isEmpty()) {
                cannotDeleteList.add(attendeeName + " (registered to " + attendeeEvents.size() + " event(s))");
            } else {
                canDeleteList.add(attendeeId);
            }
        }

        // Show error if any attendees are registered to events
        if (!cannotDeleteList.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Cannot delete the following attendee(s) because they are registered to events:\n\n");
            for (String name : cannotDeleteList) {
                message.append("- ").append(name).append("\n");
            }
            message.append("\nPlease unregister them from all events first.");

            JOptionPane.showMessageDialog(this,
                message.toString(),
                "Cannot Delete",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // All selected attendees can be deleted
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + canDeleteList.size() + " attendee(s)?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            for (String attendeeId : canDeleteList) {
                if (facade.removeAttendee(attendeeId)) {
                    successCount++;
                }
            }

            JOptionPane.showMessageDialog(this,
                successCount + " attendee(s) deleted successfully",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAttendeeList();
        }
    }

    private void viewAttendeeEvents() {
        List<Integer> checkedRows = getCheckedRows();

        if (checkedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one attendee by checking the checkbox",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int row : checkedRows) {
            String attendeeId = (String) tableModel.getValueAt(row, 1);
            String attendeeName = (String) tableModel.getValueAt(row, 2);
            List<Event> events = facade.getEventsByAttendee(attendeeId);

            message.append("Events for ").append(attendeeName).append(":\n");
            if (events.isEmpty()) {
                message.append("  Not registered for any events\n");
            } else {
                for (Event event : events) {
                    message.append("  - ").append(event.getTitle()).append("\n");
                    message.append("    ").append(event.getFormattedTimeRange()).append("\n");
                    if (event.getVenueId() != null) {
                        Venue venue = facade.getVenue(event.getVenueId());
                        if (venue != null) {
                            message.append("    ").append(venue.getName()).append("\n");
                        }
                    }
                }
            }
            message.append("\n");
        }

        JOptionPane.showMessageDialog(this, message.toString(),
            "Attendee Events", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        organizationField.setText("");
    }

    public void refreshAttendeeList() {
        tableModel.setRowCount(0);

        for (Attendee attendee : facade.getAllAttendees()) {
            // Get events this attendee is registered to
            List<Event> attendeeEvents = facade.getEventsByAttendee(attendee.getId());
            String eventsRegistered = attendeeEvents.isEmpty() ? "None" :
                attendeeEvents.stream()
                    .map(Event::getTitle)
                    .collect(java.util.stream.Collectors.joining(", "));

            tableModel.addRow(new Object[]{
                Boolean.FALSE, // Checkbox (unchecked by default)
                attendee.getId(),
                attendee.getName(),
                attendee.getEmail(),
                attendee.getPhone().isEmpty() ? "-" : attendee.getPhone(),
                attendee.getOrganization().isEmpty() ? "-" : attendee.getOrganization(),
                eventsRegistered
            });
        }
    }

    private void registerToEvent() {
        Event event = (Event) eventComboBox.getSelectedItem();
        if (event == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an event",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> checkedRows = getCheckedRows();

        if (checkedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one attendee by checking the checkbox",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int successCount = 0;
        int alreadyRegisteredCount = 0;
        int fullCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        for (int row : checkedRows) {
            String attendeeId = (String) tableModel.getValueAt(row, 1);
            String attendeeName = (String) tableModel.getValueAt(row, 2);

            LogicHandling.RegistrationResult result = facade.registerAttendeeToEvent(attendeeId, event.getId());

            switch (result) {
                case SUCCESS:
                    successCount++;
                    break;
                case ALREADY_REGISTERED:
                    alreadyRegisteredCount++;
                    break;
                case EVENT_FULL:
                    fullCount++;
                    errorMessages.append(attendeeName).append(" - Event full\n");
                    break;
                case EVENT_NOT_FOUND:
                case ATTENDEE_NOT_FOUND:
                    errorMessages.append(attendeeName).append(" - Error\n");
                    break;
            }
        }

        // Build summary message
        StringBuilder message = new StringBuilder();
        if (successCount > 0) {
            message.append(successCount).append(" attendee(s) registered successfully!\n");
        }
        if (alreadyRegisteredCount > 0) {
            message.append(alreadyRegisteredCount).append(" attendee(s) already registered\n");
        }
        if (fullCount > 0) {
            message.append(fullCount).append(" attendee(s) could not be registered (event full)\n");
        }
        if (!errorMessages.isEmpty()) {
            message.append("\nErrors:\n").append(errorMessages);
        }

        // Auto-save after successful registrations
        if (successCount > 0 && dataPersistence != null) {
            dataPersistence.saveAll();
        }

        JOptionPane.showMessageDialog(this, message.toString(),
            successCount > 0 ? "Registration Complete" : "Registration Failed",
            successCount > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        refreshAttendeeList();
        refreshEventComboBox();
    }

    private void unregisterFromEvents() {
        List<Integer> checkedRows = getCheckedRows();

        if (checkedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one attendee by checking the checkbox",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Unregister " + checkedRows.size() + " attendee(s) from all their events?",
            "Confirm Unregistration",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int successCount = 0;
        for (int row : checkedRows) {
            String attendeeId = (String) tableModel.getValueAt(row, 1);

            // Get all events this attendee is registered to
            List<Event> attendeeEvents = facade.getEventsByAttendee(attendeeId);

            // Unregister from each event
            for (Event event : attendeeEvents) {
                if (facade.unregisterAttendeeFromEvent(attendeeId, event.getId())) {
                    successCount++;
                }
            }
        }

        JOptionPane.showMessageDialog(this,
            "Unregistered from " + successCount + " event(s) successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);

        refreshAttendeeList();
        refreshEventComboBox();
    }

    public void refreshEventComboBox() {
        eventComboBox.removeAllItems();

        // Add default "Select event" option
        eventComboBox.addItem(null); // This will be rendered as "Select event"

        for (Event event : facade.getAllEvents()) {
            eventComboBox.addItem(event);
        }

        // Set custom renderer to display "Select event" in bold for null item
        eventComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("<html><b>Select event</b></html>");
                } else {
                    setText(value.toString());
                }

                return this;
            }
        });
    }

    // Set the data persistence for auto-saving
    public void setDataPersistence(LogicHandling.DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
    }
}