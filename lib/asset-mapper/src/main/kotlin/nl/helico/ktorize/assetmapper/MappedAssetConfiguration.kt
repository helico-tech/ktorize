package nl.helico.ktorize.assetmapper

fun MappedAssetsConfiguration.resolve(path: String): AssetTree.File? {
    return root.resolve(path)
}

fun AssetTree.resolve(path: String): AssetTree.File? = when (this) {
    is AssetTree.File -> if (this.logicalPath == path) this else null
    is AssetTree.Folder -> items.firstNotNullOfOrNull { it.resolve(path) }
}