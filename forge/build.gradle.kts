plugins {
    java
    idea
    `maven-publish`
    id("net.minecraftforge.gradle") version("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version("1.+")
}

// EnoughSlime
val modId: String by extra
val modJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra

// Forge
val forgeVersion: String by extra

// JEI
val jeiVersion: String by extra

// Mappings
val parchmentVersionForge: String by extra

val baseArchivesName = "${modId}-${minecraftVersion}-fabric"
base {
    archivesName.set(baseArchivesName)
}

val dependencyProjects: List<ProjectDependency> = listOf(
    project.dependencies.project(":common")
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
    minecraft("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")

    compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}"))
    compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge-api:${jeiVersion}"))

    runtimeOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge:${jeiVersion}"))

    dependencyProjects.forEach {
        implementation(it)
    }
}

minecraft {
    mappings("parchment", parchmentVersionForge)

    runs {
        create("client") {
            taskName("runClientDev")
            property("forge.logging.console.level", "debug")
            workingDirectory(file("run/client/dev"))

            mods {
                create(modId) {
                    source(sourceSets.main.get())

                    for (p in dependencyProjects) {
                        source(p.dependencyProject.sourceSets.main.get())
                    }
                }
            }
        }
    }
}

tasks.jar {
    from(sourceSets.main.get().output)

    for (p in dependencyProjects) {
        from(p.dependencyProject.sourceSets.main.get().output)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    finalizedBy("reobfJar")
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)

    for (p in dependencyProjects) {
        from(p.dependencyProject.sourceSets.main.get().allJava)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveClassifier.set("sources")
}

artifacts {
    archives(tasks.jar.get())
    archives(sourcesJarTask.get())
}

publishing {
    publications {
        register<MavenPublication>("forgeJar") {
            artifact(tasks.jar.get())
            artifact(sourcesJarTask.get())

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
