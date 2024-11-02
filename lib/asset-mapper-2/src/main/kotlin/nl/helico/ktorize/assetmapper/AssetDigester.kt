package nl.helico.ktorize.assetmapper

import java.security.MessageDigest
import kotlin.text.toByteArray

interface AssetDigester {
    fun digest(content: String): String
}

object MD5AssetDigester : AssetDigester {
    override fun digest(content: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(content.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}