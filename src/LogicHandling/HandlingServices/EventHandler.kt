package LogicHandling.HandlingServices

import LogicHandling.Event
import java.time.LocalDateTime

// This service handles all event operations like creating, updating, and deleting events
class EventHandler() {
    private val events: MutableList<Event> = mutableListOf()

    fun addEvent(event: Event): Boolean {
        val venueId = event.venueId
        if (venueId != null && !isVenueAvailableForEvent(venueId, event.startTime, event.endTime)) {
            return false
        }
        events.add(event)
        return true
    }

    fun createEvent(
        title: String,
        description: String,
        type: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        venueId: Int? = null,
        estimatedAttendees: Int = 0,
        city: String = ""
    ): Event? {
        val event = Event(
            title = title,
            description = description,
            type = type,
            startTime = startTime,
            endTime = endTime,
            venueId = venueId,
            estimatedAttendees = estimatedAttendees,
            city = city
        )
        return if (addEvent(event)) event else null
    }

    fun removeEvent(eventId: String): Boolean {
        return events.removeIf { it.id == eventId }
    }

    fun updateEvent(
        eventId: String,
        title: String,
        description: String,
        type: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        venueId: Int? = null,
        estimatedAttendees: Int,
        city: String = ""
    ): Boolean {
        val event = getEvent(eventId) ?: return false

        if (venueId != null) {
            val hasConflict = events.any {
                it.id != eventId &&
                        it.venueId == venueId &&
                        it.startTime < endTime &&
                        startTime < it.endTime
            }
            if (hasConflict) return false
        }

        event.title = title
        event.description = description
        event.type = type
        event.startTime = startTime
        event.endTime = endTime
        event.venueId = venueId
        event.estimatedAttendees = estimatedAttendees
        event.city = city

        return true
    }

    fun getEvent(eventId: String): Event? = events.find { it.id == eventId }

    fun getAllEvents(): List<Event> = events.toList()

    fun getUpcomingEvents(): List<Event> {
        val now = LocalDateTime.now()
        return events.filter { it.startTime.isAfter(now) }.sortedBy { it.startTime }
    }

    fun getPastEvents(): List<Event> {
        val now = LocalDateTime.now()
        return events.filter { it.endTime.isBefore(now) }.sortedByDescending { it.endTime }
    }

    fun isVenueAvailableForEvent(venueId: Int, startTime: LocalDateTime, endTime: LocalDateTime): Boolean {
        return !events.any { event ->
            event.venueId == venueId &&
                    event.startTime < endTime &&
                    startTime < event.endTime
        }
    }

    fun getEventsByVenue(venueId: Int): List<Event> {
        return events.filter { it.venueId == venueId }
    }

}