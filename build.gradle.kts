plugins {
    id("fabric-loom")
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()
val loaderDep = property("mod.loader_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

repositories {
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    fun fapi(module: String) = fabricApi.module(module, deps["fabric_api"])

    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${deps["yarn_build"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")

    modImplementation(fapi("fabric-lifecycle-events-v1"))
    modImplementation(fapi("fabric-key-binding-api-v1"))
    modRuntimeOnly(fapi("fabric-resource-loader-v0"))
    vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.1")

    modImplementation("com.terraformersmc:modmenu:${deps["modmenu"]}")

    // note: the name for this was changed post-1.20.2; this doesn't matter to us yet though, as the 1.20.2 builds
    // works fine on newer versions (as of the time of writing, at least).
    modImplementation("dev.isxander.yacl:yet-another-config-lib-fabric:${deps["yacl"]}+${mcVersion}") {
        // work around dependency issues with yacl 3.2.1 on 1.20.2 by simply telling gradle
        // to ignore dependencies that cause it to explode during dependency resolution; we don't use
        // any features that these dependencies are used for, so we can safely just blanket exclude these
        // across all versions.
        // see https://github.com/isXander/YetAnotherConfigLib/issues/111
        exclude(group = "org.quiltmc.parsers")
        exclude(group = "com.twelvemonkeys.common")
        exclude(group = "com.twelvemonkeys.imageio")
    }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.compare(mcVersion, "1.20.6") >= 0) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.processResources {
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("mcdep", mcDep)

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "mcdep" to mcDep,
        "loaderdep" to loaderDep
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}
