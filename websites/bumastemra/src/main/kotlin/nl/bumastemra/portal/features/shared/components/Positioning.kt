package nl.bumastemra.portal.features.shared.components

import kotlinx.html.*

fun FlowContent.FullPageContainer(block: DIV.() -> Unit) = div {
    classes = setOf("full-page-container")
    block()
}

fun FlowContent.CenterContainer(block: DIV.() -> Unit) = div {
    classes = setOf("center-container")
    block()
}