plugins {
    java
    idea
    `maven-publish`
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("io.papermc.paperweight.userdev") version "1.4.0"
}

repositories {
    maven("https://jitpack.io")
}

// EnoughSlime
val modId: String by extra
val modJavaVersion: String by extra

// Minecraft
val minecraftVersion: String by extra

// Slimefun
val slimefunVersion: String by extra

val baseArchivesName = "${modId}-${minecraftVersion}-bukkit"
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
    paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")

    implementation("com.github.Slimefun:Slimefun4:${slimefunVersion}")

    dependencyProjects.forEach {
        implementation(it)
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
        register<MavenPublication>("bukkitJar") {
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
