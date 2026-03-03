package LogicHandling.HandlingServices

import LogicHandling.Event
import LogicHandling.RegistrationResult

// This service handles all registration stuff between attendees and events.
// It takes care of linking attendees to events and managing those relationships.
class RegistrationHandler(
    private val eventService: EventHandler,
    private val attendeeService: AttendeeService
) {
    fun registerAttendeeToEvent(attendeeId: String, eventId: String): RegistrationResult {
        val attendee = attendeeService.getAttendee(attendeeId)
            ?: return RegistrationResult.ATTENDEE_NOT_FOUND

        val event = eventService.getEvent(eventId)
            ?: return RegistrationResult.EVENT_NOT_FOUND

        return if (event.registerAttendee(attendee)) {
            RegistrationResult.SUCCESS
        } else {
            if (event.isFull()) {
                RegistrationResult.EVENT_FULL
            } else {
                RegistrationResult.ALREADY_REGISTERED
            }
        }
    }

    fun unregisterAttendeeFromEvent(attendeeId: String, eventId: String): Boolean {
        val event = eventService.getEvent(eventId) ?: return false
        return event.unregisterAttendee(attendeeId)
    }

    fun getEventsByAttendee(attendeeId: String): List<Event> {
        return eventService.getAllEvents().filter { it.isAttendeeRegistered(attendeeId) }
    }

    fun unregisterAttendeeFromAllEvents(attendeeId: String) {
        eventService.getAllEvents().forEach { it.unregisterAttendee(attendeeId) }
    }
}
