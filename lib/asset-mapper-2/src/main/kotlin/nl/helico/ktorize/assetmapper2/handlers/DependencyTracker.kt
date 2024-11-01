package nl.helico.ktorize.assetmapper2.handlers

import io.ktor.util.*
import java.nio.file.Path

class DependencyTracker {
    companion object {
        val Key = AttributeKey<DependencyTracker>("DependencyTracker")
    }

    private val parents = mutableMapOf<Path, Set<Path>>()

    fun addDependency(from: Path, to: Path): Boolean {
        return addNormalizedDependency(from.normalize(), to.normalize())
    }

    private fun addNormalizedDependency(from: Path, to: Path): Boolean {
        if (hasCircularDependency(from, to)) return false

        parents.compute(to) { _, v ->
            (v ?: emptySet()).plus(from)
        }

        return true
    }

    private fun hasCircularDependency(from: Path, to: Path): Boolean {
        if (from == to) return true

        val allParents = mutableSetOf(from)
        val remaining = ArrayDeque(allParents)

        while (remaining.isNotEmpty()) {
            val current = remaining.removeFirst()
            val currentParents = parents[current] ?: emptySet()
            allParents.addAll(currentParents)

            if (allParents.contains(to)) return true

            remaining.addAll(currentParents)
        }
        return false
    }
}