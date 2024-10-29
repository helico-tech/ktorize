package nl.helico.ktorize.html

import io.ktor.util.*

interface RenderContext {
    val renderPasses: List<RenderPass>

    fun addRenderPass(renderPass: RenderPass)

    companion object {
        val Key = AttributeKey<RenderContext>("RenderContext")
    }
}

class RenderContextImpl : RenderContext {
    override val renderPasses = mutableListOf<RenderPass>()

    override fun addRenderPass(renderPass: RenderPass) {
        renderPasses.add(renderPass)
    }
}

fun RenderContext() = RenderContextImpl()