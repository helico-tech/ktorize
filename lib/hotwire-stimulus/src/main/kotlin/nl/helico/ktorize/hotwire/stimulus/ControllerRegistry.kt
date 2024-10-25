package nl.helico.ktorize.hotwire.stimulus

import io.ktor.util.*

class ControllerRegistry {

    companion object {
        val Key = AttributeKey<ControllerRegistry>("ControllerRegistry")
    }

    private val controllers = mutableSetOf<String>()

    fun registerName(name: String) {
        controllers.add(name)
    }

    fun getNames(): Set<String> = controllers

}