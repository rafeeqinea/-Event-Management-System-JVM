package unitTests.LogicHandling

import LogicHandling.Attendee
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AttendeeTest {

    @Test
    fun testValidAttendeeCreation() {
        try {
            val attendee = Attendee(
                name = "Lewis Hamilton",
                email = "lewis.hamilton@example.com",
                phone = "+44123456789",
                organization = "Mercedes-AMG Petronas"
            )

            assertEquals("Lewis Hamilton", attendee.name)
            assertEquals("lewis.hamilton@example.com", attendee.email)
            assertEquals("+44123456789", attendee.phone)
            assertEquals("Mercedes-AMG Petronas", attendee.organization)
            assertNotNull(attendee.id)

            println("Test passed: Valid attendee creation: Created attendee '${attendee.name}' with email '${attendee.email}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Valid attendee creation: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testAttendeeCreationWithMinimalFields() {
        try {
            val attendee = Attendee(name = "Max Verstappen", email = "max.verstappen@example.com")

            assertEquals("Max Verstappen", attendee.name)
            assertEquals("max.verstappen@example.com", attendee.email)
            assertEquals("", attendee.phone)
            assertEquals("", attendee.organization)

            println("Test passed: Minimal fields attendee creation: Created '${attendee.name}' with only name and email")
        } catch (e: Exception) {
            System.err.println("Test failed: Minimal fields attendee creation: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testBlankNameThrowsException() {
        try {
            Attendee(name = "", email = "test@example.com")
            System.err.println("Test failed: Blank name validation: Should have thrown exception")
            fail("Should throw exception for blank name")
        } catch (e: IllegalArgumentException) {
            assertEquals("Attendee name cannot be blank", e.message)
            println("Test passed: Blank name validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Blank name validation: Wrong exception type ${e.javaClass.simpleName}")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testWhitespaceOnlyNameThrowsException() {
        try {
            Attendee(name = "   ", email = "test@example.com")
            System.err.println("Test failed: Whitespace name validation: Should have thrown exception")
            fail("Should throw exception for whitespace-only name")
        } catch (e: IllegalArgumentException) {
            assertEquals("Attendee name cannot be blank", e.message)
            println("Test passed: Whitespace name validation: Correctly rejected whitespace-only name")
        } catch (e: Exception) {
            System.err.println("Test failed: Whitespace name validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testBlankEmailThrowsException() {
        try {
            Attendee(name = "Charles Leclerc", email = "")
            System.err.println("Test failed: Blank email validation: Should have thrown exception")
            fail("Should throw exception for blank email")
        } catch (e: IllegalArgumentException) {
            assertEquals("Email cannot be blank", e.message)
            println("Test passed: Blank email validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Blank email validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testEmailWithSpacesThrowsException() {
        try {
            Attendee(name = "George Russell", email = "george russell@example.com")
            System.err.println("Test failed: Email with spaces validation: Should have thrown exception")
            fail("Should throw exception for email with spaces")
        } catch (e: IllegalArgumentException) {
            assertEquals("Email cannot contain spaces", e.message)
            println("Test passed: Email with spaces validation: Correctly rejected 'george russell@example.com'")
        } catch (e: Exception) {
            System.err.println("Test failed: Email with spaces validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testEmailTooLongThrowsException() {
        try {
            val longEmail = "a".repeat(250) + "@example.com"
            Attendee(name = "Lando Norris", email = longEmail)
            System.err.println("Test failed: Email too long validation: Should have thrown exception")
            fail("Should throw exception for email that's too long")
        } catch (e: IllegalArgumentException) {
            assertEquals("Email is too long", e.message)
            println("Test passed: Email too long validation: Correctly rejected email with 255+ characters")
        } catch (e: Exception) {
            System.err.println("Test failed: Email too long validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testInvalidEmailFormatThrowsException() {
        val invalidEmails = listOf("NotValidEmail", "@example.com", "test@", "test@.com")
        var successCount = 0

        invalidEmails.forEach { invalidEmail ->
            try {
                Attendee(name = "Fernando Alonso", email = invalidEmail)
                System.err.println("Test failed: Invalid email format '$invalidEmail': Should have thrown exception")
                fail("Should throw exception for invalid email: $invalidEmail")
            } catch (e: IllegalArgumentException) {
                assertTrue(e.message!!.startsWith("Invalid email format"))
                successCount++
            } catch (e: Exception) {
                System.err.println("Test failed: Invalid email format '$invalidEmail': Wrong exception type")
                fail("Wrong exception type for $invalidEmail: ${e.javaClass.simpleName}")
            }
        }

        println("Test passed: Invalid email format validation: Rejected $successCount invalid formats")
    }

    @Test
    fun testValidEmailFormats() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@example.com",
            "123@example.com"
        )
        var successCount = 0

        validEmails.forEach { validEmail ->
            try {
                val attendee = Attendee(name = "Carlos Sainz", email = validEmail)
                assertNotNull(attendee)
                successCount++
            } catch (e: Exception) {
                System.err.println("Test failed: Valid email format '$validEmail': ${e.message}")
                fail("Should not throw exception for valid email $validEmail: ${e.message}")
            }
        }

        println("Test passed: Valid email formats: Accepted $successCount valid email formats")
    }

    @Test
    fun testInvalidPhoneFormatThrowsException() {
        val invalidPhones = listOf("123", "abc1234567890", "12345678901234567890" , "000000")
        var rejectedCount = 0

        invalidPhones.forEach { invalidPhone ->
            try {
                Attendee(name = "Sergio Perez", email = "sergio.perez@example.com", phone = invalidPhone)
                System.err.println("Test failed: Invalid phone format '$invalidPhone': Should have thrown exception")
                fail("Should throw exception for invalid phone: $invalidPhone")
            } catch (e: IllegalArgumentException) {
                assertTrue(e.message!!.contains("Invalid phone format"))
                rejectedCount++
            } catch (e: Exception) {
                System.err.println("Test failed: Invalid phone format '$invalidPhone': Wrong exception type")
                fail("Wrong exception type for $invalidPhone: ${e.javaClass.simpleName}")
            }
        }

        println("Test passed: Invalid phone format validation: Rejected $rejectedCount invalid formats")
    }

    @Test
    fun testValidPhoneFormats() {
        val validPhones = listOf("+1234567890", "1234567890", "+44 20 1234 5678", "(123) 456-7890", "+1-234-567-8900")
        var acceptedCount = 0

        validPhones.forEach { validPhone ->
            try {
                val attendee = Attendee(name = "Oscar Piastri", email = "oscar.piastri@example.com", phone = validPhone)
                assertNotNull(attendee)
                acceptedCount++
            } catch (e: Exception) {
                System.err.println("Test failed: Valid phone format '$validPhone': ${e.message}")
                fail("Should not throw exception for valid phone $validPhone: ${e.message}")
            }
        }

        println("Test passed: Valid phone formats: Accepted $acceptedCount valid phone formats")
    }

    @Test
    fun testEmptyPhoneIsAllowed() {
        try {
            val attendee = Attendee(name = "Pierre Gasly", email = "pierre.gasly@example.com", phone = "")
            assertNotNull(attendee)
            println("Test passed: Empty phone allowed: Successfully created attendee with empty phone field")
        } catch (e: Exception) {
            System.err.println("Test failed: Empty phone allowed: ${e.message}")
            fail("Should not throw exception for empty phone: ${e.message}")
        }
    }

    @Test
    fun testToStringFormat() {
        try {
            val attendee = Attendee(name = "Yuki Tsunoda", email = "yuki.tsunoda@example.com")
            val result = attendee.toString()
            assertEquals("Yuki Tsunoda (yuki.tsunoda@example.com)", result)
            println("Test passed: toString format: Returns '$result'")
        } catch (e: Exception) {
            System.err.println("Test failed: toString format: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testUniqueIDsAreGenerated() {
        try {
            val attendee1 = Attendee(name = "Esteban Ocon", email = "esteban.ocon@example.com")
            val attendee2 = Attendee(name = "Lance Stroll", email = "lance.stroll@example.com")
            assertNotEquals(attendee1.id, attendee2.id)
            println("Test passed: Unique ID generation: Generated unique IDs for different attendees")
        } catch (e: Exception) {
            System.err.println("Test failed: Unique ID generation: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testMutablePropertiesCanBeUpdated() {
        try {
            val attendee = Attendee(name = "Valtteri Bottas", email = "valtteri.bottas@example.com")

            attendee.name = "Zhou Guanyu"
            attendee.email = "zhou.guanyu@example.com"
            attendee.phone = "+8612345678"
            attendee.organization = "Alfa Romeo F1 Team"

            assertEquals("Zhou Guanyu", attendee.name)
            assertEquals("zhou.guanyu@example.com", attendee.email)
            assertEquals("+8612345678", attendee.phone)
            assertEquals("Alfa Romeo F1 Team", attendee.organization)

            println("Test passed: Mutable properties update: Updated all properties from 'Valtteri Bottas' to 'Zhou Guanyu'")
        } catch (e: Exception) {
            System.err.println("Test failed: Mutable properties update: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
