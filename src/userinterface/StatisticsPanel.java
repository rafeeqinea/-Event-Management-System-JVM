package userinterface;

import LogicHandling.HandlingServices.EventManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

// Panel used to display event statistics
public class StatisticsPanel extends JPanel {
    // Components  from .form file
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JPanel contentPanel;
    private JButton refreshButton;

    private final EventManager facade;

    // Overview labels
    private JLabel totalEventsLabel;
    private JLabel totalVenuesLabel;
    private JLabel totalAttendeesLabel;
    private JLabel upcomingEventsLabel;
    private JLabel pastEventsLabel;

    // Event metrics labels
    private JLabel mostPopularTypeLabel;
    private JLabel eventsThisWeekLabel;
    private JLabel eventsThisMonthLabel;

    // Venue metrics labels
    private JLabel totalVenueCapacityLabel;
    private JLabel venueWithMostEventsLabel;

    // Attendee metrics labels
    private JLabel totalRegistrationsLabel;
    private JLabel avgAttendeesPerEventLabel;
    private JLabel mostRegisteredEventLabel;

    // Capacity metrics labels
    private JLabel avgCapacityLabel;
    private JLabel avgUtilizationLabel;

    // Event types panel
    private JPanel eventTypesPanel;

    public StatisticsPanel(EventManager facade) {
        this.facade = facade;

        // Add the mainPanel from the form to this panel
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Initialize the content panels
        initializePanels();

        // Setup button listener
        refreshButton.addActionListener(e -> refreshStatistics());

        // Load initial data
        refreshStatistics();
    }

    private void initializePanels() {
        // Top-left: Overview
        contentPanel.add(createOverviewPanel());

        // Top-right: Event Metrics
        contentPanel.add(createEventMetricsPanel());

        // Bottom-left: Venue & Capacity Metrics
        contentPanel.add(createVenueMetricsPanel());

        // Bottom-right: Attendee Metrics & Event Types
        contentPanel.add(createAttendeeMetricsPanel());
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Overview"));
        GridBagConstraints gbc = createGbc();

        int row = 0;
        totalEventsLabel = addStatRow(panel, gbc, row++, "Total Events:");
        totalVenuesLabel = addStatRow(panel, gbc, row++, "Total Venues:");
        totalAttendeesLabel = addStatRow(panel, gbc, row++, "Total Attendees:");
        upcomingEventsLabel = addStatRow(panel, gbc, row++, "Upcoming Events:");
        pastEventsLabel = addStatRow(panel, gbc, row++, "Past Events:");

        // Add filler
        gbc.gridy = row;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createEventMetricsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Event Metrics"));
        GridBagConstraints gbc = createGbc();

        int row = 0;
        mostPopularTypeLabel = addStatRow(panel, gbc, row++, "Most Popular Type:");
        eventsThisWeekLabel = addStatRow(panel, gbc, row++, "Events This Week:");
        eventsThisMonthLabel = addStatRow(panel, gbc, row++, "Events This Month:");

        // Add filler
        gbc.gridy = row;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createVenueMetricsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Venue & Capacity Metrics"));
        GridBagConstraints gbc = createGbc();

        int row = 0;
        totalVenueCapacityLabel = addStatRow(panel, gbc, row++, "Total Venue Capacity:");
        venueWithMostEventsLabel = addStatRow(panel, gbc, row++, "Venue with Most Events:");
        avgCapacityLabel = addStatRow(panel, gbc, row++, "Avg Event Capacity:");
        avgUtilizationLabel = addStatRow(panel, gbc, row++, "Avg Utilization:");

        // Add filler
        gbc.gridy = row;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createAttendeeMetricsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new TitledBorder("Attendee Metrics & Event Types"));

        // Attendee metrics at top
        JPanel attendeePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        int row = 0;
        totalRegistrationsLabel = addStatRow(attendeePanel, gbc, row++, "Total Registrations:");
        avgAttendeesPerEventLabel = addStatRow(attendeePanel, gbc, row++, "Avg Attendees/Event:");
        mostRegisteredEventLabel = addStatRow(attendeePanel, gbc, row++, "Most Registered Event:");

        panel.add(attendeePanel, BorderLayout.NORTH);

        // Event types distribution
        eventTypesPanel = new JPanel(new GridBagLayout());
        eventTypesPanel.setBorder(BorderFactory.createTitledBorder("Event Types Distribution"));
        panel.add(eventTypesPanel, BorderLayout.CENTER);

        return panel;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 8, 3, 8);
        return gbc;
    }

    private JLabel addStatRow(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel nameLabel = new JLabel(labelText);
        panel.add(nameLabel, gbc);

        // Value
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD));
        panel.add(valueLabel, gbc);

        return valueLabel;
    }

    public void refreshStatistics() {
        // Overview
        totalEventsLabel.setText(String.valueOf(facade.getTotalEventCount()));
        totalVenuesLabel.setText(String.valueOf(facade.getTotalVenueCount()));
        totalAttendeesLabel.setText(String.valueOf(facade.getTotalAttendeeCount()));
        upcomingEventsLabel.setText(String.valueOf(facade.getUpcomingEvents().size()));
        pastEventsLabel.setText(String.valueOf(facade.getPastEvents().size()));

        // Event metrics
        mostPopularTypeLabel.setText(facade.getMostPopularEventType());
        eventsThisWeekLabel.setText(String.valueOf(facade.getEventsThisWeek()));
        eventsThisMonthLabel.setText(String.valueOf(facade.getEventsThisMonth()));

        // Venue metrics
        totalVenueCapacityLabel.setText(String.valueOf(facade.getTotalVenueCapacity()));
        venueWithMostEventsLabel.setText(facade.getVenueWithMostEvents());
        avgCapacityLabel.setText(String.format("%.1f", facade.getAverageEventCapacity()));
        avgUtilizationLabel.setText(String.format("%.1f%%", facade.getAverageCapacityUtilization()));

        // Attendee metrics
        totalRegistrationsLabel.setText(String.valueOf(facade.getTotalRegistrations()));
        avgAttendeesPerEventLabel.setText(String.format("%.1f", facade.getAverageAttendeesPerEvent()));
        mostRegisteredEventLabel.setText(facade.getMostRegisteredEvent());

        // Update event types distribution
        updateEventTypesPanel();
    }

    private void updateEventTypesPanel() {
        eventTypesPanel.removeAll();
        GridBagConstraints gbc = createGbc();

        Map<String, Integer> eventTypes = facade.getEventCountByType();
        int row = 0;

        for (Map.Entry<String, Integer> entry : eventTypes.entrySet()) {
            // Type label
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.0;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel typeLabel = new JLabel(entry.getKey() + ":");
            eventTypesPanel.add(typeLabel, gbc);

            // Count label
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.EAST;
            JLabel countLabel = new JLabel(String.valueOf(entry.getValue()));
            countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD));
            eventTypesPanel.add(countLabel, gbc);

            row++;
        }

        if (eventTypes.isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            eventTypesPanel.add(new JLabel("No events yet"), gbc);
        }

        eventTypesPanel.revalidate();
        eventTypesPanel.repaint();
    }
}
