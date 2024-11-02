package nl.helico.ktorize.assetmapper

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

class CachedAssetMapper(
    private val downstream: AssetMapper,
    private val cache: Cache = Cache(concurrent = true)
) : AssetMapper by downstream {

    interface Cache {
        fun get(path: Path): AssetMapper.MapResult?
        fun put(path: Path, result: AssetMapper.MapResult)

        companion object {
            operator fun invoke(concurrent: Boolean): Cache = if (concurrent) ConcurrentHashMap else HashMap

            val HashMap = object : Cache {
                private val cache = HashMap<Path, AssetMapper.MapResult>()

                override fun get(path: Path): AssetMapper.MapResult? {
                    return cache[path]
                }

                override fun put(path: Path, result: AssetMapper.MapResult) {
                    cache[path] = result
                }
            }

            val ConcurrentHashMap = object : Cache {
                private val cache = ConcurrentHashMap<Path, AssetMapper.MapResult>()

                override fun get(path: Path): AssetMapper.MapResult? {
                    return cache[path]
                }

                override fun put(path: Path, result: AssetMapper.MapResult) {
                    cache[path] = result
                }
            }
        }
    }

    override fun map(path: Path, context: Context): AssetMapper.MapResult {
        return cache.get(path) ?: run {
            val result = downstream.map(path, context)
            cache.put(path, result)
            result
        }
    }
}

fun AssetMapper.cached(concurrent: Boolean = true): AssetMapper = when (this) {
    is CachedAssetMapper -> this
    else -> CachedAssetMapper(this, CachedAssetMapper.Cache(concurrent))
}