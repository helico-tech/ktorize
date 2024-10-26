package nl.helico.ktorize.html

import io.ktor.server.application.*
import io.ktor.util.*

interface RenderingPipeline {
    companion object {
        val Key = AttributeKey<RenderingPipeline>("RenderingPipeline")
    }

    val hooks: List<Hook>

    fun addHook(hook: Hook)
}

fun RenderingPipeline(): RenderingPipeline = RenderingPipelineImpl()

class RenderingPipelineImpl : RenderingPipeline {
    override val hooks = mutableListOf<Hook>()

    override fun addHook(hook: Hook) {
        hooks.add(hook)
    }
}

val ApplicationCall.renderingPipeline get(): RenderingPipeline = attributes.computeIfAbsent(RenderingPipeline.Key) { RenderingPipeline() }