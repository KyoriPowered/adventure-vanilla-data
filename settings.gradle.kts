pluginManagement {
  repositories {
    maven("https://maven.fabricmc.net") {
      name = "fabric"
    }
    gradlePluginPortal()
  }

  plugins {
    val indraVersion = "1.2.1"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.license-header") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion
    id("net.kyori.indra.publishing") version indraVersion
    id("fabric-loom") version "0.5-SNAPSHOT"
  }
}

rootProject.name = "adventure-data"

include("generator")
