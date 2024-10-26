package nl.helico.ktorize.hotwire.stimulus

import io.ktor.util.*

class ControllerRegistry {

    companion object {
        val Key = AttributeKey<ControllerRegistry>("ControllerRegistry")
    }

    private val identifiers = mutableSetOf<String>()

    fun registerIdentifier(name: String) {
        identifiers.add(name)
    }

    fun identifiers(): Set<String> = identifiers

}