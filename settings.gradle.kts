pluginManagement {
  plugins {
    val indraVersion = "2.0.6"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.license-header") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion
    id("net.kyori.indra.publishing") version indraVersion
    id("net.kyori.indra.publishing.sonatype") version indraVersion
    id("org.spongepowered.gradle.vanilla") version "0.2"
  }
}

plugins {
  id("org.spongepowered.gradle.vanilla")
}

rootProject.name = "adventure-vanilla-data"

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
  }
}

include("generator")
