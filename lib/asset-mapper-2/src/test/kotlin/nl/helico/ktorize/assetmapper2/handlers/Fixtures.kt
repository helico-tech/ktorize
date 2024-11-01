package nl.helico.ktorize.assetmapper2.handlers

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
    }
}