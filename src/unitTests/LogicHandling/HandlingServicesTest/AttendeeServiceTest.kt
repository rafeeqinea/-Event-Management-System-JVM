package unitTests.LogicHandling.HandlingServicesTest

import LogicHandling.Attendee
import LogicHandling.HandlingServices.AttendeeService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AttendeeServiceTest {

    private lateinit var service: AttendeeService

    @BeforeEach
    fun setup() {
        try {
            service = AttendeeService()
        } catch (e: Exception) {
            fail("Test failed: Failed to initialize service: ${e.message}")
        }
    }
    // Test: Creating a valid attendee should succeed and return correct data
    @Test
    fun createAttendee() {
        try {
            val attendee = service.createAttendee(
                name = "Lewis Hamilton",
                email = "lewis.hamilton@example.com",
                phone = "+44123456789",
                organization = "Mercedes-AMG Petronas"
            )

            assertNotNull(attendee, "Attendee should not be null")
            assertEquals("Lewis Hamilton", attendee?.name)
            assertEquals("lewis.hamilton@example.com", attendee?.email)
            assertEquals("+44123456789", attendee?.phone)
            assertEquals("Mercedes-AMG Petronas", attendee?.organization)
            println("Test passed: Create attendee: ${attendee?.name} created successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Creating an attendee with a duplicate email should return null
    @Test
    fun createAttendeeWithDuplicateEmail() {
        try {
            service.createAttendee(
                name = "Max Verstappen",
                email = "max.verstappen@example.com"
            )

            val duplicate = service.createAttendee(
                name = "Charles Leclerc",
                email = "max.verstappen@example.com"
            )

            assertNull(duplicate, "Duplicate email should return null")
            assertEquals(1, service.getAllAttendees().size)
            println("Test passed: Create attendee with duplicate email: Duplicate correctly rejected")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Adding a valid attendee object should succeed
    @Test
    fun addAttendee() {
        try {
            val attendee = Attendee(name = "Lando Norris", email = "lando.norris@example.com")

            val result = service.addAttendee(attendee)

            assertTrue(result, "Should successfully add attendee")
            assertEquals(1, service.getAllAttendees().size)
            println("Test passed: Add attendee: Attendee added successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Adding the same attendee twice should fail due to duplicate ID
    @Test
    fun addAttendeeWithDuplicateId() {
        try {
            val attendee = Attendee(name = "George Russell", email = "george.russell@example.com")

            assertTrue(service.addAttendee(attendee))
            assertFalse(service.addAttendee(attendee), "Duplicate ID should return false")
            assertEquals(1, service.getAllAttendees().size)
            println("Test passed: Add attendee with duplicate ID: Duplicate ID correctly rejected")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Adding attendee with duplicate email should fail
    @Test
    fun addAttendeeWithDuplicateEmail() {
        try {
            val attendee1 = Attendee(name = "Carlos Sainz", email = "carlos.sainz@example.com")
            val attendee2 = Attendee(name = "Fernando Alonso", email = "carlos.sainz@example.com")

            assertTrue(service.addAttendee(attendee1))
            assertFalse(service.addAttendee(attendee2), "Duplicate email should return false")
            assertEquals(1, service.getAllAttendees().size)
            println("Test passed: Add attendee with duplicate email: Duplicate email correctly rejected")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Removing an existing attendee should succeed
    @Test
    fun removeAttendee() {
        try {
            val attendee = service.createAttendee(name = "Sergio Perez", email = "sergio.perez@example.com")
            assertNotNull(attendee, "Attendee should be created")

            val result = service.removeAttendee(attendee!!.id)

            assertTrue(result, "Should successfully remove attendee")
            assertEquals(0, service.getAllAttendees().size)
            println("Test passed: Remove attendee: Attendee removed successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Removing non-existent attendee ID should return false
    @Test
    fun removeAttendeeWithNonExistentId() {
        try {
            val result = service.removeAttendee("non-existent-id")

            assertFalse(result, "Should return false for non-existent ID")
            println("Test passed: Remove attendee with non-existent ID: Correctly returned false")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Updating an attendee should modify stored data correctly
    @Test
    fun updateAttendee() {
        try {
            val attendee = service.createAttendee(name = "Jasmine", email = "jasmine@proton.com")
            assertNotNull(attendee, "Attendee should be created")

            val result = service.updateAttendee(
                attendeeId = attendee!!.id,
                name = "Jane Plow",
                email = "jane@yahoo.com",
                phone = "+1234567890",
                organization = "Shell"
            )

            assertTrue(result, "Should successfully update attendee")

            val updated = service.getAttendee(attendee.id)
            assertEquals("Jane Plow", updated?.name)
            assertEquals("jane@yahoo.com", updated?.email)
            assertEquals("+1234567890", updated?.phone)
            assertEquals("Shell", updated?.organization)
            println("Test passed: Update attendee: Attendee updated to ${updated?.name}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Updating non-existent attendee should fail
    @Test
    fun updateAttendeeWithNonExistentId() {
        try {
            val result = service.updateAttendee(
                attendeeId = "non-existent-id",
                name = "Jane Doe",
                email = "jane@example.com",
                phone = "",
                organization = ""
            )

            assertFalse(result, "Should return false for non-existent ID")
            println("Test passed: Update attendee with non-existent ID: Correctly returned false")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Updating attendee with duplicate email should fail
    @Test
    fun updateAttendeeWithDuplicateEmail() {
        try {
            val attendee1 = service.createAttendee(name = "Oscar Piastri", email = "oscar.piastri@example.com")
            val attendee2 = service.createAttendee(name = "Yuki Tsunoda", email = "yuki.tsunoda@example.com")

            assertNotNull(attendee1)
            assertNotNull(attendee2)

            val result = service.updateAttendee(
                attendeeId = attendee2!!.id,
                name = "Yuki Tsunoda",
                email = "oscar.piastri@example.com",
                phone = "",
                organization = ""
            )

            assertFalse(result, "Should not allow duplicate email")

            val unchanged = service.getAttendee(attendee2.id)
            assertEquals("yuki.tsunoda@example.com", unchanged?.email)
            println("Test passed: Update attendee with duplicate email: Duplicate email correctly rejected")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Updating attendee but keeping the same email should succeed
    @Test
    fun updateAttendeeWithSameEmail() {
        try {
            val attendee = service.createAttendee(name = "Pierre Gasly", email = "pierre.gasly@example.com")
            assertNotNull(attendee)

            val result = service.updateAttendee(
                attendeeId = attendee!!.id,
                name = "Pierre Gasly",
                email = "pierre.gasly@example.com",
                phone = "+33123456789",
                organization = "Alpine F1 Team"
            )

            assertTrue(result, "Should allow same email for same attendee")

            val updated = service.getAttendee(attendee.id)
            assertEquals("Pierre Gasly", updated?.name)
            println("Test passed: Update attendee with same email: Same email allowed for same attendee")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Getting an attendee by ID should return correct attendee
    @Test
    fun getAttendee() {
        try {
            val attendee = service.createAttendee(name = "Esteban Ocon", email = "esteban.ocon@example.com")
            assertNotNull(attendee)

            val retrieved = service.getAttendee(attendee!!.id)

            assertNotNull(retrieved, "Should retrieve attendee")
            assertEquals(attendee.id, retrieved?.id)
            assertEquals("Esteban Ocon", retrieved?.name)
            println("Test passed: Get attendee: Retrieved ${retrieved?.name}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Getting attendee by non-existent ID should return null
    @Test
    fun getAttendeeWithNonExistentId() {
        try {
            val result = service.getAttendee("non-existent-id")

            assertNull(result, "Should return null for non-existent ID")
            println("Test passed: Get attendee with non-existent ID: Correctly returned null")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Retrieving attendee by email should succeed
    @Test
    fun getAttendeeByEmail() {
        try {
            service.createAttendee(name = "Lance Stroll", email = "lance.stroll@example.com")

            val retrieved = service.getAttendeeByEmail("lance.stroll@example.com")

            assertNotNull(retrieved, "Should retrieve attendee by email")
            assertEquals("Lance Stroll", retrieved?.name)
            println("Test passed: Get attendee by email: Retrieved ${retrieved?.name}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: getAttendeeByEmail should be case-insensitive
    @Test
    fun getAttendeeByEmailCaseInsensitive() {
        try {
            service.createAttendee(name = "Valtteri Bottas", email = "valtteri.bottas@example.com")

            val retrieved1 = service.getAttendeeByEmail("VALTTERI.BOTTAS@EXAMPLE.COM")
            val retrieved2 = service.getAttendeeByEmail("Valtteri.Bottas@Example.Com")

            assertNotNull(retrieved1, "Should be case insensitive")
            assertNotNull(retrieved2, "Should be case insensitive")
            assertEquals("Valtteri Bottas", retrieved1?.name)
            assertEquals("Valtteri Bottas", retrieved2?.name)
            println("Test passed: Get attendee by email case insensitive: Email lookup is case insensitive")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Getting attendee by non-existent email should return null
    @Test
    fun getAttendeeByEmailWithNonExistentEmail() {
        try {
            val result = service.getAttendeeByEmail("nonexistent@example.com")

            assertNull(result, "Should return null for non-existent email")
            println("Test passed: Get attendee by email with non-existent email: Correctly returned null")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: getAllAttendees should return all attendees
    @Test
    fun getAllAttendees() {
        try {
            assertEquals(0, service.getAllAttendees().size, "Should start with empty list")

            service.createAttendee(name = "Daniel Ricciardo", email = "daniel.ricciardo@example.com")
            service.createAttendee(name = "Alex Albon", email = "alex.albon@example.com")
            service.createAttendee(name = "Zhou Guanyu", email = "zhou.guanyu@example.com")

            val allAttendees = service.getAllAttendees()

            assertEquals(3, allAttendees.size, "Should have 3 attendees")
            println("Test passed: Get all attendees: Retrieved ${allAttendees.size} attendees")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: getAllAttendees should return a copy, not the internal list
    @Test
    fun getAllAttendeesReturnsCopy() {
        try {
            service.createAttendee(name = "Nico Hulkenberg", email = "nico.hulkenberg@example.com")

            val list1 = service.getAllAttendees()
            val list2 = service.getAllAttendees()

            assertEquals(list1.size, list2.size)
            assertNotSame(list1, list2, "Should return copy not same instance")
            println("Test passed: Get all attendees returns copy: Returns independent list instances")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: clearAll should remove all attendees
    @Test
    fun clearAll() {
        try {
            service.createAttendee(name = "Kevin Magnussen", email = "kevin.magnussen@example.com")
            service.createAttendee(name = "Logan Sargeant", email = "logan.sargeant@example.com")

            assertEquals(2, service.getAllAttendees().size)

            service.clearAll()

            assertEquals(0, service.getAllAttendees().size, "Should clear all attendees")
            println("Test passed: Clear all: All attendees cleared successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
    // Test: Sequence of multiple operations (create → add → update → remove) should behave consistently
    @Test
    fun multipleOperationsInSequence() {
        try {
            val attendee1 = service.createAttendee(name = "Mick Schumacher", email = "mick.schumacher@example.com")
            assertNotNull(attendee1)

            val attendee2 = Attendee(name = "Nyck de Vries", email = "nyck.devries@example.com")
            assertTrue(service.addAttendee(attendee2))

            assertEquals(2, service.getAllAttendees().size)

            assertTrue(service.updateAttendee(
                attendee1!!.id, "Mick Schumacher", "mick.schumacher@haas.com", "+49123456789", "Haas F1 Team"
            ))

            assertTrue(service.removeAttendee(attendee2.id))

            assertEquals(1, service.getAllAttendees().size)

            val remaining = service.getAttendee(attendee1.id)
            assertEquals("Mick Schumacher", remaining?.name)
            assertEquals("mick.schumacher@haas.com", remaining?.email)
            println("Test passed: Multiple operations in sequence: All operations completed successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
