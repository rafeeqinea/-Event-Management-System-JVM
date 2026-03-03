package LogicHandling.HandlingServices

import LogicHandling.Attendee

// Service responsible for Attendee CRUD operations.
// Single responsibility: managing attendee lifecycle.
class AttendeeService {
    private val attendees: MutableList<Attendee> = mutableListOf()

    fun addAttendee(attendee: Attendee): Boolean {
        if (attendees.any { it.id == attendee.id || it.email == attendee.email }) {
            return false
        }
        attendees.add(attendee)
        return true
    }


    fun createAttendee(
        name: String,
        email: String,
        phone: String = "",
        organization: String = ""
    ): Attendee? {
        val attendee = Attendee(
            name = name,
            email = email,
            phone = phone,
            organization = organization
        )
        return if (addAttendee(attendee)) attendee else null
    }

    fun removeAttendee(attendeeId: String): Boolean {
        return attendees.removeIf { it.id == attendeeId }
    }

    fun updateAttendee(
        attendeeId: String,
        name: String,
        email: String,
        phone: String,
        organization: String
    ): Boolean {
        val existingWithEmail = getAttendeeByEmail(email)
        if (existingWithEmail != null && existingWithEmail.id != attendeeId) {
            return false
        }

        val attendee = getAttendee(attendeeId) ?: return false
        attendee.name = name
        attendee.email = email
        attendee.phone = phone
        attendee.organization = organization
        return true
    }

    fun getAttendee(attendeeId: String): Attendee? = attendees.find { it.id == attendeeId }

    fun getAttendeeByEmail(email: String): Attendee? {
        return attendees.find { it.email.equals(email, ignoreCase = true) }
    }

    fun getAllAttendees(): List<Attendee> = attendees.toList()

    fun clearAll() {
        attendees.clear()
    }
}