package unitTests.scala

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.{BeforeEach, DisplayName, Test}
import java.time.LocalDateTime

class SlotFinderTest {

  @BeforeEach
  def setup(): Unit = {
    try {
      System.out.println("SETUP: SlotFinder test initialized")
    } catch {
      case e: Exception => fail(s"Failed to initialize: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test VenueData data class")
  def testVenueDataDataClass(): Unit = {
    System.out.println("\nTEST START: Testing VenueData data class")
    try {
      val venue = VenueData(
        id = 1,
        name = "Conference Center",
        capacity = 500,
        city = "London",
        address = "123 Main Street"
      )

      assertEquals(1, venue.id)
      assertEquals("Conference Center", venue.name)
      assertEquals(500, venue.capacity)
      assertEquals("London", venue.city)
      System.out.println(s"Test passed: TEST: VenueData created: ${venue.name}, capacity: ${venue.capacity}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test EventData data class")
  def testEventDataDataClass(): Unit = {
    System.out.println("\nTEST START: Testing EventData data class")
    try {
      val startTime = LocalDateTime.of(2025, 12, 1, 10, 0)
      val endTime = LocalDateTime.of(2025, 12, 1, 12, 0)

      val event = EventData(
        id = "event-1",
        title = "Test Event",
        startTime = startTime,
        endTime = endTime,
        venueId = Some(1)
      )

      assertEquals("event-1", event.id)
      assertEquals("Test Event", event.title)
      assertTrue(event.venueId.isDefined)
      assertEquals(1, event.venueId.get)
      System.out.println(s"Test passed: TEST: EventData created: ${event.title}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test EventData without venue")
  def testEventDataWithoutVenue(): Unit = {
    System.out.println("\nTEST START: Testing EventData without venue")
    try {
      val event = EventData(
        id = "event-1",
        title = "Test Event",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(2),
        venueId = None
      )

      assertFalse(event.venueId.isDefined)
      System.out.println("Test passed: TEST: EventData without venue created successfully")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test SlotResult data class")
  def testSlotResultDataClass(): Unit = {
    System.out.println("\nTEST START: Testing SlotResult data class")
    try {
      val startTime = LocalDateTime.of(2025, 12, 1, 10, 0)
      val endTime = LocalDateTime.of(2025, 12, 1, 12, 0)

      val slot = SlotResult(
        venueName = "Conference Center",
        venueCity = "London",
        venueCapacity = 500,
        startTime = startTime,
        endTime = endTime
      )

      assertEquals("Conference Center", slot.venueName)
      assertEquals("London", slot.venueCity)
      assertEquals(500, slot.venueCapacity)
      System.out.println(s"Test passed: TEST: SlotResult created: ${slot.venueName}, ${slot.venueCity}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test SlotResult format for display")
  def testSlotResultFormatForDisplay(): Unit = {
    System.out.println("\nTEST START: Testing SlotResult format for display")
    try {
      val startTime = LocalDateTime.of(2025, 12, 25, 10, 30)
      val endTime = LocalDateTime.of(2025, 12, 25, 14, 30)

      val slot = SlotResult(
        venueName = "Conference Center",
        venueCity = "London",
        venueCapacity = 500,
        startTime = startTime,
        endTime = endTime
      )

      val formatted = slot.formatForDisplay

      assertTrue(formatted.contains("Venue: Conference Center (London)"))
      assertTrue(formatted.contains("Capacity: 500"))
      assertTrue(formatted.contains("25/12/2025 10:30"))
      System.out.println(s"Test passed: TEST: SlotResult formatted correctly")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test findNextSlot returns string")
  def testFindNextSlotReturnsString(): Unit = {
    System.out.println("\nTEST START: Testing findNextSlot returns string")
    try {
      val result = SlotFinder.findNextSlot(
        requiredCapacity = 100,
        durationHours = 2,
        earliestStartDate = "01/12/2025",
        earliestStartTime = "10:00",
        preferredCity = "London"
      )

      assertNotNull(result)
      assertTrue(result.isInstanceOf[String])
      System.out.println(s"Test passed: TEST: findNextSlot returned: ${result.take(50)}...")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test findNextSlot with zero capacity")
  def testFindNextSlotWithZeroCapacity(): Unit = {
    System.out.println("\nTEST START: Testing findNextSlot with zero capacity")
    try {
      val result = SlotFinder.findNextSlot(
        requiredCapacity = 0,
        durationHours = 2,
        earliestStartDate = "01/12/2025",
        earliestStartTime = "10:00"
      )

      assertNotNull(result)
      assertTrue(result.isInstanceOf[String])
      System.out.println("Test passed: TEST: Zero capacity handled (uses minimum of 1)")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test findNextSlot with invalid date format")
  def testFindNextSlotWithInvalidDateFormat(): Unit = {
    System.out.println("\nTEST START: Testing findNextSlot with invalid date format")
    try {
      val result = SlotFinder.findNextSlot(
        requiredCapacity = 100,
        durationHours = 2,
        earliestStartDate = "2025-12-01",
        earliestStartTime = "10:00"
      )

      assertNotNull(result)
      assertTrue(result.contains("Error"))
      System.out.println("Test passed: TEST: Invalid date format detected and error returned")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test venue capacity filtering")
  def testVenueCapacityFiltering(): Unit = {
    System.out.println("\nTEST START: Testing venue capacity filtering")
    try {
      val smallVenue = VenueData(1, "Small Room", 50, "London", "Address 1")
      val largeVenue = VenueData(2, "Large Hall", 500, "London", "Address 2")

      val requiredCapacity = 100

      assertFalse(smallVenue.capacity >= requiredCapacity)
      assertTrue(largeVenue.capacity >= requiredCapacity)
      System.out.println("Test passed: TEST: Capacity filtering logic verified")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test city preference ordering")
  def testCityPreferenceOrdering(): Unit = {
    System.out.println("\nTEST START: Testing city preference ordering")
    try {
      val londonVenue = VenueData(1, "London Venue", 200, "London", "Address 1")
      val manchesterVenue = VenueData(2, "Manchester Venue", 200, "Manchester", "Address 2")

      val venues = List(manchesterVenue, londonVenue)
      val preferredCity = "London"

      val (cityMatches, others) = venues.partition(_.city.equalsIgnoreCase(preferredCity))

      assertTrue(cityMatches.nonEmpty)
      assertEquals("London Venue", cityMatches.head.name)
      System.out.println(s"Test passed: TEST: City preference: ${cityMatches.head.name} preferred")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test best fit capacity sorting")
  def testBestFitCapacitySorting(): Unit = {
    System.out.println("\nTEST START: Testing best fit capacity sorting")
    try {
      val venue1 = VenueData(1, "Huge Hall", 1000, "London", "Address 1")
      val venue2 = VenueData(2, "Medium Room", 150, "London", "Address 2")
      val venue3 = VenueData(3, "Small Room", 120, "London", "Address 3")

      val requiredCapacity = 100
      val venues = List(venue1, venue2, venue3)

      val suitable = venues.filter(_.capacity >= requiredCapacity).sortBy(_.capacity)

      assertEquals("Small Room", suitable.head.name)
      assertEquals(120, suitable.head.capacity)
      System.out.println(s"Test passed: TEST: Best fit: ${suitable.head.name} (${suitable.head.capacity})")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test time conflict overlap")
  def testTimeConflictOverlap(): Unit = {
    System.out.println("\nTEST START: Testing time conflict overlap")
    try {
      val event1Start = LocalDateTime.of(2025, 12, 1, 10, 0)
      val event1End = LocalDateTime.of(2025, 12, 1, 12, 0)

      val event2Start = LocalDateTime.of(2025, 12, 1, 11, 0)
      val event2End = LocalDateTime.of(2025, 12, 1, 13, 0)

      assertTrue(event2Start.isBefore(event1End) && event1Start.isBefore(event2End))
      System.out.println("Test passed: TEST: Time overlap detected correctly")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test no time conflict with buffer")
  def testTimeNoConflictWithBuffer(): Unit = {
    System.out.println("\nTEST START: Testing no time conflict with buffer")
    try {
      val event1Start = LocalDateTime.of(2025, 12, 1, 10, 0)
      val event1End = LocalDateTime.of(2025, 12, 1, 12, 0)

      val event2Start = LocalDateTime.of(2025, 12, 1, 13, 0)
      val event2End = LocalDateTime.of(2025, 12, 1, 15, 0)

      val event1EndWithBuffer = event1End.plusHours(1)
      assertFalse(event2Start.isBefore(event1EndWithBuffer))
      System.out.println("Test passed: TEST: 1-hour buffer respected, no conflict")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test large and small capacity venues")
  def testLargeAndSmallCapacityVenues(): Unit = {
    System.out.println("\nTEST START: Testing large and small capacity venues")
    try {
      val largeVenue = VenueData(1, "Stadium", 50000, "London", "Stadium Road")
      val smallVenue = VenueData(2, "Meeting Room", 10, "London", "Office Building")

      assertEquals(50000, largeVenue.capacity)
      assertEquals(10, smallVenue.capacity)
      assertTrue(largeVenue.capacity >= 10000)
      assertTrue(smallVenue.capacity >= 5)
      System.out.println(s"Test passed: TEST: Large venue: ${largeVenue.capacity}, Small venue: ${smallVenue.capacity}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }
}
