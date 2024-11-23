package layouts

import kotlinx.html.*
import nl.helico.ktorize.html.HTMLView
import nl.helico.ktorize.html.Layout

class BaseLayout : Layout {

    var title = "Demo"

    override fun render(): HTMLView = {
        head {
            title(content = this@BaseLayout.title)
            link(rel = "stylesheet", href = "/assets/css/styles.css")
        }

        body("d-flex text-bg-dark") {
            aside {
                div("d-flex flex-column p-3") {
                    +"Sidebar"
                }
            }

            main {
                div("container p-3") {
                    +"Main"
                }
            }
        }
    }

    companion object : Layout.Factory<BaseLayout> {
        override fun create(): BaseLayout = BaseLayout()

        //operator fun invoke(block: BaseLayout.() -> Unit) = BaseLayout().apply(block).render()
    }
}