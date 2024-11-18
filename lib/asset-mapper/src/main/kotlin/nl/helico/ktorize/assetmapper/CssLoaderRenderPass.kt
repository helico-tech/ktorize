package nl.helico.ktorize.assetmapper

import kotlinx.html.HEAD
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.link
import nl.helico.ktorize.html.DeferredTagConsumer
import nl.helico.ktorize.html.RenderPass
import nl.helico.ktorize.html.TagConsumerAction
import java.nio.file.Path
import kotlin.io.path.Path

class CssLoaderRenderPass(
    private val prefix: String = "kt-",
    private val cssPath: Path = Path("css"),
    private val mappedAssetsConfiguration: MappedAssetsConfiguration
) : RenderPass {

    private val cssClasses = mutableSetOf<String>()

    override fun before(
        action: TagConsumerAction,
        consumer: DeferredTagConsumer<*>
    ): Boolean {
        when (action) {
            is TagConsumerAction.TagEnd -> {
                registerClasses(action.tag)

                if (action.tag is HEAD) {
                    consumer.onDeferred {
                        val assets = resolveCssClasses()
                        assets.forEach {
                            link(rel = "stylesheet", href = it.path)
                        }
                    }
                }

                return true
            }
            else -> return true
        }
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {
    }

    private fun registerClasses(tag: Tag) {
        val classes = tag.attributes["class"]?.split(" ") ?: return
        val cssClasses = classes.filter { it.startsWith(prefix) }
        this.cssClasses.addAll(cssClasses)
    }

    private fun resolveCssClasses(): List<AssetTree.File> {
        val base = Path(mappedAssetsConfiguration.root.path).resolve(cssPath).normalize()
        val classNames = cssClasses.map { it.removePrefix(prefix)  }
        val assets = classNames.mapNotNull {
            val replaced = it.replace("--", "/")
            val path = base.resolve("$replaced.css").toString()
            mappedAssetsConfiguration.resolve(path)
        }
        return assets
    }
}