package unitTests.LogicHandling

import LogicHandling.Venue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VenueTest {

    @Test
    fun testValidVenueCreation() {
        try {
            val venue = Venue(
                id = 1,
                name = "Conference Center",
                capacity = 500,
                city = "London",
                address = "123 Main Street",
                facilities = listOf("WiFi", "Projector", "AC")
            )

            assertEquals(1, venue.id)
            assertEquals("Conference Center", venue.name)
            assertEquals(500, venue.capacity)
            assertEquals("London", venue.city)
            assertEquals("123 Main Street", venue.address)
            assertEquals(3, venue.facilities.size)
            assertTrue(venue.facilities.contains("WiFi"))
            println("Test passed: Valid venue creation: Venue '${venue.name}' created with capacity ${venue.capacity} and ${venue.facilities.size} facilities")
        } catch (e: Exception) {
            System.err.println("Test failed: Valid venue creation: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testVenueCreationWithMinimalFields() {
        try {
            val venue = Venue(
                id = 1,
                name = "Small Hall",
                capacity = 50,
                city = "Manchester",
                address = "456 Side Street"
            )

            assertEquals(1, venue.id)
            assertEquals("Small Hall", venue.name)
            assertEquals(50, venue.capacity)
            assertEquals("Manchester", venue.city)
            assertEquals("456 Side Street", venue.address)
            assertTrue(venue.facilities.isEmpty())
            println("Test passed: Venue creation with minimal fields: Created '${venue.name}' with only required fields")
        } catch (e: Exception) {
            System.err.println("Test failed: Venue creation with minimal fields: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testBlankNameThrowsException() {
        try {
            Venue(
                id = 1,
                name = "",
                capacity = 100,
                city = "London",
                address = "123 Main St"
            )
            System.err.println("Test failed: Blank name validation: Should have thrown exception")
            fail("Should throw exception for blank name")
        } catch (e: IllegalArgumentException) {
            assertEquals("Venue name cannot be empty", e.message)
            println("Test passed: Blank name validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Blank name validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testWhitespaceOnlyNameThrowsException() {
        try {
            Venue(
                id = 1,
                name = "   ",
                capacity = 100,
                city = "London",
                address = "123 Main St"
            )
            System.err.println("Test failed: Whitespace-only name validation: Should have thrown exception")
            fail("Should throw exception for whitespace-only name")
        } catch (e: IllegalArgumentException) {
            assertEquals("Venue name cannot be empty", e.message)
            println("Test passed: Whitespace-only name validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Whitespace-only name validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testBlankCityThrowsException() {
        try {
            Venue(
                id = 1,
                name = "Conference Center",
                capacity = 100,
                city = "",
                address = "123 Main St"
            )
            System.err.println("Test failed: Blank city validation: Should have thrown exception")
            fail("Should throw exception for blank city")
        } catch (e: IllegalArgumentException) {
            assertEquals("City/location must be provided", e.message)
            println("Test passed: Blank city validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Blank city validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testBlankAddressThrowsException() {
        try {
            Venue(
                id = 1,
                name = "Conference Center",
                capacity = 100,
                city = "London",
                address = ""
            )
            System.err.println("Test failed: Blank address validation: Should have thrown exception")
            fail("Should throw exception for blank address")
        } catch (e: IllegalArgumentException) {
            assertEquals("Address cannot be empty", e.message)
            println("Test passed: Blank address validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Blank address validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testZeroCapacityThrowsException() {
        try {
            Venue(
                id = 1,
                name = "Conference Center",
                capacity = 0,
                city = "London",
                address = "123 Main St"
            )
            System.err.println("Test failed: Zero capacity validation: Should have thrown exception")
            fail("Should throw exception for zero capacity")
        } catch (e: IllegalArgumentException) {
            assertEquals("Venue capacity must be a positive integer", e.message)
            println("Test passed: Zero capacity validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Zero capacity validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testNegativeCapacityThrowsException() {
        try {
            Venue(
                id = 1,
                name = "Conference Center",
                capacity = -50,
                city = "London",
                address = "123 Main St"
            )
            System.err.println("Test failed: Negative capacity validation: Should have thrown exception")
            fail("Should throw exception for negative capacity")
        } catch (e: IllegalArgumentException) {
            assertEquals("Venue capacity must be a positive integer", e.message)
            println("Test passed: Negative capacity validation: Correctly rejected with '${e.message}'")
        } catch (e: Exception) {
            System.err.println("Test failed: Negative capacity validation: Wrong exception type")
            fail("Wrong exception type: ${e.javaClass.simpleName}")
        }
    }

    @Test
    fun testGetDetailedInfoWithFacilities() {
        try {
            val venue = Venue(
                id = 1,
                name = "Conference Center",
                capacity = 500,
                city = "London",
                address = "123 Main Street",
                facilities = listOf("WiFi", "Projector", "AC")
            )

            val info = venue.getDetailedInfo()

            assertTrue(info.contains("Venue: Conference Center"))
            assertTrue(info.contains("Location: 123 Main Street, London"))
            assertTrue(info.contains("Capacity: 500"))
            assertTrue(info.contains("Facilities: WiFi, Projector, AC"))
            println("Test passed: Get detailed info with facilities: Returns complete venue information with 3 facilities")
        } catch (e: Exception) {
            System.err.println("Test failed: Get detailed info with facilities: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testGetDetailedInfoWithoutFacilities() {
        try {
            val venue = Venue(
                id = 1,
                name = "Small Hall",
                capacity = 50,
                city = "Manchester",
                address = "456 Side Street"
            )

            val info = venue.getDetailedInfo()

            assertTrue(info.contains("Venue: Small Hall"))
            assertTrue(info.contains("Location: 456 Side Street, Manchester"))
            assertTrue(info.contains("Capacity: 50"))
            assertFalse(info.contains("Facilities:"))
            println("Test passed: Get detailed info without facilities: Returns venue information without facilities section")
        } catch (e: Exception) {
            System.err.println("Test failed: Get detailed info without facilities: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testMutablePropertiesCanBeUpdated() {
        try {
            val venue = Venue(
                id = 1,
                name = "Old Name",
                capacity = 100,
                city = "Old City",
                address = "Old Address"
            )

            venue.name = "New Name"
            venue.capacity = 200
            venue.city = "New City"
            venue.address = "New Address"
            venue.facilities = listOf("WiFi", "AC")

            assertEquals("New Name", venue.name)
            assertEquals(200, venue.capacity)
            assertEquals("New City", venue.city)
            assertEquals("New Address", venue.address)
            assertEquals(2, venue.facilities.size)
            println("Test passed: Mutable properties can be updated: All venue properties successfully updated")
        } catch (e: Exception) {
            System.err.println("Test failed: Mutable properties can be updated: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testVenueWithSingleFacility() {
        try {
            val venue = Venue(
                id = 1,
                name = "Small Room",
                capacity = 20,
                city = "London",
                address = "10 Downing Street",
                facilities = listOf("WiFi")
            )

            val info = venue.getDetailedInfo()

            assertTrue(info.contains("Facilities: WiFi"))
            println("Test passed: Venue with single facility: Correctly displays single facility 'WiFi'")
        } catch (e: Exception) {
            System.err.println("Test failed: Venue with single facility: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun testVenueCapacityEdgeCases() {
        try {
            val smallVenue = Venue(
                id = 1,
                name = "Tiny Room",
                capacity = 1,
                city = "London",
                address = "1 Small Lane"
            )
            assertEquals(1, smallVenue.capacity)

            val largeVenue = Venue(
                id = 2,
                name = "Stadium",
                capacity = 100000,
                city = "London",
                address = "Stadium Road"
            )
            assertEquals(100000, largeVenue.capacity)
            println("Test passed: Venue capacity edge cases: Successfully handles capacities from 1 to 100,000")
        } catch (e: Exception) {
            System.err.println("Test failed: Venue capacity edge cases: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
