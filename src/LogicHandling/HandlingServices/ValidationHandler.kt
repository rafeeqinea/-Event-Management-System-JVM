package LogicHandling.HandlingServices

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Centralised validation service for all domain objects
// UI panels should use this service to validate input before calling the facade.
class ValidationHandler {

     // Validation result containing success status and error message
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String = ""
    ) {
        companion object {
            fun success() = ValidationResult(true)
            fun error(message: String) = ValidationResult(false, message)
        }
    }

    // ==================== Event Validation ====================

    // Validate all event fields before creation or update
    fun validateEvent(
        title: String,
        startDate: String,
        startTime: String,
        endDate: String,
        endTime: String,
        estimatedAttendeesStr: String,
        requireFutureStart: Boolean = true
    ): ValidationResult {
        // Validate title
        if (title.isBlank()) {
            return ValidationResult.error("Event title cannot be blank")
        }

        // Validate estimated attendees
        val attendeesResult = validatePositiveInteger(estimatedAttendeesStr, "estimated attendees")
        if (!attendeesResult.isValid) {
            return attendeesResult
        }

        // Validate date/time fields are not empty
        if (startDate.isEmpty() || startTime.isEmpty()) {
            return ValidationResult.error("Start date and time cannot be blank")
        }

        if (endDate.isEmpty() || endTime.isEmpty()) {
            return ValidationResult.error("End date and time cannot be blank")
        }

        // Parse date/time
        val startDateTime = parseDateTime(startDate, startTime)
        val endDateTime = parseDateTime(endDate, endTime)

        if (startDateTime == null || endDateTime == null) {
            return ValidationResult.error("Invalid date/time format. Use dd/MM/yyyy for date and HH:mm for time")
        }

        // Validate start time is in the future (for new events)
        if (requireFutureStart && !startDateTime.isAfter(LocalDateTime.now())) {
            return ValidationResult.error("Start date and time must be in the future")
        }

        // Validate end time is after start time
        if (!endDateTime.isAfter(startDateTime)) {
            return ValidationResult.error("End date and time must be after start date and time")
        }

        return ValidationResult.success()
    }

    // Parse date and time strings into LocalDateTime
    // Returns null if parsing fails
    fun parseDateTime(dateStr: String, timeStr: String): LocalDateTime? {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val date = LocalDate.parse(dateStr.trim(), dateFormatter)
            val timeParts = timeStr.trim().split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            date.atTime(hour, minute)
        } catch (e: Exception) {
            null
        }
    }

    // ==================== Attendee Validation ====================

    // Validate attendee fields before creation or update
    fun validateAttendee(
        name: String,
        email: String,
        phone: String = ""
    ): ValidationResult {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()

        // Validate name
        if (trimmedName.isEmpty()) {
            return ValidationResult.error("Attendee name cannot be blank")
        }

        // Validate email
        if (trimmedEmail.isEmpty()) {
            return ValidationResult.error("Email cannot be blank")
        }

        if (trimmedEmail.contains(" ")) {
            return ValidationResult.error("Email cannot contain spaces")
        }

        if (trimmedEmail.length > 254) {
            return ValidationResult.error("Email is too long")
        }

        if (!isValidEmail(trimmedEmail)) {
            return ValidationResult.error("Invalid email format: $trimmedEmail")
        }

        // Validate phone if provided
        if (trimmedPhone.isNotEmpty() && !isValidPhone(trimmedPhone)) {
            return ValidationResult.error(
                "Invalid phone format: phone must contain 10 to 15 digits and may include +, spaces, (), or -"
            )
        }

        return ValidationResult.success()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun isValidPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[\\s()-]"), "")
        val phoneRegex = Regex("^[+]?[0-9]{10,15}$")
        return phoneRegex.matches(cleanPhone)
    }

    // ==================== Venue Validation ====================

    // Validate venue fields before creation or update
    fun validateVenue(
        name: String,
        address: String,
        city: String,
        capacityStr: String
    ): ValidationResult {
        // Validate name
        if (name.trim().isEmpty()) {
            return ValidationResult.error("Venue name cannot be blank")
        }

        // Validate address
        if (address.trim().isEmpty()) {
            return ValidationResult.error("Venue address cannot be blank")
        }

        // Validate city
        if (city.trim().isEmpty()) {
            return ValidationResult.error("Venue city cannot be blank")
        }

        // Validate capacity
        val capacityResult = validatePositiveInteger(capacityStr, "capacity")
        if (!capacityResult.isValid) {
            return capacityResult
        }

        val capacity = capacityStr.toInt()
        if (capacity <= 0) {
            return ValidationResult.error("Venue capacity must be greater than 0")
        }

        return ValidationResult.success()
    }

    // ==================== Common Validation ====================

    // Validate that a string represents a valid positive integer
    fun validatePositiveInteger(value: String, fieldName: String): ValidationResult {
        if (value.trim().isEmpty()) {
            return ValidationResult.error("Please enter a value for $fieldName")
        }

        return try {
            val number = value.trim().toInt()
            if (number < 0) {
                ValidationResult.error("Please enter a valid positive number for $fieldName")
            } else {
                ValidationResult.success()
            }
        } catch (e: NumberFormatException) {
            ValidationResult.error("Please enter a valid number for $fieldName")
        }
    }

    // Validate that a required string field is not empty
    fun validateRequired(value: String, fieldName: String): ValidationResult {
        return if (value.trim().isEmpty()) {
            ValidationResult.error("$fieldName cannot be blank")
        } else {
            ValidationResult.success()
        }
    }
}