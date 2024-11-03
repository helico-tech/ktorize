package nl.helico.ktorize.assetmapper.handlers

import io.ktor.util.*
import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.Context

interface AssetHandler {
    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input, mapper: AssetMapper, context: Context = Attributes()): Asset.Output
}

