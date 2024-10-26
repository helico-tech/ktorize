package nl.helico.ktorize.assetmapper

import io.ktor.server.application.*

interface AssetTransformer {
    fun accepts(call: PipelineCall, data: Any): Boolean
    suspend fun transform(call: PipelineCall, data: Any): Any
}