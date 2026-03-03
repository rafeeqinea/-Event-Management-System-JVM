package unitTests.LogicHandling.HandlingServicesTest

import LogicHandling.HandlingServices.EventHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventHandlerTest {

    private lateinit var service: EventHandler

    @BeforeEach
    fun setup() {
        try {
            service = EventHandler()
        } catch (e: Exception) {
            fail("Test failed: Failed to initialize service: ${e.message}")
        }
    }

    @Test
    fun testCreateEventSuccessfully() {
        println("TEST : Testing create event successfully")
        try {
            val startTime = LocalDateTime.now().plusDays(1)
            val endTime = startTime.plusHours(2)

            val event = service.createEvent(
                title = "Tech Conference",
                description = "Annual tech event",
                type = "Conference",
                startTime = startTime,
                endTime = endTime,
                venueId = 1,
                estimatedAttendees = 200,
                city = "London"
            )

            assertNotNull(event)
            assertEquals("Tech Conference", event?.title)
            println("Test passed: Event created successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testCreateEventWithVenueConflict() {
        println("TEST : Testing create event with venue conflict")
        try {
            val startTime = LocalDateTime.now().plusDays(1)
            val endTime = startTime.plusHours(2)

            val event1 = service.createEvent(
                title = "Event 1",
                description = "",
                type = "Conference",
                startTime = startTime,
                endTime = endTime,
                venueId = 1,
                estimatedAttendees = 100
            )

            assertNotNull(event1)

            val event2 = service.createEvent(
                title = "Event 2",
                description = "",
                type = "Conference",
                startTime = startTime.plusMinutes(30),
                endTime = endTime.plusMinutes(30),
                venueId = 1,
                estimatedAttendees = 100
            )

            assertNull(event2, "Should return null for conflicting venue")
            assertEquals(1, service.getAllEvents().size)
            println("Test passed: Venue conflict detected correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testRemoveEventSuccessfully() {
        println("TEST : Testing remove event successfully")
        try {
            val event = service.createEvent(
                title = "Test Event",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2)
            )

            assertNotNull(event)

            val result = service.removeEvent(event!!.id)

            assertTrue(result, "Should successfully remove event")
            assertEquals(0, service.getAllEvents().size)
            println("Test passed: Event removed successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testUpdateEventSuccessfully() {
        println("TEST : Testing update event successfully")
        try {
            val event = service.createEvent(
                title = "Old Title",
                description = "Old Description",
                type = "Old Type",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                venueId = 1,
                estimatedAttendees = 100,
                city = "Old City"
            )

            assertNotNull(event)

            val newStartTime = LocalDateTime.now().plusDays(2)
            val newEndTime = newStartTime.plusHours(3)

            val result = service.updateEvent(
                eventId = event!!.id,
                title = "New Title",
                description = "New Description",
                type = "New Type",
                startTime = newStartTime,
                endTime = newEndTime,
                venueId = 2,
                estimatedAttendees = 200,
                city = "New City"
            )

            assertTrue(result, "Should successfully update event")

            val updated = service.getEvent(event.id)
            assertEquals("New Title", updated?.title)
            println("Test passed: Event updated successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetUpcomingEvents() {
        println("TEST : Testing get upcoming events")
        try {
            val future = LocalDateTime.now().plusDays(1)

            service.createEvent(
                title = "Future Event",
                description = "",
                type = "General",
                startTime = future,
                endTime = future.plusHours(2)
            )

            val upcoming = service.getUpcomingEvents()

            assertEquals(1, upcoming.size)
            assertEquals("Future Event", upcoming[0].title)
            println("Test passed: Upcoming events filtered correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetPastEvents() {
        println("TEST: Testing get past events")
        try {
            val past = LocalDateTime.now().minusDays(1)

            service.createEvent(
                title = "Past Event",
                description = "",
                type = "General",
                startTime = past,
                endTime = past.plusHours(2)
            )

            val pastEvents = service.getPastEvents()

            assertEquals(1, pastEvents.size)
            assertEquals("Past Event", pastEvents[0].title)
            println("Test passed: Past events filtered correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testIsVenueAvailable() {
        println("TEST : Testing venue availability check")
        try {
            val startTime = LocalDateTime.now().plusDays(1)

            service.createEvent(
                title = "Event 1",
                description = "",
                type = "General",
                startTime = startTime,
                endTime = startTime.plusHours(2),
                venueId = 1
            )

            val isAvailable = service.isVenueAvailableForEvent(
                venueId = 1,
                startTime = startTime.plusMinutes(30),
                endTime = startTime.plusHours(3)
            )

            assertFalse(isAvailable, "Venue should not be available")
            println("Test passed: Venue availability check works correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetEventsByVenue() {
        println("TEST : Testing get events by venue")
        try {
            val now = LocalDateTime.now().plusDays(1)

            service.createEvent(
                title = "Event at Venue 1",
                description = "",
                type = "General",
                startTime = now,
                endTime = now.plusHours(2),
                venueId = 1
            )

            service.createEvent(
                title = "Another Event at Venue 1",
                description = "",
                type = "General",
                startTime = now.plusDays(1),
                endTime = now.plusDays(1).plusHours(2),
                venueId = 1
            )

            service.createEvent(
                title = "Event at Venue 2",
                description = "",
                type = "General",
                startTime = now,
                endTime = now.plusHours(2),
                venueId = 2
            )

            val venueEvents = service.getEventsByVenue(1)

            assertEquals(2, venueEvents.size)
            assertTrue(venueEvents.all { it.venueId == 1 })
            println("Test passed: Events filtered by venue correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
