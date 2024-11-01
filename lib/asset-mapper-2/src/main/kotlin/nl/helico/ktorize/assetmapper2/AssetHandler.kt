package nl.helico.ktorize.assetmapper2

import java.nio.file.Path

interface AssetHandler {
    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input): Asset.Output
}

class DefaultHandler : AssetHandler {
    override fun accepts(input: Asset.Input): Boolean {
        return true
    }

    override fun handle(input: Asset.Input): Asset.Output {
        return Asset.Output(input.path, input.data)
    }
}