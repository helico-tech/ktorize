package nl.helico.ktorize.hotwire.stimulus

import kotlinx.io.files.Path
import java.net.URL

class ControllerResolver(
    private val basePackage: String,
    private val remotePath: String,
    val classLoader: ClassLoader
) {
    fun resolveController(identifier: String): String {
        val path = identifierToPath(identifier)

        classLoader.getResource("$basePackage/$path") ?: throw ControllerNotFoundException(identifier)

        return "${remotePath}/${path}"
    }

    private fun identifierToPath(identifier: String): String {
        return identifier.replace("--", "/") + ".controller.js"
    }
}

class ControllerNotFoundException(identifier: String) : Exception("Controller not found: $identifier")