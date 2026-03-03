package scala

import com.google.gson.JsonParser

import java.io.File
import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.jdk.CollectionConverters.*

// Scheduling Algorithm - Creates a schedule by assigning events to venues
// Reads events and venues from JSON files and finds a schedule without conflicts

// ==================== Data Types ====================

case class ScheduleEvent(
  id: String,
  title: String,
  startTime: LocalDateTime,
  endTime: LocalDateTime,
  requiredCapacity: Int,
  currentVenueId: Option[Int]
)

case class ScheduleVenue(
  id: Int,
  name: String,
  capacity: Int,
  city: String
)

case class Assignment(
  event: ScheduleEvent,
  venue: ScheduleVenue
)

case class ScheduleResult(
  assignments: List[Assignment],
  unassignedEvents: List[ScheduleEvent],
  conflicts: List[String]
)

// ==================== Scheduling Algorithm ====================

object SchedulingAlgorithm {

  private val dataDirectory = new File("data")
  private val venuesFile = new File(dataDirectory, "venues.json")
  private val eventsFile = new File(dataDirectory, "events.json")
  private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  // Creates a schedule by reading from files and assigning venues to events
  def generateSchedule(): ScheduleResult = {
    val venues = loadVenuesFromJson()
    val events = loadEventsFromJson()

    scheduleEvents(events, venues)
  }

  // Assigns venues to events by trying to find the best match for each event
  private def scheduleEvents(
                              events: List[ScheduleEvent],
                              venues: List[ScheduleVenue]
                            ): ScheduleResult = {

    if (venues.isEmpty) {
      return ScheduleResult(
        assignments = List.empty,
        unassignedEvents = events,
        conflicts = List("No venues available in the system")
      )
    }

    // Sort events by start time (earliest first) for greedy scheduling
    val sortedEvents = events.sortBy(_.startTime)

    // Track assignments and venue bookings
    val initialState = SchedulingState(
      assignments = List.empty,
      venueBookings = Map.empty.withDefaultValue(List.empty),
      unassigned = List.empty,
      conflicts = List.empty
    )

    // Process each event using foldLeft (functional approach)
    val finalState = sortedEvents.foldLeft(initialState) { (state, event) =>
      assignEventToVenue(event, venues, state)
    }

    ScheduleResult(
      assignments = finalState.assignments.reverse,
      unassignedEvents = finalState.unassigned.reverse,
      conflicts = finalState.conflicts.reverse
    )
  }

  // Keeps track of assignments and venue bookings while scheduling
  private case class SchedulingState(
                                      assignments: List[Assignment],
                                      venueBookings: Map[Int, List[(LocalDateTime, LocalDateTime)]],
                                      unassigned: List[ScheduleEvent],
                                      conflicts: List[String]
                                    )

  // Tries to find a venue that works for this event
  private def assignEventToVenue(
                                  event: ScheduleEvent,
                                  venues: List[ScheduleVenue],
                                  state: SchedulingState
                                ): SchedulingState = {

    // Filter venues by capacity and sort by best fit (smallest suitable venue first)
    val suitableVenues = venues
      .filter(_.capacity >= event.requiredCapacity)
      .sortBy(_.capacity)

    if (suitableVenues.isEmpty) {
      val maxCapacity = venues.map(_.capacity).max
      return state.copy(
        unassigned = event :: state.unassigned,
        conflicts = s"Event '${event.title}' requires ${event.requiredCapacity} capacity, max available is $maxCapacity" :: state.conflicts
      )
    }

    // Find first venue without conflict
    findAvailableVenue(event, suitableVenues, state.venueBookings) match {
      case Some(venue) =>
        val newBooking = (event.startTime, event.endTime)
        val updatedBookings = state.venueBookings.updated(
          venue.id,
          newBooking :: state.venueBookings(venue.id)
        )
        state.copy(
          assignments = Assignment(event, venue) :: state.assignments,
          venueBookings = updatedBookings
        )

      case None =>
        state.copy(
          unassigned = event :: state.unassigned,
          conflicts = s"Event '${event.title}' conflicts with existing bookings at all suitable venues" :: state.conflicts
        )
    }
  }

  // Finds the first venue that is free during the event time
  private def findAvailableVenue(
                                  event: ScheduleEvent,
                                  venues: List[ScheduleVenue],
                                  bookings: Map[Int, List[(LocalDateTime, LocalDateTime)]]
                                ): Option[ScheduleVenue] = {

    venues.find { venue =>
      val venueBookings = bookings.getOrElse(venue.id, List.empty)
      !hasTimeConflict(event.startTime, event.endTime, venueBookings)
    }
  }

