package nl.helico.ktorize.assetmapper2.handlers

import io.ktor.util.*
import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import nl.helico.ktorize.assetmapper2.Context

interface AssetHandler {
    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input, mapper: AssetMapper, context: Context = Attributes()): Asset.Output
}

