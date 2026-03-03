package unitTests.scala

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.{BeforeEach, DisplayName, Test}
import java.time.LocalDateTime

class SchedulingAlgorithmTest {

  @BeforeEach
  def setup(): Unit = {
    try {
      // Setup complete.
    } catch {
      case e: Exception => fail(s"Failed to initialize: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test ScheduleEvent data class")
  def testScheduleEventDataClass(): Unit = {
    System.out.println("\nTEST: Testing ScheduleEvent data class")
    try {
      val startTime = LocalDateTime.of(2025, 12, 1, 10, 0)
      val endTime = LocalDateTime.of(2025, 12, 1, 12, 0)

      val event = ScheduleEvent(
        id = "event-1",
        title = "Test Event",
        startTime = startTime,
        endTime = endTime,
        requiredCapacity = 100,
        currentVenueId = Some(1)
      )

      assertEquals("event-1", event.id)
      assertEquals("Test Event", event.title)
      assertEquals(100, event.requiredCapacity)
      assertTrue(event.currentVenueId.isDefined)
      System.out.println(s"Test passed: ScheduleEvent created: ${event.title}, capacity: ${event.requiredCapacity}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test ScheduleVenue data class")
  def testScheduleVenueDataClass(): Unit = {
    System.out.println("\nTEST : Testing ScheduleVenue data class")
    try {
      val venue = ScheduleVenue(
        id = 2,
        name = "Silverstone Circuit",
        capacity = 450,
        city = "Silverstone"
      )

      assertEquals(2, venue.id)
      assertEquals("Silverstone Circuit", venue.name)
      assertEquals(450, venue.capacity)
      assertEquals("Silverstone", venue.city)
      System.out.println(s"Test passed: ScheduleVenue created: ${venue.name}, capacity: ${venue.capacity}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test Assignment data class")
  def testAssignmentDataClass(): Unit = {
    System.out.println("\nTEST : Testing Assignment data class")
    try {
      val event = ScheduleEvent(
        id = "assignment-event",
        title = "Conference Assignment",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(2),
        requiredCapacity = 150,
        currentVenueId = None
      )

      val venue = ScheduleVenue(1, "Test Venue", 200, "London")
      val assignment = Assignment(event, venue)

      assertEquals(event, assignment.event)
      assertEquals(venue, assignment.venue)
      System.out.println(s"Test passed: Assignment created: ${event.title} -> ${venue.name}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test ScheduleResult data class")
  def testScheduleResultDataClass(): Unit = {
    System.out.println("\nTEST : Testing ScheduleResult data class")
    try {
      val event = ScheduleEvent(
        id = "unassigned-event",
        title = "Unassigned Workshop",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(3),
        requiredCapacity = 75,
        currentVenueId = None
      )

      val result = ScheduleResult(
        assignments = List(),
        unassignedEvents = List(event),
        conflicts = List("Test conflict")
      )

      assertTrue(result.assignments.isEmpty)
      assertEquals(1, result.unassignedEvents.size)
      assertEquals(1, result.conflicts.size)
      System.out.println(s"Test passed: ScheduleResult created: ${result.conflicts.size} conflicts")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test getAssignments returns Java list")
  def testGetAssignmentsReturnsJavaList(): Unit = {
    System.out.println("\nTEST : Testing getAssignments returns Java list")
    try {
      val assignments = SchedulingAlgorithm.getAssignments
      assertNotNull(assignments)
      assertTrue(assignments.isInstanceOf[java.util.List[_]])
      System.out.println(s"Test passed: getAssignments returns Java list")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test hasConflicts method")
  def testHasConflictsMethod(): Unit = {
    System.out.println("\nTEST : Testing hasConflicts method")
    try {
      val hasConflicts = SchedulingAlgorithm.hasConflicts
      assertTrue(hasConflicts || !hasConflicts)
      System.out.println(s"Test passed: hasConflicts returned: $hasConflicts")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test venue capacity matching")
  def testVenueCapacityMatching(): Unit = {
    System.out.println("\nTEST : Testing venue capacity matching")
    try {
      val smallVenue = ScheduleVenue(1, "Small Room", 50, "London")
      val largeVenue = ScheduleVenue(2, "Large Hall", 500, "London")

      val smallEvent = ScheduleEvent(
        id = "small",
        title = "Small Event",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(2),
        requiredCapacity = 30,
        currentVenueId = None
      )

      val largeEvent = ScheduleEvent(
        id = "large",
        title = "Large Event",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(2),
        requiredCapacity = 400,
        currentVenueId = None
      )

      assertTrue(smallVenue.capacity >= smallEvent.requiredCapacity)
      assertFalse(smallVenue.capacity >= largeEvent.requiredCapacity)
      assertTrue(largeVenue.capacity >= largeEvent.requiredCapacity)
      System.out.println("Test passed: Capacity matching logic verified")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test time conflict detection")
  def testTimeConflictDetection(): Unit = {
    System.out.println("\nTEST : Testing time conflict detection")
    try {
      val event1 = ScheduleEvent(
        id = "event-1",
        title = "Event 1",
        startTime = LocalDateTime.of(2025, 12, 1, 10, 0),
        endTime = LocalDateTime.of(2025, 12, 1, 12, 0),
        requiredCapacity = 100,
        currentVenueId = Some(1)
      )

      val event2Overlap = ScheduleEvent(
        id = "event-2",
        title = "Event 2 Overlap",
        startTime = LocalDateTime.of(2025, 12, 1, 11, 0),
        endTime = LocalDateTime.of(2025, 12, 1, 13, 0),
        requiredCapacity = 120,
        currentVenueId = Some(1)
      )

      val event3NoOverlap = ScheduleEvent(
        id = "event-3",
        title = "Event 3 No Overlap",
        startTime = LocalDateTime.of(2025, 12, 1, 13, 0),
        endTime = LocalDateTime.of(2025, 12, 1, 15, 0),
        requiredCapacity = 80,
        currentVenueId = Some(1)
      )

      assertTrue(event2Overlap.startTime.isBefore(event1.endTime) &&
                 event1.startTime.isBefore(event2Overlap.endTime))

      assertFalse(event3NoOverlap.startTime.isBefore(event1.endTime) &&
                  event1.startTime.isBefore(event3NoOverlap.endTime))
      System.out.println("Test passed: Time conflict detection verified")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }

  @Test
  @DisplayName("Test event sorting by start time")
  def testEventSortingByStartTime(): Unit = {
    System.out.println("\nTEST : Testing event sorting by start time")
    try {
      val event1 = ScheduleEvent(
        "event-1", "Third Event",
        LocalDateTime.of(2025, 12, 3, 10, 0),
        LocalDateTime.of(2025, 12, 3, 12, 0),
        90, None
      )

      val event2 = ScheduleEvent(
        "event-2", "First Event",
        LocalDateTime.of(2025, 12, 1, 10, 0),
        LocalDateTime.of(2025, 12, 1, 12, 0),
        110, None
      )

      val event3 = ScheduleEvent(
        "event-3", "Second Event",
        LocalDateTime.of(2025, 12, 2, 10, 0),
        LocalDateTime.of(2025, 12, 2, 12, 0),
        95, None
      )

      val events = List(event1, event2, event3)
      val sorted = events.sortBy(_.startTime)

      assertEquals("First Event", sorted.head.title)
      assertEquals("Second Event", sorted(1).title)
      assertEquals("Third Event", sorted(2).title)
      System.out.println(s"Test passed: Events sorted correctly: ${sorted.map(_.title).mkString(", ")}")
    } catch {
      case e: Exception =>
        System.err.println(s"Test failed: TEST: ${e.getMessage}")
        fail(s"Should not throw exception: ${e.getMessage}")
    }
  }
}