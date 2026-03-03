package LogicHandling

import java.util.UUID

// Represents someone attending an event
data class Attendee(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var email: String,
    var phone: String = "",
    var organization: String = ""
) {
    // Validate attendee details when creating
    init {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()

        require(trimmedName.isNotEmpty()) { "Attendee name cannot be blank" }
        require(trimmedEmail.isNotEmpty()) { "Email cannot be blank" }
        require(!trimmedEmail.contains(" ")) { "Email cannot contain spaces" }
        require(trimmedEmail.length <= 254) { "Email is too long" }
        require(isValidEmail(trimmedEmail)) { "Invalid email format: $trimmedEmail" }

        if (trimmedPhone.isNotEmpty()) {
            require(isValidPhone(trimmedPhone)) {
                "Invalid phone format: phone must contain 10 to 15 digits and may include +, spaces, (), or -"
            }
        }
    }

    // Check if email format is valid using regex
    private fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    // Validate phone number (supports international format)
    private fun isValidPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(PHONE_CLEANUP_REGEX, "")
        return PHONE_DIGITS_REGEX.matches(cleanPhone)
    }


    override fun toString(): String {
        return "$name ($email)"
    }

    companion object {
        private val EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        private val PHONE_CLEANUP_REGEX = Regex("[\\s()-]")
        private val PHONE_DIGITS_REGEX = Regex("^[+]?[0-9]{10,15}$")
    }
}