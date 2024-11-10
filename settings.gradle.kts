enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ktorize"

includeBuild("plugins/asset-mapper")

include(":lib:di")
include(":lib:forms")
include(":lib:hotwire-turbo")
include(":lib:hotwire-stimulus")
include(":lib:html")
include(":lib:importmap")

//include(":examples:simple-server")

include(":websites:bumastemra")
