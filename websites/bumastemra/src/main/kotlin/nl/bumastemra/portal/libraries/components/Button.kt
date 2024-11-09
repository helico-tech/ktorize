package nl.bumastemra.portal.libraries.components

import kotlinx.html.FlowContent
import kotlinx.html.button

fun FlowContent.Button(
    text: String,
) {
    button(classes = "kt-button") {
        +text
    }
}