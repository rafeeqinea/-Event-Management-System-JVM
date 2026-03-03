package unitTests.LogicHandling.HandlingServicesTest

import LogicHandling.HandlingServices.ValidationHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ValidationHandlerTest {

    private lateinit var validator: ValidationHandler

    @BeforeEach
    fun setup() {
        try {
            validator = ValidationHandler()
        } catch (e: Exception) {
            fail("Failed to initialize: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate event with valid inputs")
    fun testValidateEventWithValidInputs() {
        try {
            val result = validator.validateEvent(
                title = "Monaco Grand Prix",
                startDate = "01/12/2025",
                startTime = "10:00",
                endDate = "01/12/2025",
                endTime = "14:00",
                estimatedAttendeesStr = "100",
                requireFutureStart = false
            )

            assertTrue(result.isValid)
            assertEquals("", result.errorMessage)
            println("Test passed: Validate event with valid inputs: Event 'Monaco Grand Prix' validated successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate event with valid inputs: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate event with blank title")
    fun testValidateEventWithBlankTitle() {
        try {
            val result = validator.validateEvent(
                title = "",
                startDate = "01/12/2025",
                startTime = "10:00",
                endDate = "01/12/2025",
                endTime = "14:00",
                estimatedAttendeesStr = "100"
            )

            assertFalse(result.isValid)
            assertEquals("Event title cannot be blank", result.errorMessage)
            println("Test passed: Validate event with blank title: Correctly rejected with '${result.errorMessage}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate event with blank title: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate event with invalid attendees")
    fun testValidateEventWithInvalidAttendees() {
        try {
            val result = validator.validateEvent(
                title = "Bahrain Grand Prix",
                startDate = "01/12/2025",
                startTime = "10:00",
                endDate = "01/12/2025",
                endTime = "14:00",
                estimatedAttendeesStr = "invalid"
            )

            assertFalse(result.isValid)
            assertTrue(result.errorMessage.contains("valid number"))
            println("Test passed: Validate event with invalid attendees: Correctly rejected with '${result.errorMessage}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate event with invalid attendees: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate event with end before start")
    fun testValidateEventWithEndBeforeStart() {
        try {
            val result = validator.validateEvent(
                title = "Belgian Grand Prix",
                startDate = "01/12/2025",
                startTime = "14:00",
                endDate = "01/12/2025",
                endTime = "10:00",
                estimatedAttendeesStr = "100",
                requireFutureStart = false
            )

            assertFalse(result.isValid)
            assertEquals("End date and time must be after start date and time", result.errorMessage)
            println("Test passed: Validate event with end before start: Correctly rejected with '${result.errorMessage}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate event with end before start: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate attendee with valid inputs")
    fun testValidateAttendeeWithValidInputs() {
        try {
            val result = validator.validateAttendee(
                name = "Lewis Hamilton",
                email = "lewis.hamilton@mercedesamg.com",
                phone = "+441234567890"
            )

            assertTrue(result.isValid)
            assertEquals("", result.errorMessage)
            println("Test passed: Validate attendee with valid inputs: Attendee 'Lewis Hamilton' validated successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate attendee with valid inputs: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate attendee with blank email")
    fun testValidateAttendeeWithBlankEmail() {
        try {
            val result = validator.validateAttendee(
                name = "Max Verstappen",
                email = ""
            )

            assertFalse(result.isValid)
            assertEquals("Email cannot be blank", result.errorMessage)
            println("Test passed: Validate attendee with blank email: Correctly rejected with '${result.errorMessage}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate attendee with blank email: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate attendee with invalid email")
    fun testValidateAttendeeWithInvalidEmail() {
        try {
            val result = validator.validateAttendee(
                name = "Charles Leclerc",
                email = "notanemail"
            )

            assertFalse(result.isValid)
            assertTrue(result.errorMessage.startsWith("Invalid email format"))
            println("Test passed: Validate attendee with invalid email: Correctly rejected with '${result.errorMessage}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate attendee with invalid email: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate venue with valid inputs")
    fun testValidateVenueWithValidInputs() {
        try {
            val result = validator.validateVenue(
                name = "Silverstone Circuit",
                address = "Circuit Dr",
                city = "Silverstone",
                capacityStr = "500"
            )

            assertTrue(result.isValid)
            assertEquals("", result.errorMessage)
            println("Test passed: Validate venue with valid inputs: Venue 'Silverstone Circuit' with capacity 500 validated successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate venue with valid inputs: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate venue with zero capacity")
    fun testValidateVenueWithZeroCapacity() {
        try {
            val result = validator.validateVenue(
                name = "Red Bull Ring",
                address = "Spielberg 300",
                city = "Spielberg",
                capacityStr = "0"
            )

            assertFalse(result.isValid)
            assertEquals("Venue capacity must be greater than 0", result.errorMessage)
            println("Test passed: Validate venue with zero capacity: Correctly rejected with '${result.errorMessage}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate venue with zero capacity: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test parse date time with valid input")
    fun testParseDateTimeWithValidInput() {
        try {
            val result = validator.parseDateTime("25/12/2025", "14:30")

            assertNotNull(result)
            assertEquals(2025, result?.year)
            assertEquals(12, result?.monthValue)
            assertEquals(25, result?.dayOfMonth)
            assertEquals(14, result?.hour)
            assertEquals(30, result?.minute)
            println("Test passed: Parse date time with valid input: Successfully parsed '25/12/2025 14:30'")
        } catch (e: Exception) {
            System.err.println("Test failed: Parse date time with valid input: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test parse date time with invalid format")
    fun testParseDateTimeWithInvalidFormat() {
        try {
            val result = validator.parseDateTime("2025-12-25", "14:30")

            assertNull(result)
            println("Test passed: Parse date time with invalid format: Correctly returned null for invalid format")
        } catch (e: Exception) {
            System.err.println("Test failed: Parse date time with invalid format: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate positive integer")
    fun testValidatePositiveInteger() {
        try {
            val result1 = validator.validatePositiveInteger("100", "test field")
            assertTrue(result1.isValid)

            val result2 = validator.validatePositiveInteger("-50", "test field")
            assertFalse(result2.isValid)

            val result3 = validator.validatePositiveInteger("abc", "test field")
            assertFalse(result3.isValid)

            println("Test passed: Validate positive integer: Validated 100 (valid), -50 (invalid), abc (invalid) correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate positive integer: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test validate required field")
    fun testValidateRequiredField() {
        try {
            val result1 = validator.validateRequired("Some Value", "test field")
            assertTrue(result1.isValid)

            val result2 = validator.validateRequired("", "test field")
            assertFalse(result2.isValid)
            assertEquals("test field cannot be blank", result2.errorMessage)

            println("Test passed: Validate required field: Accepted 'Some Value', rejected blank correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: Validate required field: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
