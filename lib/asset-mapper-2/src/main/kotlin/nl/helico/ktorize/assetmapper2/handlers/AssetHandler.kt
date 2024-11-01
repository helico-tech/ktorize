package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetDigester
import nl.helico.ktorize.assetmapper2.AssetPathTransformer
import nl.helico.ktorize.assetmapper2.readers.AssetReader

interface AssetHandler {
    data class Context(
        val reader: AssetReader,
        val digester: AssetDigester,
        val pathTransformer: AssetPathTransformer
    )

    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input, context: Context): Asset.Output
}

