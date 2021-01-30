pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://repo-new.spongepowered.org/repository/maven-public/")
  }

  plugins {
    val indraVersion = "1.2.1"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.license-header") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion
    id("net.kyori.indra.publishing") version indraVersion
    id("net.kyori.indra.publishing.sonatype") version indraVersion
    id("org.spongepowered.gradle.vanilla") version "0.1-SNAPSHOT"
  }
}

rootProject.name = "adventure-vanilla-data"

include("generator")