  // Checks if a time slot overlaps with any existing bookings
  private def hasTimeConflict(
                               start: LocalDateTime,
                               end: LocalDateTime,
                               bookings: List[(LocalDateTime, LocalDateTime)]
                             ): Boolean = {
    bookings.exists { case (bookStart, bookEnd) =>
      start.isBefore(bookEnd) && bookStart.isBefore(end)
    }
  }

  // ==================== JSON Loading ====================

  private def loadVenuesFromJson(): List[ScheduleVenue] = {
    if (!venuesFile.exists()) return List.empty

    var source: scala.io.BufferedSource = null
    try {
      source = scala.io.Source.fromFile(venuesFile)
      val jsonContent = source.mkString
      val jsonArray = JsonParser.parseString(jsonContent).getAsJsonArray

      jsonArray.asScala.toList.map { element =>
        val obj = element.getAsJsonObject
        ScheduleVenue(
          id = obj.get("id").getAsInt,
          name = obj.get("name").getAsString,
          capacity = obj.get("capacity").getAsInt,
          city = if (obj.has("city") && !obj.get("city").isJsonNull)
            obj.get("city").getAsString else ""
        )
      }
    } catch {
      case e: Exception =>
        println(s"Error loading venues: ${e.getMessage}")
        List.empty
    } finally {
      if (source != null) source.close()
    }
  }

  private def loadEventsFromJson(): List[ScheduleEvent] = {
    if (!eventsFile.exists()) return List.empty

    var source: scala.io.BufferedSource = null
    try {
      source = scala.io.Source.fromFile(eventsFile)
      val jsonContent = source.mkString
      val jsonArray = JsonParser.parseString(jsonContent).getAsJsonArray

      jsonArray.asScala.toList.map { element =>
        val obj = element.getAsJsonObject
        ScheduleEvent(
          id = obj.get("id").getAsString,
          title = obj.get("title").getAsString,
          startTime = LocalDateTime.parse(obj.get("startTime").getAsString, dateTimeFormatter),
          endTime = LocalDateTime.parse(obj.get("endTime").getAsString, dateTimeFormatter),
          requiredCapacity = if (obj.has("estimatedAttendees") && !obj.get("estimatedAttendees").isJsonNull)
            obj.get("estimatedAttendees").getAsInt else 1,
          currentVenueId = if (obj.has("venueId") && !obj.get("venueId").isJsonNull)
            Some(obj.get("venueId").getAsInt) else None
        )
      }
    } catch {
      case e: Exception =>
        println(s"Error loading events: ${e.getMessage}")
        List.empty
    } finally {
      if (source != null) source.close()
    }
  }

  // ==================== Java/UI Integration ====================

  // Creates a schedule and formats it as text for display
  def generateScheduleForUI(): String = {
    val result = generateSchedule()
    formatScheduleResult(result)
  }

  // Converts the schedule into readable text
  private def formatScheduleResult(result: ScheduleResult): String = {
    val sb = new StringBuilder()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    sb.append("A Conflict Free schedule\n\n")

    if (result.assignments.nonEmpty) {
      sb.append(s"Successfully Scheduled: ${result.assignments.size} event(s)\n")
      sb.append("-" * 40 + "\n")

      result.assignments.foreach { assignment =>
        sb.append(s"Event: ${assignment.event.title}\n")
        sb.append(s"  Time: ${assignment.event.startTime.format(dateFormatter)} - ${assignment.event.endTime.format(dateFormatter)}\n")
        sb.append(s"  Venue: ${assignment.venue.name} (Capacity: ${assignment.venue.capacity})\n")
        sb.append("\n")
      }
    }

    if (result.unassignedEvents.nonEmpty) {
      sb.append(s"\nUnassigned Events: ${result.unassignedEvents.size}\n")
      sb.append("-" * 40 + "\n")

      result.unassignedEvents.foreach { event =>
        sb.append(s"  - ${event.title}\n")
      }
    }

    if (result.conflicts.nonEmpty) {
      sb.append(s"\nConflicts/Issues: ${result.conflicts.size}\n")
      sb.append("-" * 40 + "\n")

      result.conflicts.foreach { conflict =>
        sb.append(s"  ! $conflict\n")
      }
    }

    if (result.assignments.isEmpty && result.unassignedEvents.isEmpty) {
      sb.append("No events to schedule.\n")
    }

    sb.toString()
  }

  // Returns a list of event-to-venue assignments
  // Each entry contains: eventId, venueId, venueName
  def getAssignments: java.util.List[Array[String]] = {
    val result = generateSchedule()
    result.assignments.map { a =>
      Array(a.event.id, a.venue.id.toString, a.venue.name)
    }.asJava
  }

  // Checks if there are any problems with the schedule
  def hasConflicts: Boolean = {
    val result = generateSchedule()
    result.conflicts.nonEmpty || result.unassignedEvents.nonEmpty
  }
}