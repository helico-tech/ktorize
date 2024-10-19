package nl.helico.ktorize.assetmapper

fun createMappedAssetRegex(
    baseUrl: String
) = Regex("""$baseUrl(?<directoryName>.*?)/(?<baseName>[^/]+)-(?<digest>[0-9a-f]{32})\.(?<extension>[^/]+)""")