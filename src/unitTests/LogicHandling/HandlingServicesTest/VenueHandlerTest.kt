package unitTests.LogicHandling.HandlingServicesTest

import LogicHandling.HandlingServices.VenueHandler
import LogicHandling.Venue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class VenueHandlerTest {

    private lateinit var service: VenueHandler

    @BeforeEach
    fun setup() {
        try {
            service = VenueHandler()
        } catch (e: Exception) {
            fail("Test failed: Failed to initialize service: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test adding venue successfully")
    fun testAddVenueSuccessfully() {
        println("\nTEST : Testing add venue successfully")
        try {
            val venue = service.addVenue(
                name = "Symphony Hall",
                address = "123 Broad Street",
                city = "Birmingham",
                capacity = 500,
                facilities = listOf("WiFi", "Projector")
            )

            assertEquals("Symphony Hall", venue.name)
            assertEquals(500, venue.capacity)
            assertEquals(1, venue.id)
            println("Test passed: Venue added successfully with ID: ${venue.id}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test venue ID generation")
    fun testVenueIdGeneration() {
        println("\nTEST : Testing venue ID generation")
        try {
            val venue1 = service.addVenue("City Hall", "100 Municipal Blvd", "Bristol", 100)
            val venue2 = service.addVenue("Tech Hub", "200 Innovation Way", "Cambridge", 200)
            val venue3 = service.addVenue("Arena Centre", "300 Stadium Road", "Sheffield", 300)

            assertEquals(1, venue1.id)
            assertEquals(2, venue2.id)
            assertEquals(3, venue3.id)
            println("Test passed: Sequential IDs generated: ${venue1.id}, ${venue2.id}, ${venue3.id}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test adding existing venue")
    fun testAddExistingVenue() {
        println("\nTEST : Testing add existing venue")
        try {
            val venue = Venue(
                id = 5,
                name = "Existing Venue",
                capacity = 100,
                city = "London",
                address = "Test Address"
            )

            val result = service.addExistingVenue(venue)

            assertTrue(result, "Should successfully add existing venue")
            assertEquals(1, service.getAllVenues().size)
            assertEquals(5, service.getVenue(5)?.id)
            println("Test passed: Existing venue added with ID: ${venue.id}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test duplicate venue ID rejection")
    fun testAddExistingVenueWithDuplicateId() {
        println("\nTEST : Testing duplicate venue ID rejection")
        try {
            val venue1 = Venue(1, "Royal Opera House", 100, "London", "Bow Street")
            val venue2 = Venue(1, "Bridgewater Hall", 200, "Manchester", "Lower Mosley Street")

            assertTrue(service.addExistingVenue(venue1))
            assertFalse(service.addExistingVenue(venue2), "Should reject duplicate ID")
            assertEquals(1, service.getAllVenues().size)
            println("Test passed: Duplicate ID rejected correctly")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test remove venue without events")
    fun testRemoveVenueWithoutEvents() {
        println("\nTEST : Testing remove venue without events")
        try {
            val venue = service.addVenue("Community Hall", "25 Park Lane", "London", 100)

            val result = service.removeVenue(venue.id, hasEvents = false)

            assertTrue(result, "Should successfully remove venue")
            assertEquals(0, service.getAllVenues().size)
            println("Test passed: Venue removed successfully")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test remove venue with events fails")
    fun testRemoveVenueWithEvents() {
        println("\nTEST : Testing remove venue with events fails")
        try {
            val venue = service.addVenue("Event Venue", "456 Event St", "Manchester", 250)

            val result = service.removeVenue(venue.id, hasEvents = true)

            assertFalse(result, "Should not remove venue with events")
            assertEquals(1, service.getAllVenues().size)
            println("Test passed: Venue with events protected from deletion")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test update venue successfully")
    fun testUpdateVenueSuccessfully() {
        println("\nTEST : Testing update venue successfully")
        try {
            val venue = service.addVenue("Old Town Hall", "45 Heritage Street", "York", 100, listOf("WiFi"))

            val result = service.updateVenue(
                venueId = venue.id,
                name = "Renovated Town Hall",
                address = "45 Heritage Street West Wing",
                city = "York",
                capacity = 200,
                facilities = listOf("WiFi", "Projector", "AC")
            )

            assertTrue(result, "Should successfully update venue")

            val updated = service.getVenue(venue.id)
            assertEquals("Renovated Town Hall", updated?.name)
            assertEquals(200, updated?.capacity)
            println("Test passed: Venue updated: ${updated?.name}, capacity: ${updated?.capacity}")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test get all venues")
    fun testGetAllVenues() {
        println("\nTEST : Testing get all venues")
        try {
            assertEquals(0, service.getAllVenues().size)

            service.addVenue("Grand Hall", "789 Grand Ave", "Birmingham", 150)
            service.addVenue("Small Room", "321 Compact Rd", "Liverpool", 50)
            service.addVenue("Exhibition Center", "555 Display St", "Leeds", 400)

            val allVenues = service.getAllVenues()

            assertEquals(3, allVenues.size)
            println("Test passed: Retrieved ${allVenues.size} venues")
        } catch (e: Exception) {
            System.err.println("Test failed: ${e.message}")
            fail("Should not throw exception: ${e.message}")
        }
    }
}
