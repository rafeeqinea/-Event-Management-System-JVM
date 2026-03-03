package userinterface;

import LogicHandling.HandlingServices.ValidationHandler;
import LogicHandling.Venue;
import LogicHandling.HandlingServices.EventManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for managing venues
 * The UI layout is defined in VenuePanel.form
 */
public class VenuePanel extends JPanel {
    // Components bound from .form file
    private JPanel mainPanel;
    private JTextField nameField;
    private JTextField cityField;
    private JTextField addressField;
    private JTextField capacityField;
    private JTextArea facilitiesArea;
    private JScrollPane facilitiesScrollPane;
    private JButton addButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton viewButton;
    private JButton editDetailsButton;
    private JTable venueTable;
    private JScrollPane tableScrollPane;

    private final EventManager facade;
    private final ValidationHandler validationService;
    private DefaultTableModel tableModel;
    private LogicHandling.DataPersistence dataPersistence;

    public VenuePanel(EventManager facade) {
        this.facade = facade;
        this.validationService = new ValidationHandler();

        // Check if form was compiled
        if (mainPanel == null) {
            System.err.println("ERROR: VenuePanel form not compiled!");
            System.err.println("Please rebuild the project in IntelliJ:");
            System.err.println("1. Go to Build -> Rebuild Project");
            System.err.println("2. Or check Settings -> Build -> Compiler -> GUI Designer");

            JLabel errorLabel = new JLabel("<html><h2>Form not compiled!</h2>" +
                "<p>Please rebuild the project:</p>" +
                "<p>Build → Rebuild Project</p></html>");
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
        refreshVenueList();
    }
    
    private void initializeTable() {
        String[] columnNames = {"ID", "Name", "City", "Address", "Capacity", "Facilities"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        venueTable.setModel(tableModel);
        venueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add table sorting functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        venueTable.setRowSorter(sorter);

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

        // Apply numeric comparator to ID (column 0) and Capacity (column 4)
        sorter.setComparator(0, numericComparator);
        sorter.setComparator(4, numericComparator);

        // Set default sort by Name (column 1) in ascending order
        List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
    }
    
    private void setupListeners() {
        addButton.addActionListener(e -> addVenue());
        deleteButton.addActionListener(e -> deleteSelectedVenue());
        clearButton.addActionListener(e -> clearForm());
        viewButton.addActionListener(e -> viewSelectedVenue());
        editDetailsButton.addActionListener(e -> editSelectedVenue());
    }

    private void editSelectedVenue() {
        int selectedRow = venueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a venue to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row index to model row index (important for sorting)
        int modelRow = venueTable.convertRowIndexToModel(selectedRow);
        int venueId = (Integer) tableModel.getValueAt(modelRow, 0);
        Venue venue = facade.getVenue(venueId);

        if (venue == null) {
            JOptionPane.showMessageDialog(this, "Venue not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create edit dialog
        JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Venue Details", true);
        editDialog.setLayout(new java.awt.BorderLayout(10, 10));
        editDialog.setSize(450, 400);
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
        JTextField nameEdit = new JTextField(venue.getName(), 20);
        gbc.gridx = 1;
        formPanel.add(nameEdit, gbc);

        // City
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("City:"), gbc);
        JTextField cityEdit = new JTextField(venue.getCity(), 20);
        gbc.gridx = 1;
        formPanel.add(cityEdit, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        JTextField addressEdit = new JTextField(venue.getAddress(), 20);
        gbc.gridx = 1;
        formPanel.add(addressEdit, gbc);

        // Capacity
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Capacity:"), gbc);
        JTextField capacityEdit = new JTextField(String.valueOf(venue.getCapacity()), 20);
        gbc.gridx = 1;
        formPanel.add(capacityEdit, gbc);

        // Facilities
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Facilities (one per line):"), gbc);
        JTextArea facilitiesEdit = new JTextArea(String.join("\n", venue.getFacilities()), 5, 20);
        facilitiesEdit.setLineWrap(true);
        JScrollPane facilitiesScrollPane = new JScrollPane(facilitiesEdit);
        gbc.gridx = 1;
        formPanel.add(facilitiesScrollPane, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameEdit.getText().trim();
                String city = cityEdit.getText().trim();
                String address = addressEdit.getText().trim();
                String capacityText = capacityEdit.getText().trim();

                // Centralized validation handling to prevent reusing same codes
                ValidationHandler.ValidationResult validationResult = validationService.validateVenue(name, address, city, capacityText);

                if (!validationResult.isValid()) {
                    JOptionPane.showMessageDialog(editDialog, validationResult.getErrorMessage(),
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int capacity = Integer.parseInt(capacityText);

                List<String> facilities = Arrays.stream(facilitiesEdit.getText().split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

                boolean success = facade.updateVenue(venueId, name, address, city, capacity, facilities);

                if (success) {
                    // Auto-save after updating venue
                    if (dataPersistence != null) {
                        dataPersistence.saveAll();
                    }

                    JOptionPane.showMessageDialog(editDialog, "Venue updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    refreshVenueList();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Cannot update venue",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(editDialog, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Error updating venue: " + ex.getMessage(),
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

    private void addVenue() {
        try {
            String name = nameField.getText().trim();
            String city = cityField.getText().trim();
            String address = addressField.getText().trim();
            String capacityText = capacityField.getText().trim();

            // Centralized validation
            ValidationHandler.ValidationResult validationResult = validationService.validateVenue(name, address, city, capacityText);

            if (!validationResult.isValid()) {
                JOptionPane.showMessageDialog(this, validationResult.getErrorMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacity = Integer.parseInt(capacityText);

            List<String> facilities = Arrays.stream(facilitiesArea.getText().split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

            facade.addVenue(name, address, city, capacity, facilities);

            // Auto-save after adding venue
            if (dataPersistence != null) {
                dataPersistence.saveAll();
            }

            JOptionPane.showMessageDialog(this, "Venue added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshVenueList();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding venue: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedVenue() {
        int selectedRow = venueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a venue to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row index to model row index (important for sorting)
        int modelRow = venueTable.convertRowIndexToModel(selectedRow);
        int venueId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this venue?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (facade.removeVenue(venueId)) {
                JOptionPane.showMessageDialog(this, "Venue deleted successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshVenueList();
            } else {
                JOptionPane.showMessageDialog(this, "Cannot delete venue with existing events", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewSelectedVenue() {
        int selectedRow = venueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a venue to view",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row index to model row index (important for sorting)
        int modelRow = venueTable.convertRowIndexToModel(selectedRow);
        int venueId = (Integer) tableModel.getValueAt(modelRow, 0);
        Venue venue = facade.getVenue(venueId);
        
        if (venue != null) {
            JOptionPane.showMessageDialog(this, venue.getDetailedInfo(), 
                "Venue Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        cityField.setText("");
        addressField.setText("");
        capacityField.setText("");
        facilitiesArea.setText("");
    }
    
    public void refreshVenueList() {
        tableModel.setRowCount(0);

        for (Venue venue : facade.getAllVenues()) {
            tableModel.addRow(new Object[]{
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getAddress(),
                venue.getCapacity(),
                String.join(", ", venue.getFacilities())
            });
        }
    }

    /**
     * Set the data persistence for auto-saving
     */
    public void setDataPersistence(LogicHandling.DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
    }
}
