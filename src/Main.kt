import LogicHandling.HandlingServices.EventManager
import LogicHandling.DataPersistence
import javax.swing.SwingUtilities
import javax.swing.UIManager


fun main() {
    // Makes the UI look like normal windows instead of ugly default java stuff
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // Initialize facade (single entry point for all business logic)
    val facade = EventManager()
    val persistence = DataPersistence(facade)

    // Load existing data if available
    if (persistence.hasExistingData()) {
        persistence.loadAll()
        println("Loaded existing data from files")
    }
    
    // Starts up the GUI window
    SwingUtilities.invokeLater {
        val mainWindow = userinterface.MainApplicationWindow(facade, persistence)
        mainWindow.isVisible = true
        
        // Saves everything when the user closes the app
        Runtime.getRuntime().addShutdownHook(Thread {
            persistence.saveAll()
            println("Data saved on application exit")
        })
    }
}