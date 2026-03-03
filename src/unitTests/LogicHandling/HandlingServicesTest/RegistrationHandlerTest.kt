package unitTests.LogicHandling.HandlingServicesTest


import LogicHandling.HandlingServices.AttendeeService
import LogicHandling.HandlingServices.EventHandler
import LogicHandling.HandlingServices.RegistrationHandler
import LogicHandling.RegistrationResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RegistrationHandlerTest {

    private lateinit var eventHandler: EventHandler
    private lateinit var attendeeService: AttendeeService
    private lateinit var registrationHandler: RegistrationHandler

    @BeforeEach
    fun setup() {
        try {
            eventHandler = EventHandler()
            attendeeService = AttendeeService()
            registrationHandler = RegistrationHandler(eventHandler, attendeeService)
        } catch (e: Exception) {
            fail("Test failed: Failed to initialize: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test register attendee successfully")
    fun testRegisterAttendeeSuccessfully() {
        println("\nTEST START: Testing register attendee successfully")
        try {
            val attendee = attendeeService.createAttendee("Lewis Hamilton", "lewis.hamilton@mercedesamg.com")
            val event = eventHandler.createEvent(
                title = "Monaco Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            assertNotNull(attendee)
            assertNotNull(event)

            val result = registrationHandler.registerAttendeeToEvent(attendee!!.id, event!!.id)

            assertEquals(RegistrationResult.SUCCESS, result)
            assertTrue(event.isAttendeeRegistered(attendee.id))
            println("Test passed: Attendee registered successfully. Count: ${event.getAttendeeCount()}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test register with non-existent attendee")
    fun testRegisterWithNonExistentAttendee() {
        println("\nTEST START: Testing register with non-existent attendee")
        try {
            val event = eventHandler.createEvent(
                title = "Singapore Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2)
            )

            assertNotNull(event)

            val result = registrationHandler.registerAttendeeToEvent("non-existent-id", event!!.id)

            assertEquals(RegistrationResult.ATTENDEE_NOT_FOUND, result)
            println("Test passed: Non-existent attendee detected: ATTENDEE_NOT_FOUND")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test register to full event")
    fun testRegisterToFullEvent() {
        println("\nTEST START: Testing register to full event")
        try {
            val attendee1 = attendeeService.createAttendee("Max Verstappen", "max.verstappen@redbullracing.com")
            val attendee2 = attendeeService.createAttendee("Sergio Perez", "sergio.perez@redbullracing.com")
            val attendee3 = attendeeService.createAttendee("Lando Norris", "lando.norris@mclaren.com")

            val event = eventHandler.createEvent(
                title = "Drivers Briefing",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 2
            )

            assertNotNull(event)

            registrationHandler.registerAttendeeToEvent(attendee1!!.id, event!!.id)
            registrationHandler.registerAttendeeToEvent(attendee2!!.id, event.id)

            val result = registrationHandler.registerAttendeeToEvent(attendee3!!.id, event.id)

            assertEquals(RegistrationResult.EVENT_FULL, result)
            assertEquals(2, event.getAttendeeCount())
            println("Test passed: Full event detected: EVENT_FULL")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test register already registered attendee")
    fun testRegisterAlreadyRegisteredAttendee() {
        println("\nTEST START: Testing register already registered attendee")
        try {
            val attendee = attendeeService.createAttendee("Charles Leclerc", "charles.leclerc@ferrari.com")
            val event = eventHandler.createEvent(
                title = "Italian Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            assertNotNull(attendee)
            assertNotNull(event)

            registrationHandler.registerAttendeeToEvent(attendee!!.id, event!!.id)
            val result = registrationHandler.registerAttendeeToEvent(attendee.id, event.id)

            assertEquals(RegistrationResult.ALREADY_REGISTERED, result)
            assertEquals(1, event.getAttendeeCount())
            println("Test passed: Already registered detected: ALREADY_REGISTERED")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test unregister attendee successfully")
    fun testUnregisterAttendeeSuccessfully() {
        println("\nTEST START: Testing unregister attendee successfully")
        try {
            val attendee = attendeeService.createAttendee("Fernando Alonso", "fernando.alonso@astonmartin.com")
            val event = eventHandler.createEvent(
                title = "Spanish Grand Prix",
                description = "",
                type = "General",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            assertNotNull(attendee)
            assertNotNull(event)

            registrationHandler.registerAttendeeToEvent(attendee!!.id, event!!.id)
            assertEquals(1, event.getAttendeeCount())

            val result = registrationHandler.unregisterAttendeeFromEvent(attendee.id, event.id)

            assertTrue(result)
            assertEquals(0, event.getAttendeeCount())
            println("Test passed: Attendee unregistered. Remaining count: ${event.getAttendeeCount()}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test get events by attendee")
    fun testGetEventsByAttendee() {
        println("\nTEST START: Testing get events by attendee")
        try {
            val attendee = attendeeService.createAttendee("George Russell", "george.russell@mercedesamg.com")
            assertNotNull(attendee)

            val event1 = eventHandler.createEvent(
                "British Grand Prix", "", "General",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            val event2 = eventHandler.createEvent(
                "Japanese Grand Prix", "", "General",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(2),
                estimatedAttendees = 10
            )

            registrationHandler.registerAttendeeToEvent(attendee!!.id, event1!!.id)
            registrationHandler.registerAttendeeToEvent(attendee.id, event2!!.id)

            val events = registrationHandler.getEventsByAttendee(attendee.id)

            assertEquals(2, events.size)
            assertTrue(events.any { it.id == event1.id })
            assertTrue(events.any { it.id == event2.id })
            println("Test passed: Retrieved ${events.size} events for attendee")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test unregister from all events")
    fun testUnregisterFromAllEvents() {
        println("\nTEST START: Testing unregister from all events")
        try {
            val attendee = attendeeService.createAttendee("Oscar Piastri", "oscar.piastri@mclaren.com")
            assertNotNull(attendee)

            val event1 = eventHandler.createEvent(
                "Canadian Grand Prix", "", "General",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                estimatedAttendees = 10
            )

            val event2 = eventHandler.createEvent(
                "Brazilian Grand Prix", "", "General",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(2),
                estimatedAttendees = 10
            )

            registrationHandler.registerAttendeeToEvent(attendee!!.id, event1!!.id)
            registrationHandler.registerAttendeeToEvent(attendee.id, event2!!.id)

            assertEquals(2, registrationHandler.getEventsByAttendee(attendee.id).size)

            registrationHandler.unregisterAttendeeFromAllEvents(attendee.id)

            assertEquals(0, registrationHandler.getEventsByAttendee(attendee.id).size)
            assertEquals(0, event1.getAttendeeCount())
            assertEquals(0, event2.getAttendeeCount())
            println("Test passed: Attendee unregistered from all events")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
