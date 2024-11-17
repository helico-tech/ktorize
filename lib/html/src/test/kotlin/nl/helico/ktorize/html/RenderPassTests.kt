package nl.helico.ktorize.html

import kotlinx.html.TagConsumer
import kotlinx.html.*
import kotlin.test.Test

class RenderPassTests {

    fun MultiPassConsumer.actions(): List<TagConsumerAction>  {

        val actions = mutableListOf<TagConsumerAction>()

        val pass = object : RenderPass {
            override fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean {
                actions.add(action)
                return true
            }

            override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {}
        }

        this.applyRenderPass(pass)

        return actions
    }

    @Test
    fun injectScriptTag() {

        val pass = object : RenderPass {
            override fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean {
                if (action is TagConsumerAction.TagEnd && action.tag.tagName == "head") {
                    consumer.script(src = "https://example.com/script.js") {}
                }
                return true
            }

            override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {}
        }

        val consumer = MultiPassConsumer()

        consumer.html {
            head { }
            body {
                +"Hello, world!"
            }
        }

        assert(consumer.actions().count { it is TagConsumerAction.TagStart && it.tag.tagName == "script" } == 0)

        consumer.applyRenderPass(pass)

        val actions = consumer.actions()

        assert(actions.count { it is TagConsumerAction.TagStart && it.tag.tagName == "script" } == 1)
    }
}