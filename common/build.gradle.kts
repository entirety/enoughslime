plugins {
    java
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    `maven-publish`
}

// EnoughSlime
val modId: String by extra
val modJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra

// JEI
val jeiVersion: String by extra

val baseArchivesName = "${modId}-${minecraftVersion}-common"
base {
    archivesName.set(baseArchivesName)
}

sourceSets {
    named("main") {
        resources.setSrcDirs(emptyList<String>())
    }

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

minecraft {
    version(minecraftVersion)
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}")
}

publishing {
    publications {
        register<MavenPublication>("commonJar") {
            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))

            artifactId = base.archivesName.get()
        }
    }

    repositories {
        val deployDir = project.findProperty("DEPLOY_DIR")
        if (deployDir != null) {
            maven(deployDir)
        }
    }
}
