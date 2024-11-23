package nl.helico.ktorize.html

import kotlinx.html.TagConsumer

interface DeferredTagConsumer<out R> : TagConsumer<R> {
    fun onDeferred(block: TagConsumer<*>.() -> Unit)
}