plugins {
	id("com.diffplug.spotless") version("6.12.0")
}

apply {
    from("buildtools/ColoredOutput.gradle")
}

// EnoughSlime
val modId: String by extra
val modName: String by extra
val modGroup: String by extra
val modAuthor: String by extra
val githubUrl: String by extra
val modDescription: String by extra
val modJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra
val minecraftApiVersion: String by extra
val minecraftVersionRange: String by extra

// Forge
val forgeVersion: String by extra
val forgeVersionRange: String by extra
val loaderVersionRange: String by extra

// Fabric
val fabricApiVersion: String by extra
val fabricLoaderVersion: String by extra

// Mappings
val parchmentVersionFabric: String by extra
val parchmentMinecraftVersion: String by extra

// Version
val specificationVersion: String by extra

repositories {
    mavenCentral()
}

spotless {
    java {
        target("*/src/*/java/dev/entirety/enoughslime/**/*.java")

        endWithNewline()
        removeUnusedImports()
        trimTrailingWhitespace()
    }
}

subprojects {
    var buildNumber = project.findProperty("BUILD_NUMBER")
    if (buildNumber === null) {
        buildNumber = "9999"
    }

    group = modGroup
    version = "${specificationVersion}.${buildNumber}"

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(JavaLanguageVersion.of(modJavaVersion).asInt())
    }

    tasks.withType<Jar> {
        val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(java.util.Date())
        manifest {
            attributes(mapOf(
                "Specification-Title" to modName,
                "Specification-Vendor" to modAuthor,
                "Specification-Version" to specificationVersion,
                "Implementation-Title" to name,
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to modAuthor,
                "Implementation-Timestamp" to now,
            ))
        }
    }

    tasks.withType<ProcessResources> {
        inputs.property("version", version)

        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta", "fabric.mod.json", "plugin.yml")) {
            expand(mapOf(
                "modId" to modId,
                "modName" to modName,
                "version" to version,
                "githubUrl" to githubUrl,
                "modAuthor" to modAuthor,
                "modDescription" to modDescription,
                "modJavaVersion" to modJavaVersion,

                "minecraftVersion" to minecraftVersion,
                "minecraftApiVersion" to minecraftApiVersion,
                "minecraftVersionRange" to minecraftVersionRange,

                "forgeVersion" to forgeVersion,
                "forgeVersionRange" to forgeVersionRange,
                "loaderVersionRange" to loaderVersionRange,

                "fabricApiVersion" to fabricApiVersion,
                "fabricLoaderVersion" to fabricLoaderVersion
            ))
        }
    }
}
