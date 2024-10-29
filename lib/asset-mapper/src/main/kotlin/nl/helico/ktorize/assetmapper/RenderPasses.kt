package nl.helico.ktorize.assetmapper

import kotlinx.html.TagConsumer
import nl.helico.ktorize.html.RenderPass
import nl.helico.ktorize.html.TagConsumerAction

class AssetMapperRenderPass(
    val assetMapper: AssetMapper,
    val tagNames: List<String> = listOf("img", "script", "link", "a"),
    val attributeNames: List<String> = listOf("src", "href")
) : RenderPass {

    override fun before(action: TagConsumerAction, consumer: TagConsumer<*>): Boolean {
        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {
        if (action !is TagConsumerAction.TagStart) return

        if (!tagNames.contains(action.tag.tagName)) return

        attributeNames.forEach {attributeName ->
            val src = action.tag.attributes[attributeName]
            if (src != null && assetMapper.matches(src)) action.tag.attributes[attributeName] = assetMapper.map(src)
        }
    }
}
