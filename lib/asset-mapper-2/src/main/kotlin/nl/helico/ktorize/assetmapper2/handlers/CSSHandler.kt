package nl.helico.ktorize.assetmapper2.handlers

import io.ktor.http.*
import nl.helico.ktorize.assetmapper2.Asset

class CSSHandler : BaseHandler() {
    override fun accepts(input: Asset.Input): Boolean {
        return input.contentType.match(ContentType.Text.CSS)
    }
}