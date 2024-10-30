package nl.helico.ktorize.html

import kotlinx.html.TagConsumer

interface Layout {
    fun render(): HTMLView

    interface Factory<L : Layout> {
        fun create(): L
    }
}

