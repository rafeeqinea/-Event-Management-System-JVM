package userinterface;

import LogicHandling.HandlingServices.EventManager;
import LogicHandling.DataPersistence;

import javax.swing.*;
import java.awt.*;


public class MainApplicationWindow extends JFrame {
    // Components bound from .form file
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    private final EventManager facade;
    private final DataPersistence dataPersistence;
    private VenuePanel venuePanel;
    private EventPanel eventPanel;
    private AttendeePanel attendeePanel;
    private StatisticsPanel statisticsPanel;

    public MainApplicationWindow(EventManager facade, DataPersistence dataPersistence) {
        this.facade = facade;
        this.dataPersistence = dataPersistence;

        // Set up frame
        setTitle("Event Planning Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Use the mainPanel from the form
        setContentPane(mainPanel);

        // Initialize panels and add to tabbed pane
        initializePanels();

        // Create menu bar
        createMenuBar();

        // Setup listeners
        setupListeners();
    }

    private void initializePanels() {
        // Create panels with facade
        venuePanel = new VenuePanel(facade);
        eventPanel = new EventPanel(facade);
        attendeePanel = new AttendeePanel(facade);
        statisticsPanel = new StatisticsPanel(facade);

        // Set parent tabbed pane for EventPanel to enable auto-switching
        eventPanel.setParentTabbedPane(tabbedPane);

        // Set data persistence for auto-saving
        eventPanel.setDataPersistence(dataPersistence);
        attendeePanel.setDataPersistence(dataPersistence);
        venuePanel.setDataPersistence(dataPersistence);

        // Add to tabbed pane
        tabbedPane.addTab("Events", eventPanel);
        tabbedPane.addTab("Attendee Registration", attendeePanel);
        tabbedPane.addTab("Venue Registration", venuePanel);
        tabbedPane.addTab("Statistics", statisticsPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // View menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem statsItem = new JMenuItem("Statistics");
        statsItem.addActionListener(e -> {
            // Switch to Statistics tab
            tabbedPane.setSelectedComponent(statisticsPanel);
        });
        viewMenu.add(statsItem);

        menuBar.add(viewMenu);

        setJMenuBar(menuBar);
    }

    private void setupListeners() {
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected == eventPanel) {
                eventPanel.refreshEventList();
                eventPanel.refreshVenues();
            } else if (selected == venuePanel) {
                venuePanel.refreshVenueList();
            } else if (selected == attendeePanel) {
                attendeePanel.refreshAttendeeList();
                attendeePanel.refreshEventComboBox();
            } else if (selected == statisticsPanel) {
                statisticsPanel.refreshStatistics();
            }
        });
    }

}
