package nl.helico.ktorize.html

import kotlinx.html.TagConsumer

interface Layout {
    fun render(): HTMLView

    fun interface Factory<L : Layout> {
        fun create(): L
    }
}

