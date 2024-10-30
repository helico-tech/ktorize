package nl.bumastemra.portal.features.shared.components

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.classes
import kotlinx.html.div

fun FlowContent.Panel(block: DIV.() -> Unit) = div {
    classes = setOf("panel")
    block()
}