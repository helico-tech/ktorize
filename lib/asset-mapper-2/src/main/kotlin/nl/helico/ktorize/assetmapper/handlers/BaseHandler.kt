package nl.helico.ktorize.assetmapper.handlers

import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.Context

abstract class BaseHandler : AssetHandler {
    override fun handle(input: Asset.Input, mapper: AssetMapper, context: Context): Asset.Output {
        return Asset.Output(
            path = mapper.getTransformedPath(input),
            lines = input.lines,
            input = input,
            digest = mapper.digest(input.lines),
            dependencies = emptyList()
        )
    }
}