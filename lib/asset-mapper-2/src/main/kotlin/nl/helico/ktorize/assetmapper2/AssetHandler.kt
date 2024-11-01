package nl.helico.ktorize.assetmapper2

interface AssetHandler {
    fun accepts(input: Asset.Input): Boolean
    fun handle(input: Asset.Input, digester: AssetDigester, pathTransformer: AssetPathTransformer): Asset.Output
}

class DefaultHandler : AssetHandler {
    override fun accepts(input: Asset.Input): Boolean {
        return true
    }

    override fun handle(input: Asset.Input, digester: AssetDigester, pathTransformer: AssetPathTransformer): Asset.Output {
        return Asset.Output(input.path, input.data, emptyList())
    }
}