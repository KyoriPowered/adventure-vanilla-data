pluginManagement {
  plugins {
    val indraVersion = "1.2.1"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.license-header") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion
    id("net.kyori.indra.publishing") version indraVersion
    id("net.kyori.indra.publishing.sonatype") version indraVersion
    id("org.spongepowered.gradle.vanilla") version "0.1"
  }
}

rootProject.name = "adventure-vanilla-data"

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven ("https://files.minecraftforge.net/maven/") {
      name = "forge"
      content { includeGroup("net.minecraftforge") }
    }
    maven("https://libraries.minecraft.net/") {
      name = "minecraft"
      mavenContent { releasesOnly() }
    }
  }

}

include("generator")
