package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset

abstract class BaseHandler : AssetHandler {
    override fun handle(input: Asset.Input, context: AssetHandler.Context): Asset.Output {
        val digest = context.digester.digest(input.data)
        return Asset.Output(context.pathTransformer.transform(input.path, digest), input.data, emptyList())
    }
}