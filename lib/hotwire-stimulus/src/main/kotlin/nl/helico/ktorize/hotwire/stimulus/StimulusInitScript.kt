package nl.helico.ktorize.hotwire.stimulus

class StimulusInitScript(
    val basePath: String,
    val controllerPrefix: String,
    val controllerRegistry: ControllerRegistry
) {
    fun script(): String {
        return """
            import { Application } from "@hotwired/stimulus";
            
            ${controllerRegistry.identifiers().joinToString("\n") { "import ${it}Controller from \"${basePath}${controllerPrefix}/${it}\";" }}
            
            window.Stimulus = Application.start();
            
            ${controllerRegistry.identifiers().joinToString("\n") { "Stimulus.register(\"${it}\", ${it}Controller);" }}
        """.trimIndent()
    }
}