package nl.helico.ktorize.assetmapper2

import java.security.MessageDigest
import kotlin.text.toByteArray

interface AssetDigester {
    fun digest(contents: List<String>): String
}

object MD5AssetDigester : AssetDigester {
    override fun digest(contents: List<String>): String {
        val bytes = MessageDigest.getInstance("MD5").digest(contents.joinToString("\n").toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}