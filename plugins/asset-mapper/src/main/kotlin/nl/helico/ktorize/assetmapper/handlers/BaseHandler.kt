package nl.helico.ktorize.assetmapper.handlers

import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.Context

abstract class BaseHandler : AssetHandler {
    override fun handle(input: Asset.Input, mapper: AssetMapper, context: Context): Asset.Output {
        return Asset.Output(
            source = input.source,
            input = input,
            digest = input.digest,
            dependencies = emptyList()
        )
    }
}