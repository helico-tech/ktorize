package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.io.Buffer
import kotlinx.io.readLine
import kotlin.io.path.Path

class CSSAssetTransformer(
    private val assetMapper: AssetMapper
) : AssetTransformer {

    val cssImportRegex = Regex("""@import\s+(url\((['"]?)(.*?)\2\)|(['"])(.*?)\4)""")

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

            val matches = cssImportRegex.findAll(line)

            for (match in matches) {
                val url = match.groupValues[3].ifEmpty { match.groupValues[5] }
                val relativeUrl = currentUrl.parent.resolve(url).toString()
                val mappedUrl = assetMapper.map(relativeUrl)

                if (match.groupValues[1].startsWith("url")) {
                    line = line.replace(url, mappedUrl)
                } else {
                    line = line.replace("\"$url\"", "url(\"$mappedUrl\")")
                }
            }

            output.appendLine(line)
        }

        call.response.header(HttpHeaders.ContentType, ContentType.Text.CSS.toString())

        return output.toString()
    }
}