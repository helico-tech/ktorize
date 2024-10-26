package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.utils.io.*
import kotlinx.io.Buffer
import kotlinx.io.readLine
import kotlin.io.path.Path

class CSSAssetTransformer(
    private val assetMapper: AssetMapper
) : AssetTransformer {

    val cssImportUrlRegex = Regex("""@import\s+url\((['"]?)(.*?)\1\)""")

    override fun accepts(call: PipelineCall, data: Any): Boolean {
        return data is OutgoingContent.ReadChannelContent && data.contentType?.match(ContentType.Text.CSS) ?: false && assetMapper.matches(call.request.uri)
    }

    override suspend fun transform(call: PipelineCall, data: Any): Any {
        val content = data as OutgoingContent.ReadChannelContent
        val currentUrl = Path(call.request.uri)
        val output = StringBuilder()
        val channel = content.readFrom()

        while (!channel.isClosedForRead) {
            var line = channel.readUTF8Line() ?: break

            val matches = cssImportUrlRegex.findAll(line)

            for (match in matches) {
                val url = match.groupValues[2]
                val relativeUrl = currentUrl.parent.resolve(url).toString()
                val mappedUrl = assetMapper.map(relativeUrl)

                line = line.replace(url, mappedUrl)
            }

            output.appendLine(line)
        }

        return output.toString()
    }
}