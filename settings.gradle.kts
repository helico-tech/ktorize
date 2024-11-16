enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ktorize"

includeBuild("lib/asset-mapper-core")
includeBuild("plugins/asset-mapper")

include(":lib:asset-mapper")
include(":lib:di")
include(":lib:forms")
include(":lib:hotwire-turbo")
include(":lib:hotwire-stimulus")
include(":lib:html")
include(":lib:importmap")

include(":websites:bumastemra")
