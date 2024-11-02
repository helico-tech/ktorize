package nl.helico.ktorize.assetmapper.handlers

import nl.helico.ktorize.assetmapper.Asset

class DefaultHandler : BaseHandler() {
    override fun accepts(input: Asset.Input): Boolean {
        return true
    }
}