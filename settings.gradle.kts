pluginManagement {
	repositories {
        gradlePluginPortal()

		maven("https://maven.parchmentmc.org")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.papermc.io/repository/maven-public/")

		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}

		maven("https://repo.spongepowered.org/repository/maven-public/") {
			name = "Sponge Snapshots"
		}
	}

	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "net.minecraftforge.gradle") {
				useModule("${requested.id}:ForgeGradle:${requested.version}")
			}

			if (requested.id.id == "org.spongepowered.mixin") {
				useModule("org.spongepowered:mixingradle:${requested.version}")
			}
		}
	}
}

val minecraftVersion: String by settings

rootProject.name = "enoughslime-${minecraftVersion}"
include(
    "bukkit",
    "common", "common-api",
    "forge",
    "fabric"
)
