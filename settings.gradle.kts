pluginManagement {
  plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://repo.spongepowered.org/repository/maven-public/")
  }
}

plugins {
  id("org.spongepowered.gradle.vanilla")
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "adventure-vanilla-data"

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
  }
}

include("generator")
