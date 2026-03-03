package LogicHandling.HandlingServices

import java.time.LocalDateTime

// This crunches all the numbers for stats and reports
// Basically the analytics department of the app
class StatisticsHandler(
    private val eventService: EventHandler,
    private val venueService: VenueHandler,
    private val attendeeService: AttendeeService
) {
    // Simple counting - how many of each thing do we have?
    fun getTotalEventCount(): Int = eventService.getAllEvents().size
    fun getTotalVenueCount(): Int = venueService.getAllVenues().size
    fun getTotalAttendeeCount(): Int = attendeeService.getAllAttendees().size

    // Figure out average venue capacity across all events
    fun getAverageEventCapacity(): Double {
        val events = eventService.getAllEvents()
        if (events.isEmpty()) return 0.0
        // Only look at events that actually have venues assigned
        val capacities = events.mapNotNull { event ->
            event.venueId?.let { venueService.getVenue(it)?.capacity }
        }
        return if (capacities.isEmpty()) 0.0 else capacities.average()
    }

    // See how full events are on average (as a percentage)
    fun getAverageCapacityUtilization(): Double {
        val events = eventService.getAllEvents()
        return if (events.isEmpty()) 0.0
        else events.map { event ->
            val venueCapacity = event.venueId?.let { venueService.getVenue(it)?.capacity }
            event.getCapacityUtilization(venueCapacity)
        }.average()
    }

    // Break down events by type (workshop, conference, etc.)
    fun getEventCountByType(): Map<String, Int> {
        return eventService.getAllEvents()
            .groupBy { it.type }
            .mapValues { it.value.size }
    }

    // Which type of event gets organized the most?
    fun getMostPopularEventType(): String {
        val events = eventService.getAllEvents()
        if (events.isEmpty()) return "N/A"
        return events.groupBy { it.type }
            .maxByOrNull { it.value.size }?.key ?: "N/A"
    }

    // Count events happening in the next 7 days
    fun getEventsThisWeek(): Int {
        val now = LocalDateTime.now()
        val weekEnd = now.plusDays(7)
        return eventService.getAllEvents().count {
            it.startTime.isAfter(now) && it.startTime.isBefore(weekEnd)
        }
    }

    // Count events happening in the next 30 days
    fun getEventsThisMonth(): Int {
        val now = LocalDateTime.now()
        val monthEnd = now.plusDays(30)
        return eventService.getAllEvents().count {
            it.startTime.isAfter(now) && it.startTime.isBefore(monthEnd)
        }
    }

    // Add up the max capacity of all venues combined
    fun getTotalVenueCapacity(): Int {
        return venueService.getAllVenues().sumOf { it.capacity }
    }

    // Which venue hosts the most events?
    fun getVenueWithMostEvents(): String {
        val events = eventService.getAllEvents()
        if (events.isEmpty()) return "N/A"
        // Group by venue ID and count how many times each appears
        val venueCounts = events.mapNotNull { event ->
            event.venueId?.let { venueService.getVenue(it) }
        }
            .groupBy { it.name }
            .mapValues { it.value.size }
        return venueCounts.maxByOrNull { it.value }?.key ?: "N/A"
    }

    // Total number of people registered across all events
    fun getTotalRegistrations(): Int {
        return eventService.getAllEvents().sumOf { it.getAttendeeCount() }
    }

    // On average, how many people sign up per event?
    fun getAverageAttendeesPerEvent(): Double {
        val events = eventService.getAllEvents()
        if (events.isEmpty()) return 0.0
        return events.map { it.getAttendeeCount() }.average()
    }

    // Which event has the most people registered?
    fun getMostRegisteredEvent(): String {
        val events = eventService.getAllEvents()
        if (events.isEmpty()) return "N/A"
        return events.maxByOrNull { it.getAttendeeCount() }?.title ?: "N/A"
    }
}
