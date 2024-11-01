package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import nl.helico.ktorize.assetmapper2.Context

abstract class BaseHandler : AssetHandler {
    override fun handle(input: Asset.Input, mapper: AssetMapper, context: Context): Asset.Output {
        val digest = mapper.digester.digest(input.lines)
        return Asset.Output(mapper.pathTransformer.transform(input.path, digest), input.lines, emptyList())
    }
}