package nl.helico.ktorize.assetmapper

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import nl.helico.ktorize.html.RenderPass
import nl.helico.ktorize.html.TagConsumerAction
import kotlin.collections.contains

class AssetMapperRenderPass(
    private val configuration: MappedAssetsConfiguration,
    private val attributes: Set<String> = setOf("src", "href")
) : RenderPass {

    override fun before(
        action: TagConsumerAction,
        consumer: TagConsumer<*>
    ): Boolean {
        return true
    }

    override fun after(
        action: TagConsumerAction,
        consumer: TagConsumer<*>
    ) {
        when (action) {
            is TagConsumerAction.TagStart -> {
                resolveAttributes(action.tag)
            }

            is TagConsumerAction.TagAttributeChange -> {
                resolveAttributes(action.tag)
            }

            else -> {}
        }
    }

    private fun resolveAttributes(tag: Tag) {
        attributes.forEach { attr  ->
            if (tag.attributes.contains(attr)) {
                val assetPath = tag.attributes[attr]!!
                val resolved = configuration.resolve(assetPath)
                if (resolved != null) {
                    tag.attributes[attr] = resolved.path
                }
            }
        }
    }
}