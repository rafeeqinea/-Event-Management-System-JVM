package LogicHandling

import LogicHandling.HandlingServices.EventManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
  Data is stored into three  files to avoid duplication:
  1. venues.json: All venue data (child)
  2. attendees.json: All attendee data (child)
  3. events.json: Event data with references (IDs) to venues and attendees (Graph style: parent,child)
 */
class DataPersistence(private val facade: EventManager) {

    // File paths for JSON storage
    private val dataDirectory = File("data")
    private val venuesFile = File(dataDirectory, "venues.json")
    private val attendeesFile = File(dataDirectory, "attendees.json")
    private val eventsFile = File(dataDirectory, "events.json")

    /** Gson instance configured with:
    1. setPrettyPrinting(): Formats JSON with indentation for readability instead of storing as a string
    2. LocalDateTimeAdapter: Custom handler for Java 8 date/time types
     */
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    init {
        // Ensure the data directory/folder exists, if not it makes one
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs()
        }
    }

    // Checks for existing JSON files before initialization
    fun hasExistingData(): Boolean {
        return venuesFile.exists() || attendeesFile.exists() || eventsFile.exists()
    }

    // Saves any input data from UI fields into JSON
    fun saveAll() {
        try {
            saveVenues()
            saveAttendees()
            saveEvents()
            println("Data saved successfully to ${dataDirectory.absolutePath}")
        } catch (e: Exception) {
            println("Error saving data: ${e.message}")
            e.printStackTrace()
        }
    }

    // Loading all the JSON files
    fun loadAll() {
        try {
            loadVenues()
            loadAttendees()
            loadEvents()
            println("Data loaded successfully from ${dataDirectory.absolutePath}")
        } catch (e: Exception) {
            println("Error loading data: ${e.message}")
            e.printStackTrace()
        }
    }

    // ==================== Public Getters for Scala Integration ====================
    // Save venue details to JSON file
    private fun saveVenues() {
        val venues = facade.getAllVenues()
        venuesFile.writeText(gson.toJson(venues))
    }

    // Load venue details from JSON file
    private fun loadVenues() {
        if (!venuesFile.exists()) return

        val type = object : TypeToken<List<Venue>>() {}.type
        val venues: List<Venue> = gson.fromJson(venuesFile.readText(), type)
        venues.forEach { facade.addExistingVenue(it) }
    }

    // Save attendee details to JSON file
    private fun saveAttendees() {
        val attendees = facade.getAllAttendees()
        attendeesFile.writeText(gson.toJson(attendees))
    }

    // Load attendee details from JSON file
    private fun loadAttendees() {
        if (!attendeesFile.exists()) return

        val type = object : TypeToken<List<Attendee>>() {}.type
        val attendees: List<Attendee> = gson.fromJson(attendeesFile.readText(), type)
        attendees.forEach { facade.addAttendee(it) }
    }

    // Save events to JSON file
    private fun saveEvents() {
        val events = facade.getAllEvents()

        val eventsData = events.map { event ->
            EventData(
                id = event.id,
                title = event.title,
                description = event.description,
                type = event.type,
                startTime = event.startTime,
                endTime = event.endTime,
                venueId = event.venueId,
                estimatedAttendees = event.estimatedAttendees,
                attendeeIds = event.attendees.map { it.id }
            )
        }

        eventsFile.writeText(gson.toJson(eventsData))
    }

    // Load events from JSON file
    private fun loadEvents() {
        if (!eventsFile.exists()) return

        val type = object : TypeToken<List<EventData>>() {}.type
        val eventsData: List<EventData> = gson.fromJson(eventsFile.readText(), type)

        eventsData.forEach { eventData ->
            val event = facade.createEvent(
                title = eventData.title,
                description = eventData.description,
                type = eventData.type,
                startTime = eventData.startTime,
                endTime = eventData.endTime,
                venueId = eventData.venueId,
                estimatedAttendees = eventData.estimatedAttendees
            )

            // Register attendees to this event
            if (event != null) {
                eventData.attendeeIds.forEach { attendeeId ->
                    facade.registerAttendeeToEvent(attendeeId, event.id)
                }
            }
        }
    }
      // Serialization class for events - stores event data + IDs referencing venues/attendees
    // Uses IDs instead of full objects to avoid data duplication (like foreign keys)
    private data class EventData(
        val id: String,
        val title: String,
        val description: String,
        val type: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val venueId: Int?,            // Reference to venue (stored in venues.json)
        val estimatedAttendees: Int,
        val attendeeIds: List<String> // References to attendees (stored in attendees.json)
    )

    
    
      //Gson doesn't support Java date/time types, so we need convert LocalDateTime to JSON string

    private class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        // Convert LocalDateTime to JSON string
        override fun write(out: JsonWriter, value: LocalDateTime?) {
            if (value == null) {
                out.nullValue()
            } else {
                out.value(value.format(formatter))
            }
        }

        // Convert JSON string back to LocalDateTime for displaying(UI) in readable format
        override fun read(input: JsonReader): LocalDateTime? {
            val value = input.nextString()
            return if (value.isNullOrEmpty()) null else LocalDateTime.parse(value, formatter)
        }
    }
}