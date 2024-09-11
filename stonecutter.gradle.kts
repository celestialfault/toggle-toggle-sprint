plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.6-SNAPSHOT" apply false
}
stonecutter active "1.20.2" /* [SC] DO NOT EDIT */

// Builds every version into `build/libs/{mod.version}/`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter configureEach {
    /*
    See src/main/java/com/example/TemplateMod.java
    and https://stonecutter.kikugie.dev/
    */
    // Swaps replace the scope with a predefined value
    swap("mod_version", "\"${property("mod.version")}\";")
    // Dependencies add targets to check versions against
    // Using `project.property()` in this block gets the versioned property
    dependency("fapi", project.property("deps.fabric_api").toString())
    dependency("yacl", project.property("deps.yacl").toString())
}
