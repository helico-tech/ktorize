package nl.helico.ktorize.hotwire.stimulus

import kotlinx.html.Tag

fun Tag.stimulusController(controller: String) {
    attributes["data-controller"] = controller
}