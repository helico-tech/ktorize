package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetDigester
import nl.helico.ktorize.assetmapper2.AssetPathTransformer

interface AssetHandler {
    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input, digester: AssetDigester, pathTransformer: AssetPathTransformer): Asset.Output
}

