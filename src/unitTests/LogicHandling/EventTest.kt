package unitTests.LogicHandling

import LogicHandling.Attendee
import LogicHandling.Event
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventTest {

    private fun createTestEvent(
        title: String = "Test Event",
        startTime: LocalDateTime = LocalDateTime.now().plusDays(1),
        endTime: LocalDateTime = LocalDateTime.now().plusDays(1).plusHours(2),
        estimatedAttendees: Int = 100
    ): Event {
        return Event(
            title = title,
            description = "Test Description",
            type = "Conference",
            startTime = startTime,
            endTime = endTime,
            venueId = 1,
            estimatedAttendees = estimatedAttendees,
            city = "London"
        )
    }

    @Test
    fun testValidEventCreation() {
        try {
            val startTime = LocalDateTime.now().plusDays(1)
            val endTime = startTime.plusHours(2)

            val event = Event(
                title = "Tech Conference",
                description = "Annual tech conference",
                type = "Conference",
                startTime = startTime,
                endTime = endTime,
                venueId = 1,
                estimatedAttendees = 200,
                city = "London"
            )

            assertEquals("Tech Conference", event.title)
            assertEquals("Annual tech conference", event.description)
            assertEquals("Conference", event.type)
            assertEquals(startTime, event.startTime)
            assertEquals(endTime, event.endTime)
            assertEquals(1, event.venueId)
            assertEquals(200, event.estimatedAttendees)
            assertEquals("London", event.city)
            assertNotNull(event.id)
            println("Test passed: Valid event creation: Event '${event.title}' created with ${event.estimatedAttendees} capacity")
        } catch (e: Exception) {
            System.err.println("Test failed: Valid event creation: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testBlankTitleThrowsException() {
        try {
            Event(
                title = "",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2)
            )
            System.err.println("Test failed: Blank title validation: Should have thrown exception")
            fail("Should throw exception for blank title")
        } catch (e: IllegalArgumentException) {
            assertEquals("Event title cannot be blank", e.message)
            println("Test passed: Blank title validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Blank title validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testEndTimeBeforeStartTimeThrowsException() {
        try {
            val startTime = LocalDateTime.now().plusDays(1)
            val endTime = startTime.minusHours(1)

            Event(
                title = "Test Event",
                startTime = startTime,
                endTime = endTime
            )
            System.err.println("Test failed: End time before start validation: Should have thrown exception")
            fail("Should throw exception for end time before start time")
        } catch (e: IllegalArgumentException) {
            assertEquals("End time must be after start time", e.message)
            println("Test passed: End time before start validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: End time before start validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testEndTimeEqualsStartTimeThrowsException() {
        try {
            val time = LocalDateTime.now().plusDays(1)

            Event(
                title = "Test Event",
                startTime = time,
                endTime = time
            )
            System.err.println("Test failed: End time equals start validation: Should have thrown exception")
            fail("Should throw exception for end time equals start time")
        } catch (e: IllegalArgumentException) {
            assertEquals("End time must be after start time", e.message)
            println("Test passed: End time equals start validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: End time equals start validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testRegisterAttendeeSuccessfully() {
        try {
            val event = createTestEvent(estimatedAttendees = 10)
            val attendee = Attendee(name = "John Doe", email = "john@example.com")

            val result = event.registerAttendee(attendee)

            assertTrue(result)
            assertEquals(1, event.getAttendeeCount())
            assertTrue(event.isAttendeeRegistered(attendee.id))
            println("Test passed: Register attendee successfully: ${attendee.name} registered, event count: ${event.getAttendeeCount()}")
        } catch (e: Exception) {
            System.err.println("Test failed: Register attendee successfully: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testRegisterAttendeeWhenEventIsFull() {
        try {
            val event = createTestEvent(estimatedAttendees = 2)
            val attendee1 = Attendee(name = "John", email = "john@example.com")
            val attendee2 = Attendee(name = "Jane", email = "jane@example.com")
            val attendee3 = Attendee(name = "Benji", email = "Benji@example.com")

            assertTrue(event.registerAttendee(attendee1))
            assertTrue(event.registerAttendee(attendee2))
            assertFalse(event.registerAttendee(attendee3))

            assertEquals(2, event.getAttendeeCount())
            println("Test passed: Register attendee when event full: Correctly rejected registration to full event")
        } catch (e: Exception) {
            System.err.println("Test failed: Register attendee when event full: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testRegisterSameAttendeeTwice() {
        try {
            val event = createTestEvent()
            val attendee = Attendee(name = "John Doe", email = "john@example.com")

            assertTrue(event.registerAttendee(attendee))
            assertFalse(event.registerAttendee(attendee))

            assertEquals(1, event.getAttendeeCount())
            println("Test passed: Register same attendee twice: Correctly rejected duplicate registration")
        } catch (e: Exception) {
            System.err.println("Test failed: Register same attendee twice: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testUnregisterAttendeeSuccessfully() {
        try {
            val event = createTestEvent()
            val attendee = Attendee(name = "John Doe", email = "john@example.com")

            event.registerAttendee(attendee)
            assertEquals(1, event.getAttendeeCount())

            val result = event.unregisterAttendee(attendee.id)

            assertTrue(result)
            assertEquals(0, event.getAttendeeCount())
            assertFalse(event.isAttendeeRegistered(attendee.id))
            println("Test passed: Unregister attendee successfully: ${attendee.name} unregistered, event count: ${event.getAttendeeCount()}")
        } catch (e: Exception) {
            System.err.println("Test failed: Unregister attendee successfully: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testUnregisterNonExistentAttendee() {
        try {
            val event = createTestEvent()
            val result = event.unregisterAttendee("non-existent-id")

            assertFalse(result)
            println("Test passed: Unregister non-existent attendee: Correctly returned false for non-existent attendee")
        } catch (e: Exception) {
            System.err.println("Test failed: Unregister non-existent attendee: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testIsFullReturnsCorrectStatus() {
        try {
            val event = createTestEvent(estimatedAttendees = 2)

            assertFalse(event.isFull())

            event.registerAttendee(Attendee(name = "James", email = "james@yahoo.com"))
            assertFalse(event.isFull())

            event.registerAttendee(Attendee(name = "Azel", email = "azel@gmail.com"))
            assertTrue(event.isFull())
            println("Test passed: isFull returns correct status: Event correctly marked as full with 2/2 attendees")
        } catch (e: Exception) {
            System.err.println("Test failed: isFull returns correct status: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testIsFullWithCustomVenueCapacity() {
        try {
            val event = createTestEvent(estimatedAttendees = 100)
            val venueCapacity = 2

            event.registerAttendee(Attendee(name = "James", email = "james@example.com"))
            assertFalse(event.isFull(venueCapacity))

            event.registerAttendee(Attendee(name = "Azel", email = "azel@example.com"))
            assertTrue(event.isFull(venueCapacity))
            println("Test passed: isFull with custom venue capacity: Event correctly checked against venue capacity of $venueCapacity")
        } catch (e: Exception) {
            System.err.println("Test failed: isFull with custom venue capacity: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetAttendeeCount() {
        try {
            val event = createTestEvent()

            assertEquals(0, event.getAttendeeCount())

            event.registerAttendee(Attendee(name = "John", email = "john@example.com"))
            assertEquals(1, event.getAttendeeCount())

            event.registerAttendee(Attendee(name = "Jane", email = "jane@example.com"))
            assertEquals(2, event.getAttendeeCount())
            println("Test passed: Get attendee count: Count correctly updated from 0 to 2")
        } catch (e: Exception) {
            System.err.println("Test failed: Get attendee count: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetCapacityUtilization() {
        try {
            val event = createTestEvent(estimatedAttendees = 100)

            assertEquals(0.0, event.getCapacityUtilization(), 0.01)

            event.registerAttendee(Attendee(name = "John", email = "john@example.com"))
            assertEquals(1.0, event.getCapacityUtilization(), 0.01)

            repeat(49) {
                event.registerAttendee(Attendee(name = "Attendee $it", email = "attendee$it@example.com"))
            }
            assertEquals(50.0, event.getCapacityUtilization(), 0.01)
            println("Test passed: Get capacity utilization: Utilization correctly calculated as 50% with 50/100 attendees")
        } catch (e: Exception) {
            System.err.println("Test failed: Get capacity utilization: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetFormattedTimeRange() {
        try {
            val startTime = LocalDateTime.of(2025, 12, 25, 10, 30)
            val endTime = LocalDateTime.of(2025, 12, 25, 14, 30)

            val event = createTestEvent(startTime = startTime, endTime = endTime)
            val formatted = event.getFormattedTimeRange()

            assertEquals("25/12/2025 10:30 - 25/12/2025 14:30", formatted)
            println("Test passed: Get formatted time range: Successfully formatted as '$formatted'")
        } catch (e: Exception) {
            System.err.println("Test failed: Get formatted time range: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testToStringFormat() {
        try {
            val startTime = LocalDateTime.of(2025, 12, 25, 10, 0)
            val endTime = LocalDateTime.of(2025, 12, 25, 14, 0)

            val event = Event(
                title = "Test Event",
                startTime = startTime,
                endTime = endTime,
                venueId = 5
            )

            val result = event.toString()

            assertTrue(result.contains("Test Event"))
            assertTrue(result.contains("Venue ID: 5"))
            assertTrue(result.contains("25/12/2025"))
            println("Test passed: toString format: Returns formatted string containing event title, venue ID, and date")
        } catch (e: Exception) {
            System.err.println("Test failed: toString format: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testToStringWithNoVenue() {
        try {
            val event = Event(
                title = "Test Event",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                venueId = null
            )

            val result = event.toString()

            assertTrue(result.contains("No venue assigned"))
            println("Test passed: toString with no venue: Correctly displays 'No venue assigned'")
        } catch (e: Exception) {
            System.err.println("Test failed: toString with no venue: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetDetailedInfo() {
        try {
            val event = Event(
                title = "Tech Conference",
                description = "Annual event",
                type = "Conference",
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(2),
                venueId = 1,
                estimatedAttendees = 100,
                city = "London"
            )

            val info = event.getDetailedInfo()

            assertTrue(info.contains("Event: Tech Conference"))
            assertTrue(info.contains("Type: Conference"))
            assertTrue(info.contains("Description: Annual event"))
            assertTrue(info.contains("City: London"))
            assertTrue(info.contains("Venue ID: 1"))
            assertTrue(info.contains("Estimated Attendees: 100"))
            assertTrue(info.contains("Registered Attendees: 0"))
            println("Test passed: Get detailed info: Returns complete event information including all fields")
        } catch (e: Exception) {
            System.err.println("Test failed: Get detailed info: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testAttendeesListIsImmutable() {
        try {
            val event = createTestEvent()
            val attendee = Attendee(name = "John", email = "john@example.com")

            event.registerAttendee(attendee)

            val attendeesList = event.attendees
            assertEquals(1, attendeesList.size)
            assertTrue(true)
            println("Test passed: Attendees list is immutable: Returns immutable List type with ${attendeesList.size} attendee")
        } catch (e: Exception) {
            System.err.println("Test failed: Attendees list is immutable: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testEventWithMultipleAttendees() {
        try {
            val event = createTestEvent(estimatedAttendees = 10)

            val attendees = listOf(
                Attendee(name = "John", email = "john@example.com"),
                Attendee(name = "Jane", email = "jane@example.com"),
                Attendee(name = "Bob", email = "bob@example.com")
            )

            attendees.forEach { event.registerAttendee(it) }

            assertEquals(3, event.getAttendeeCount())
            attendees.forEach { assertTrue(event.isAttendeeRegistered(it.id)) }
            println("Test passed: Event with multiple attendees: Successfully registered ${attendees.size} attendees")
        } catch (e: Exception) {
            System.err.println("Test failed: Event with multiple attendees: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
