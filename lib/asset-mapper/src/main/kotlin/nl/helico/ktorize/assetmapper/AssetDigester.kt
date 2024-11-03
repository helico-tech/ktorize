package nl.helico.ktorize.assetmapper

import io.ktor.utils.io.core.*
import kotlinx.io.Source
import kotlinx.io.readByteArray
import java.security.MessageDigest
import kotlin.text.toByteArray

interface AssetDigester {
    fun digest(content: Source): String
}

object MD5AssetDigester : AssetDigester {
    override fun digest(content: Source): String {
        val bytes = MessageDigest.getInstance("MD5").digest(content.copy().readByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}