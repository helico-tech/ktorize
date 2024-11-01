package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper

interface AssetHandler {
    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input, mapper: AssetMapper): Asset.Output
}

