package LogicHandling

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter //added a new formated time range
import java.util.*


class Event(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var description: String = "",
    var type: String = "General",
    var startTime: LocalDateTime,
    var endTime: LocalDateTime,
    var venueId: Int? = null, // Store venue ID instead of entire Venue object
    var estimatedAttendees: Int = 0, //new property added here, field for planning vs actual attendance
    var city: String = "" // City where the event takes place
) {
    // The system keeps the actual list private and only shares a copy with readers
    private val _attendees: MutableList<Attendee> = mutableListOf()
    val attendees: List<Attendee>
        get() = _attendees.toList()

    // The title must have at least one character to be considered valid
    init {
        require(title.isNotBlank()) { "Event title cannot be blank" }
        require(endTime.isAfter(startTime)) { "End time must be after start time" } // now using idiomatic require instead of manual if + throw
    }

    // This method registers an attendee to the event and returns true if successful
    // Note: capacity needs to be checked externally by passing venue capacity or using estimatedAttendees
    fun registerAttendee(attendee: Attendee, venueCapacity: Int? = null): Boolean {
        val capacity = venueCapacity ?: estimatedAttendees
        if (_attendees.size >= capacity) return false
        if (_attendees.any { it.id == attendee.id }) return false
        _attendees.add(attendee)
        return true
    }


    // This method removes an attendee from the event using their ID
    fun unregisterAttendee(attendeeId: String): Boolean {
        return _attendees.removeIf { it.id == attendeeId } // using removeIf now instead of manual iterator loop
    }

    // This checks whether the event has reached its maximum capacity
    fun isFull(venueCapacity: Int? = null): Boolean {
        val capacity = venueCapacity ?: estimatedAttendees
        return _attendees.size >= capacity
    }

    // This returns how many attendees are currently registered
    fun getAttendeeCount(): Int = _attendees.size



    // This checks if a specific attendee is already registered for the event
    fun isAttendeeRegistered(attendeeId: String): Boolean {
        return _attendees.any { it.id == attendeeId }
    }

    // This calculates how full the event is as a percentage of total capacity
    fun getCapacityUtilization(venueCapacity: Int? = null): Double {
        val capacity = venueCapacity ?: estimatedAttendees
        return if (capacity > 0) {
            (_attendees.size.toDouble() / capacity) * 100
        } else 0.0
    }

    // This formats the start and end times into a readable string
    fun getFormattedTimeRange(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return "${startTime.format(formatter)} - ${endTime.format(formatter)}"
    }

    override fun toString(): String {
        val venueInfo = if (venueId != null) "Venue ID: $venueId" else "No venue assigned"
        return "$title - $venueInfo (${getFormattedTimeRange()})"
    }

    // This builds a detailed summary of the event including venue information
    fun getDetailedInfo(): String {
        return buildString {
            appendLine("Event: $title")
            appendLine("Type: $type")
            appendLine("Description: ${if (description.isNotBlank()) description else "No description"}")
            appendLine("Time: ${getFormattedTimeRange()}")
            appendLine("City: ${if (city.isNotBlank()) city else "No city specified"}")
            appendLine("Venue ID: ${venueId ?: "No venue assigned"}")
            appendLine("Estimated Attendees: $estimatedAttendees")
            appendLine("Registered Attendees: ${_attendees.size}")
        }
    }
}
