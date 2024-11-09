package nl.helico.ktorize.assetmapper.handlers

object Fixtures {
    object CSS {
        val `no-imports` = """
            body {
                background-color: red;
            }
        """.trimIndent()

        val `external-import` = """
            @import url("https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap");
            body {
                font-family: 'Roboto', sans-serif;
            }
        """.trimIndent()

        val `simple-direct-import` = """
            @import "single-dependency.css";
            body {
                background-color: red;
            }
        """.trimIndent()

        val `single-dependency` = """
            :root {
                --primary-color: red;
            }
        """.trimIndent()

        val `simple-circular-1` = """
            @import "simple-circular-2.css";
        """.trimIndent()

        val `simple-circular-2` = """
            @import "simple-circular-1.css";
        """.trimIndent()

        val `multi-level-circular-1` = """
            @import "multi-level-circular-2.css";
        """.trimIndent()

        val `multi-level-circular-2` = """
            @import "multi-level-circular-3.css";
        """.trimIndent()

        val `multi-level-circular-3` = """
            @import "multi-level-circular-1.css";
        """.trimIndent()

        val `multi-level-dependency-1` = """
            @import "multi-level-dependency-2.css";
            @import "multi-level-dependency-4.css";
        """.trimIndent()

        val `multi-level-dependency-2` = """
            @import "multi-level-dependency-3.css";
            @import "multi-level-dependency-4.css";
        """.trimIndent()

        val `multi-level-dependency-3` = """
            @import "multi-level-dependency-4.css";
        """.trimIndent()

        val `multi-level-dependency-4` = """
        """.trimIndent()

        val `other-urls` = """
           body {
                background-image: url("https://example.com/banner.png");
            }
            
            .container {
                background-image: url("container.png");
            }
        """.trimIndent()

        val `container-png` = """PNGDATA"""

        val `missing-url` = """
            body {
                background-image: url("missing.png");
            }
        """.trimIndent()

        val `dependency-other-dir1` = """
            @import "../dependency.css";
        """.trimIndent()

        val `dependency-other-dir2` = """
        """.trimIndent()
    }
}