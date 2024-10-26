package nl.helico.ktorize.hotwire.stimulus

import io.ktor.util.*
import io.ktor.util.logging.*

class ControllerRegistry {

    companion object {
        val Key = AttributeKey<ControllerRegistry>("ControllerRegistry")
    }

    private val identifiers = mutableSetOf<String>()
    private val LOGGER = KtorSimpleLogger("ControllerRegistry")


    fun registerIdentifier(name: String) {
        LOGGER.debug("Registering controller identifier: $name")
        identifiers.add(name)
    }

    fun identifiers(): Set<String> = identifiers

}