package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import java.net.URL

data class Asset(
    val url: URL,
    val contentType: ContentType
)