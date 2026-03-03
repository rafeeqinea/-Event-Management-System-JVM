package unitTests.LogicHandling.HandlingServicesTest

import LogicHandling.HandlingServices.EventManager
import LogicHandling.RegistrationResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventManagerTest {

    private lateinit var manager: EventManager

    @BeforeEach
    fun setup() {
        try {
            manager = EventManager()
        } catch (e: Exception) {
            fail("Test failed: Failed to initialize manager: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test create event through facade")
    fun testCreateEventThroughFacade() {
        println("\nTEST START: Testing create event through facade")
        try {
            val event = manager.createEvent(
                title = "Monaco Grand Prix",
                description = "Formula 1 street circuit race",
                type = "Conference",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                venueId = null,
                estimatedAttendees = 200,
                city = "Monte Carlo"
            )

            assertNotNull(event)
            assertEquals("Monaco Grand Prix", event?.title)
            println("Test passed: Event created via facade: ${event?.title}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test add venue through facade")
    fun testAddVenueThroughFacade() {
        println("\nTEST START: Testing add venue through facade")
        try {
            val venue = manager.addVenue(
                name = "Silverstone Circuit",
                address = "Circuit Dr",
                city = "Silverstone",
                capacity = 500,
                facilities = listOf("Pit Lane", "Paddock")
            )

            assertEquals("Silverstone Circuit", venue.name)
            assertEquals(500, venue.capacity)
            println("Test passed: Venue added via facade: ${venue.name}, capacity: ${venue.capacity}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test create attendee through facade")
    fun testCreateAttendeeThroughFacade() {
        println("\nTEST START: Testing create attendee through facade")
        try {
            val attendee = manager.createAttendee(
                name = "Lewis Hamilton",
                email = "lewis.hamilton@mercedesamg.com",
                phone = "+441234567890",
                organization = "Mercedes-AMG Petronas F1 Team"
            )

            assertNotNull(attendee)
            assertEquals("Lewis Hamilton", attendee?.name)
            println("Test passed: Attendee created via facade: ${attendee?.name}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test register attendee to event")
    fun testRegisterAttendeeToEvent() {
        println("\nTEST START: Testing register attendee to event")
        try {
            val attendee = manager.createAttendee("Max Verstappen", "max.verstappen@redbullracing.com")
            val event = manager.createEvent(
                title = "Austrian Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            val result = manager.registerAttendeeToEvent(attendee!!.id, event!!.id)

            assertEquals(RegistrationResult.SUCCESS, result)
            assertEquals(1, event.getAttendeeCount())
            println("Test passed: Registration successful. Event count: ${event.getAttendeeCount()}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test remove attendee unregisters from all events")
    fun testRemoveAttendeeUnregistersFromAllEvents() {
        println("\nTEST START: Testing remove attendee unregisters from all events")
        try {
            val attendee = manager.createAttendee("Charles Leclerc", "charles.leclerc@ferrari.com")
            val event = manager.createEvent(
                title = "Singapore Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            assertNotNull(attendee)
            assertNotNull(event)

            manager.registerAttendeeToEvent(attendee!!.id, event!!.id)
            assertEquals(1, event.getAttendeeCount())

            assertTrue(manager.removeAttendee(attendee.id))

            assertEquals(0, event.getAttendeeCount())
            assertNull(manager.getAttendee(attendee.id))
            println("Test passed: Attendee removed and unregistered from all events")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test remove venue fails when has events")
    fun testRemoveVenueFailsWhenHasEvents() {
        println("\nTEST START: Testing remove venue fails when has events")
        try {
            val venue = manager.addVenue("Monza Circuit", "Parco di Monza", "Monza", 100)

            manager.createEvent(
                title = "Italian Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                venueId = venue.id
            )

            assertFalse(manager.removeVenue(venue.id))
            assertNotNull(manager.getVenue(venue.id))
            println("Test passed: Venue with events protected from deletion")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test statistics methods")
    fun testStatisticsMethods() {
        println("\nTEST START: Testing statistics methods")
        try {
            assertEquals(0, manager.getTotalEventCount())
            assertEquals(0, manager.getTotalVenueCount())
            assertEquals(0, manager.getTotalAttendeeCount())

            manager.addVenue("Spa-Francorchamps", "Circuit de Spa", "Spa", 100)
            manager.createAttendee("Fernando Alonso", "fernando.alonso@astonmartin.com")
            manager.createEvent(
                "Belgian Grand Prix", "", "General",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2)
            )

            assertEquals(1, manager.getTotalEventCount())
            assertEquals(1, manager.getTotalVenueCount())
            assertEquals(1, manager.getTotalAttendeeCount())
            println("Test passed: Statistics: Events=${manager.getTotalEventCount()}, Venues=${manager.getTotalVenueCount()}, Attendees=${manager.getTotalAttendeeCount()}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test complete workflow through facade")
    fun testCompleteWorkflowThroughFacade() {
        println("\nTEST START: Testing complete workflow through facade")
        try {
            val venue = manager.addVenue("Suzuka Circuit", "Ino, Suzuka", "Suzuka", 100)
            val attendee1 = manager.createAttendee("Lando Norris", "lando.norris@mclaren.com")
            val attendee2 = manager.createAttendee("Oscar Piastri", "oscar.piastri@mclaren.com")

            val event = manager.createEvent(
                title = "Japanese Grand Prix",
                description = "F1 race at Suzuka",
                type = "Conference",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                venueId = venue.id,
                estimatedAttendees = 100,
                city = "Suzuka"
            )

            assertEquals(RegistrationResult.SUCCESS,
                manager.registerAttendeeToEvent(attendee1!!.id, event!!.id))
            assertEquals(RegistrationResult.SUCCESS,
                manager.registerAttendeeToEvent(attendee2!!.id, event.id))

            assertEquals(1, manager.getTotalVenueCount())
            assertEquals(2, manager.getTotalAttendeeCount())
            assertEquals(1, manager.getTotalEventCount())
            assertEquals(2, event.getAttendeeCount())
            println("Test passed: Complete workflow: Venue added, 2 attendees registered to event")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
