package nl.helico.ktorize.assetmapper2

import java.nio.file.Path

sealed interface Asset {
    val path: Path
    val data: List<String>

    data class Input(
        override val path: Path,
        override val data: List<String>
    ) : Asset

    data class Output(
        override val path: Path,
        override val data: List<String>
    ) : Asset
}