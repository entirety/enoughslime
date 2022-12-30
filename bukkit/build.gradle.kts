plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("io.papermc.paperweight.userdev") version "1.4.0"
}

// EnoughSlime
val modId: String by extra
val modJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra

val baseArchivesName = "${modId}-${minecraftVersion}-bukkit"
base {
    archivesName.set(baseArchivesName)
}

sourceSets {
    named("test") {
        allJava.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()

    javaToolchains {
        compilerFor {
            languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
        }
    }
}

dependencies {
    paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")
}
