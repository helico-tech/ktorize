package nl.helico.ktorize.assetmapper

import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.*
import kotlinx.io.Source
import kotlinx.io.readByteArray
import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.text.toByteArray

interface AssetDigester {
    fun digest(content: ByteArray): String
}

object MD5AssetDigester : AssetDigester {
    override fun digest(content: ByteArray): String {
        val bytes = MessageDigest.getInstance("MD5").digest(content)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

object Base64AssetDigester : AssetDigester {
    @OptIn(ExperimentalEncodingApi::class)
    override fun digest(content: ByteArray): String {
        val bytes = MessageDigest.getInstance("SHA1").digest(content)
        return Base64.UrlSafe.encode(bytes.sliceArray(0 until 4)).trimEnd('=')
    }
}