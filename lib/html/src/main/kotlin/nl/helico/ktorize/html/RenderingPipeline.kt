package nl.helico.ktorize.html

import io.ktor.server.application.*
import io.ktor.util.*
import io.ktor.util.logging.*

interface RenderingPipeline {
    companion object {
        val Key = AttributeKey<RenderingPipeline>("RenderingPipeline")
    }

    val hooks: List<Hook>

    fun addHook(hook: Hook)
}

fun RenderingPipeline(): RenderingPipeline = RenderingPipelineImpl()

class RenderingPipelineImpl : RenderingPipeline {

    private val LOGGER = KtorSimpleLogger("RenderingPipelineImpl")

    init {
        LOGGER.info("RenderingPipelineImpl created")
    }

    override val hooks = mutableListOf<Hook>()

    override fun addHook(hook: Hook) {
        LOGGER.debug("Adding hook {}", hook)
        hooks.add(hook)
    }
}

val ApplicationCall.renderingPipeline get(): RenderingPipeline = attributes.computeIfAbsent(RenderingPipeline.Key) { RenderingPipeline() }