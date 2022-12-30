plugins {
    java
    idea
    `maven-publish`
    id("fabric-loom") version "1.0.16"
}

repositories {
    maven("https://maven.parchmentmc.org")
}

// EnoughSlime
val modId: String by extra
val modJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra

// Fabric
val fabricApiVersion: String by extra
val fabricLoaderVersion: String by extra

// Mappings
val parchmentVersionFabric: String by extra
val parchmentMinecraftVersion: String by extra

val baseArchivesName = "${modId}-${minecraftVersion}-fabric"
base {
    archivesName.set(baseArchivesName)
}

val dependencyProjects: List<ProjectDependency> = listOf(
    project.dependencies.project(":common"),
    project.dependencies.project(":common-api")
)

dependencyProjects.forEach {
    project.evaluationDependsOn(it.dependencyProject.path)
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
    minecraft("com.mojang:minecraft:${minecraftVersion}")

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()

        parchment("org.parchmentmc.data:parchment-${parchmentMinecraftVersion}:${parchmentVersionFabric}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")

    dependencyProjects.forEach {
        implementation(it)
    }
}

loom {
    runs {
        val dependencyJarPaths = dependencyProjects.map {
            it.dependencyProject.tasks.jar.get().archiveFile.get().asFile
        }
        val classPaths = sourceSets.main.get().output.classesDirs
        val resourcesPaths = listOf(sourceSets.main.get().output.resourcesDir)
        val classPathGroups = listOf(dependencyJarPaths, classPaths, resourcesPaths).flatten().filterNotNull()
        val classPathGroupsString = classPathGroups.joinToString(separator = File.pathSeparator) {
            it.absolutePath.toString()
        }

        val loomRunDir = project.projectDir.relativeTo(project.rootDir).resolve("run")

        named("client") {
            client()
            runDir(loomRunDir.resolve("client").toString())
            vmArgs("-Dfabric.classPathGroups=${classPathGroupsString}")
            ideConfigGenerated(true)

            configName = "Fabric Client"
        }
    }
}

tasks.jar {
    from(sourceSets.main.get().output)

    for (p in dependencyProjects) {
        from(p.dependencyProject.sourceSets.main.get().output)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")

    for (p in dependencyProjects) {
        from(p.dependencyProject.sourceSets.main.get().allJava)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

artifacts {
    archives(tasks.remapJar)
    archives(tasks.remapSourcesJar)
}

publishing {
    publications {
        register<MavenPublication>("fabricJar") {
            @Suppress("UnstableApiUsage")
            loom.disableDeprecatedPomGeneration(this)

            artifact(tasks.remapJar)
            artifact(tasks.remapSourcesJar)

            artifactId = base.archivesName.get()

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                dependencyProjects.forEach {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", it.group)
                    dependencyNode.appendNode("artifactId", it.dependencyProject.base.archivesName.get())
                    dependencyNode.appendNode("version", it.version)
                }
            }
        }
    }

    repositories {
        val deployDir = project.findProperty("DEPLOY_DIR")
        if (deployDir != null) {
            maven(deployDir)
        }
    }
}

idea {
    module {
        for (fileName in listOf("run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}
