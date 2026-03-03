package LogicHandling.HandlingServices

import LogicHandling.*
import java.time.LocalDateTime

// A facade for UI to interact with rather than individual services (loose coupling between UI and logic)
class EventManager {
    private val venueService = VenueHandler()
    private val eventService = EventHandler()
    private val attendeeService = AttendeeService()
    private val registrationService = RegistrationHandler(eventService, attendeeService)
    private val statisticsService = StatisticsHandler(eventService, venueService, attendeeService)

    //====================Event Operations====================

    fun createEvent(
        title: String,
        description: String,
        type: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        venueId: Int? = null,
        estimatedAttendees: Int = 0,
        city: String = ""
    ): Event? = eventService.createEvent(title, description, type, startTime, endTime, venueId, estimatedAttendees, city)

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
    ): Boolean = eventService.updateEvent(eventId, title, description, type, startTime, endTime, venueId, estimatedAttendees, city)

    fun removeEvent(eventId: String): Boolean = eventService.removeEvent(eventId)
    fun getEvent(eventId: String): Event? = eventService.getEvent(eventId)
    fun getAllEvents(): List<Event> = eventService.getAllEvents()
    fun getUpcomingEvents(): List<Event> = eventService.getUpcomingEvents()
    fun getPastEvents(): List<Event> = eventService.getPastEvents()

    //====================Venue Operations====================

    fun addVenue(
        name: String,
        address: String,
        city: String,
        capacity: Int,
        facilities: List<String> = emptyList()
    ): Venue = venueService.addVenue(name, address, city, capacity, facilities)

    fun addExistingVenue(venue: Venue): Boolean = venueService.addExistingVenue(venue)

    fun removeVenue(venueId: Int): Boolean {
        val hasEvents = eventService.getEventsByVenue(venueId).isNotEmpty()
        return venueService.removeVenue(venueId, hasEvents)
    }

    fun updateVenue(
        venueId: Int,
        name: String,
        address: String,
        city: String,
        capacity: Int,
        facilities: List<String>
    ): Boolean = venueService.updateVenue(venueId, name, address, city, capacity, facilities)

    fun getVenue(venueId: Int): Venue? = venueService.getVenue(venueId)
    fun getAllVenues(): List<Venue> = venueService.getAllVenues()

    //====================Attendee Operations ==================

    fun createAttendee(
        name: String,
        email: String,
        phone: String = "",
        organization: String = ""
    ): Attendee? = attendeeService.createAttendee(name, email, phone, organization)

    fun addAttendee(attendee: Attendee): Boolean = attendeeService.addAttendee(attendee)

    fun removeAttendee(attendeeId: String): Boolean {
        registrationService.unregisterAttendeeFromAllEvents(attendeeId)
        return attendeeService.removeAttendee(attendeeId)
    }

    fun updateAttendee(
        attendeeId: String,
        name: String,
        email: String,
        phone: String,
        organization: String
    ): Boolean = attendeeService.updateAttendee(attendeeId, name, email, phone, organization)

    fun getAttendee(attendeeId: String): Attendee? = attendeeService.getAttendee(attendeeId)
    fun getAllAttendees(): List<Attendee> = attendeeService.getAllAttendees()

    // ===============Registration related operations==================

    fun registerAttendeeToEvent(attendeeId: String, eventId: String): RegistrationResult {
        return registrationService.registerAttendeeToEvent(attendeeId, eventId)
    }

    fun unregisterAttendeeFromEvent(attendeeId: String, eventId: String): Boolean {
        return registrationService.unregisterAttendeeFromEvent(attendeeId, eventId)
    }

    fun getEventsByAttendee(attendeeId: String): List<Event> {
        return registrationService.getEventsByAttendee(attendeeId)
    }

    // =========Used for statistics page============

    fun getTotalEventCount(): Int = statisticsService.getTotalEventCount()
    fun getTotalVenueCount(): Int = statisticsService.getTotalVenueCount()
    fun getTotalAttendeeCount(): Int = statisticsService.getTotalAttendeeCount()
    fun getAverageEventCapacity(): Double = statisticsService.getAverageEventCapacity()
    fun getAverageCapacityUtilization(): Double = statisticsService.getAverageCapacityUtilization()
    fun getEventCountByType(): Map<String, Int> = statisticsService.getEventCountByType()
    fun getMostPopularEventType(): String = statisticsService.getMostPopularEventType()
    fun getEventsThisWeek(): Int = statisticsService.getEventsThisWeek()
    fun getEventsThisMonth(): Int = statisticsService.getEventsThisMonth()
    fun getTotalVenueCapacity(): Int = statisticsService.getTotalVenueCapacity()
    fun getVenueWithMostEvents(): String = statisticsService.getVenueWithMostEvents()
    fun getTotalRegistrations(): Int = statisticsService.getTotalRegistrations()
    fun getAverageAttendeesPerEvent(): Double = statisticsService.getAverageAttendeesPerEvent()
    fun getMostRegisteredEvent(): String = statisticsService.getMostRegisteredEvent()
}