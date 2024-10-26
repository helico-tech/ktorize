package nl.helico.ktorize.assetmapper

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import nl.helico.ktorize.html.DeferredTagConsumer
import nl.helico.ktorize.html.Hook

class AssetMapperHook(
    val assetMapper: AssetMapper,
    val tagNames: List<String> = listOf("img", "script", "link", "a"),
    val attributeNames: List<String> = listOf("src", "href")
) : Hook.TagStart {
    override fun beforeTagStart(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean {
        if (tagNames.contains(tag.tagName)) {
            attributeNames.forEach { attributeName ->
                val src = tag.attributes[attributeName]

                if (src != null && assetMapper.matches(src)) tag.attributes[attributeName] = assetMapper.map(src)
            }
        }

        return true
    }

    override fun afterTagStart(consumer: DeferredTagConsumer<*>, tag: Tag) {}
}