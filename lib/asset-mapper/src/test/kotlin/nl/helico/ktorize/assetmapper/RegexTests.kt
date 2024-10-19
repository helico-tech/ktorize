package nl.helico.ktorize.assetmapper

import kotlin.test.Test

class RegexTests {

    @Test
    fun basicTest() {
        val regex = createMappedAssetRegex("/baseurl")

        val match = regex.matchEntire("/baseurl/file1-2f03b03637bf162937793f756f0f1583.txt")

        assert(match != null)

        val groups = match!!.groups

        assert(groups["directoryName"]!!.value == "")
        assert(groups["baseName"]!!.value == "file1")
        assert(groups["digest"]!!.value == "2f03b03637bf162937793f756f0f1583")
        assert(groups["extension"]!!.value == "txt")
    }

    @Test
    fun deeperDirectoryTes() {
        val regex = createMappedAssetRegex("/baseurl")

        val match = regex.matchEntire("/baseurl/deeper/file1-2f03b03637bf162937793f756f0f1583.txt")

        assert(match != null)

        val groups = match!!.groups

        assert(groups["directoryName"]!!.value == "/deeper")
        assert(groups["baseName"]!!.value == "file1")
        assert(groups["digest"]!!.value == "2f03b03637bf162937793f756f0f1583")
        assert(groups["extension"]!!.value == "txt")
    }
}