package LogicHandling.HandlingServices

import LogicHandling.Venue


//Manages the venue as the parent node (multiple venue.kt instances)
class VenueHandler {
    private val venues: MutableList<Venue> = mutableListOf()
    private var nextVenueId = 1

    fun addVenue(
        name: String,
        address: String,
        city: String,
        capacity: Int,
        facilities: List<String> = emptyList()
    ): Venue {
        val venue = Venue(
            id = nextVenueId++,
            name = name,
            capacity = capacity,
            city = city,
            address = address,
            facilities = facilities
        )
        venues.add(venue)
        return venue
    }

    fun addExistingVenue(venue: Venue): Boolean {
        if (venues.any { it.id == venue.id }) {
            return false
        }
        venues.add(venue)
        if (venue.id >= nextVenueId) {
            nextVenueId = venue.id + 1
        }
        return true
    }

    fun removeVenue(venueId: Int, hasEvents: Boolean): Boolean {
        if (hasEvents) {
            return false
        }
        return venues.removeIf { it.id == venueId }
    }

    fun updateVenue(
        venueId: Int,
        name: String,
        address: String,
        city: String,
        capacity: Int,
        facilities: List<String>
    ): Boolean {
        val venue = getVenue(venueId) ?: return false
        venue.name = name
        venue.address = address
        venue.city = city
        venue.capacity = capacity
        venue.facilities = facilities
        return true
    }

    fun getVenue(venueId: Int): Venue? = venues.find { it.id == venueId }

    fun getAllVenues(): List<Venue> = venues.toList()
    
}
