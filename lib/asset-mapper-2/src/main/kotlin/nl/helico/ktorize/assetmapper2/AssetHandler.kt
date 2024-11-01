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
        val digest = digester.digest(input.data)
        return Asset.Output(pathTransformer.transform(input.path, digest), input.data, emptyList())
    }
}