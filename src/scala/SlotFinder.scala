package scala

import com.google.gson.*

import java.io.{File, FileReader}
import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*

// This reads venue and event data from JSON files to find the earliest time
// and venue that's available, also takes city into consideration the data type definitions
case class VenueData(
                      id: Int,
                      name: String,
                      capacity: Int, 
                      city: String,
                      address: String
                    )

case class EventData(
                      id: String,
                      title: String,
                      startTime: LocalDateTime,
                      endTime: LocalDateTime,
                      venueId: Option[Int]
                    )

case class SlotResult(
                       venueName: String,
                       venueCity: String,
                       venueCapacity: Int,
                       startTime: LocalDateTime,
                       endTime: LocalDateTime
                     ) {
  def formatForDisplay: String = {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    s"Venue: $venueName ($venueCity)\n" +
      s"Capacity: $venueCapacity\n" +
      s"Available: ${startTime.format(formatter)} - ${endTime.format(formatter)}"
  }
}

// ========== The main slot finder logic =====

object SlotFinder {

  private val dataDirectory = new File("data")
  private val venuesFile = new File(dataDirectory, "venues.json")
  private val eventsFile = new File(dataDirectory, "events.json")
  private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME


  // This is where everything starts - the button on the UI calls this to find a free slot
  private def findNextAvailableSlot(
                             requiredCapacity: Int,
                             earliestStart: LocalDateTime,
                             durationHours: Int,
                             preferredCity: Option[String] = None
                           ): Either[String, SlotResult] = {

    // Firstly load the venues and events from our JSON files
    val venues = loadVenuesFromJson()
    val events = loadEventsFromJson()

    // then find an available slot with all that data
    findSlot(venues, events, requiredCapacity, earliestStart, durationHours, preferredCity)
  }

    // Find a slot - this handles city preferences too
   // Calls recursive functions to search through venues and time slots
  private def findSlot(
                        venues: List[VenueData],
                        events: List[EventData],
                        requiredCapacity: Int,
                        earliestStart: LocalDateTime,
                        durationHours: Int,
                        preferredCity: Option[String]
                      ): Either[String, SlotResult] = {

    if (venues.isEmpty) {
      return Left("No venues available in the system")
    }

    // filter out any venues that are too small for our event
    val suitableVenues = venues.filter(_.capacity >= requiredCapacity)

    if (suitableVenues.isEmpty) {
      val maxCapacity = venues.map(_.capacity).max
      return Left(s"No venue has capacity for $requiredCapacity. Maximum available: $maxCapacity")
    }

    // Put venues in order - preferred city first, then sorted by capacity (best fit to the required capacity)
    val sortedVenues = preferredCity match {
      case Some(city) =>
        val (cityMatches, others) = suitableVenues.partition(_.city.equalsIgnoreCase(city))
        cityMatches.sortBy(_.capacity) ++ others.sortBy(_.capacity)
      case None =>
        suitableVenues.sortBy(_.capacity)
    }

    val duration = Duration.ofHours(durationHours)
    val searchEndTime = earliestStart.plusDays(30)

    // Alright, let's start searching for the first free slot
    findAvailableSlot(sortedVenues, events, earliestStart, searchEndTime, duration)
  }

  // ==================== JSON Loading ====================

