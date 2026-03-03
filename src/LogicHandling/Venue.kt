package LogicHandling;

data class Venue(
    val id: Int,
    var name: String,
    var capacity: Int,
    var city: String,
    var address: String,
    var facilities: List<String> = emptyList()
    ) {
    init {
        // Validation checks on input data
        require(name.isNotBlank()) { "Venue name cannot be empty" }
        require(city.isNotBlank()) { "City/location must be provided" }
        require(address.isNotBlank()) { "Address cannot be empty" }
        require(capacity > 0) { "Venue capacity must be a positive integer" }
    }
    
    override fun toString(): String {
        return "$name ($city) - Capacity: $capacity"
    }
    
    
    fun getDetailedInfo(): String {
        return buildString {
            appendLine("Venue: $name")
            appendLine("Location: $address, $city")
            appendLine("Capacity: $capacity")
            if (facilities.isNotEmpty()) {
                appendLine("Facilities: ${facilities.joinToString(", ")}")
            }
        }
    }
}
