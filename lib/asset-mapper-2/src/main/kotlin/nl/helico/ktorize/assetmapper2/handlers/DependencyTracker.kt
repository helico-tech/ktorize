package nl.helico.ktorize.assetmapper2.handlers

import io.ktor.util.*
import java.nio.file.Path

class DependencyTracker {
    companion object {
        val Key = AttributeKey<DependencyTracker>("DependencyTracker")
    }

    private val dependencies = mutableMapOf<Path, Set<Path>>()

    fun addDependency(from: Path, to: Path): Boolean {
        val (normalizedTo, normalizedFrom) = to.normalize() to from.normalize()
        if (dependencies[normalizedTo]?.contains(normalizedFrom) == true) {
            return false
        }

        dependencies.compute(normalizedFrom) { _, v ->
            (v ?: emptySet()).plus(normalizedTo)
        }
        return true
    }
}