  // Load venues from JSON file
  private def loadVenuesFromJson(): List[VenueData] = {
    if (!venuesFile.exists()) return List.empty

    var source: scala.io.BufferedSource = null
    try {
      source = scala.io.Source.fromFile(venuesFile)
      val jsonContent = source.mkString
      val jsonArray = JsonParser.parseString(jsonContent).getAsJsonArray

      jsonArray.asScala.toList.map { element =>
        val obj = element.getAsJsonObject
        VenueData(
          id = obj.get("id").getAsInt,
          name = obj.get("name").getAsString,
          capacity = obj.get("capacity").getAsInt,
          city = obj.get("city").getAsString,
          address = if (obj.has("address") && !obj.get("address").isJsonNull)
            obj.get("address").getAsString else ""
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

  // Load events from JSON file
  private def loadEventsFromJson(): List[EventData] = {
    if (!eventsFile.exists()) return List.empty

    var source: scala.io.BufferedSource = null
    try {
      source = scala.io.Source.fromFile(eventsFile)
      val jsonContent = source.mkString
      val jsonArray = JsonParser.parseString(jsonContent).getAsJsonArray

      jsonArray.asScala.toList.map { element =>
        val obj = element.getAsJsonObject
        EventData(
          id = obj.get("id").getAsString,
          title = obj.get("title").getAsString,
          startTime = LocalDateTime.parse(obj.get("startTime").getAsString, dateTimeFormatter),
          endTime = LocalDateTime.parse(obj.get("endTime").getAsString, dateTimeFormatter),
          venueId = if (obj.has("venueId") && !obj.get("venueId").isJsonNull)
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
    /**
   * We search through different time slots, checking if any venues are free at each moment.
   * Find something at a requested time before looking at later dates. (earliest date)
   */
  private def findAvailableSlot(
                                 venues: List[VenueData],
                                 events: List[EventData],
                                 searchStart: LocalDateTime,
                                 searchEnd: LocalDateTime,
                                 duration: Duration
                               ): Either[String,SlotResult] = {

    // We check time slots one by one instead of going venue by venue
    // That way we'll find something at your preferred time before suggesting later dates
    searchByTimeSlot(venues, events, searchStart, searchEnd, duration)
  }

  /**
   * RECURSION: Walks through time slots hour by hour
   * At each time slot, checks all venues for availability
   * Uses tail recursion (optimized by @tailrec annotation, prevent stack overflow)
   */
  @tailrec
  private def searchByTimeSlot(
                                venues: List[VenueData],
                                events: List[EventData],
                                currentTime: LocalDateTime,
                                searchEnd: LocalDateTime,
                                duration: Duration
                              ): Either[String,SlotResult] = {

    if (currentTime.isAfter(searchEnd)) {
      // Base case: reached end of search period
      Left("All suitable venues are booked during the search period")
    } else {
      val slotEnd = currentTime.plus(duration)

      // Check all venues for availability at this specific time
      findAvailableVenueAtTime(venues, events, currentTime, slotEnd) match {
        case Some(slot) =>
          // Found a venue at this time - return it
          Right(slot)
        case None =>
          // No venue available at this time - try next hour
          // RECURSION: Call ourselves again with the next hour
          searchByTimeSlot(venues, events, currentTime.plusHours(1), searchEnd, duration)
      }
    }
  }

  /**
   * RECURSION: Checks all venues to find one available at the specified time
   * Returns the first available venue (already sorted by best-fit)
   * Uses tail recursion
   */
  @tailrec
  private def findAvailableVenueAtTime(
                                        venues: List[VenueData],
                                        events: List[EventData],
                                        slotStart: LocalDateTime,
                                        slotEnd: LocalDateTime
                                      ): Option[SlotResult] = {

    venues match {
      case Nil => None
      case venue :: remainingVenues =>
        // Get bookings for this venue
        val venueBookings = events
          .filter(_.venueId.contains(venue.id))
          .map(e => (e.startTime, e.endTime))

        // Check if this venue is available at this time
        if (!hasTimeConflict(venueBookings, slotStart, slotEnd)) {
          // This venue is available - return it
          Some(SlotResult(venue.name, venue.city, venue.capacity, slotStart, slotEnd))
        } else {
          // This venue is not available - check remaining venues
          // RECURSION: Call ourselves again with the remaining venues
          findAvailableVenueAtTime(remainingVenues, events, slotStart, slotEnd)
        }
    }
  }

  /**
   * RECURSION: Checks if a time slot conflicts with existing bookings
   * Uses non-tail recursion (because we need the result of the recursive call for the OR operation)
   * Includes 1-hour buffer after each booking to simulate real life transitions/cleaning/setup/teardown
   */
  private def hasTimeConflict(
                               bookings: List[(LocalDateTime, LocalDateTime)],
                               slotStart: LocalDateTime,
                               slotEnd: LocalDateTime
                             ): Boolean = {
    bookings match {
      case Nil => false
      case (bookStart, bookEnd) :: remainingBookings =>
        // Add 1-hour buffer after each booking to ensure transition time
        val bookEndWithBuffer = bookEnd.plusHours(1)
        val currentConflict = slotStart.isBefore(bookEndWithBuffer) && bookStart.isBefore(slotEnd)
        // RECURSION: Check if current booking conflicts OR if any remaining bookings conflict
        // The OR operation after the recursive call makes this non-tail recursive
        currentConflict || hasTimeConflict(remainingBookings, slotStart, slotEnd)
    }
  }
  // ==================== Java/UI Integration ====================

  /**
   * Find next available slot
   * Called directly from Java Swing UI
   * Returns formatted string for display, or error message
   */
  def findNextSlot(
                    requiredCapacity: Int,
                    durationHours: Int,
                    earliestStartDate: String,  // Format: dd/MM/yyyy
                    earliestStartTime: String,   // Format: HH:mm
                    preferredCity: String = ""   // Optional city preference
                  ): String = {

    try {
      // Handle 0 capacity - use 1 as minimum to find any venue
      val capacity = if (requiredCapacity <= 0) 1 else requiredCapacity
      val duration = if (durationHours <= 0) 1 else durationHours

      val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

      val date = java.time.LocalDate.parse(earliestStartDate, dateFormatter)
      val time = java.time.LocalTime.parse(earliestStartTime, timeFormatter)
      val earliestStart = LocalDateTime.of(date, time)

      val cityOption = if (preferredCity != null && preferredCity.trim.nonEmpty) {
        Some(preferredCity.trim)
      } else {
        None
      }

      findNextAvailableSlot(capacity, earliestStart, duration, cityOption) match {
        case Right(slot) => slot.formatForDisplay
        case Left(reason) => s"No slot available: $reason"
      }
    } catch {
      case e: Exception => s"Error: ${e.getMessage}"
    }
  }
}