package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset

class DefaultHandler : BaseHandler() {
    override fun accepts(input: Asset.Input): Boolean {
        return true
    }
}