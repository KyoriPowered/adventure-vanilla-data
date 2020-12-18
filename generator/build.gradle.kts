plugins {
  id("net.kyori.indra")
  id("fabric-loom")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.license-header")
}

repositories {
  mavenCentral()
}

dependencies {
  val adventureVersion: String by project
  val minecraftVersion: String by project
  minecraft("com.mojang:minecraft:$minecraftVersion")
  mappings(minecraft.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:0.10.8")

  implementation("net.kyori:adventure-api:$adventureVersion")
  implementation("com.squareup:javapoet:1.13.0")
  checkstyle("ca.stellardrift:stylecheck:0.1")
}

configurations.runtimeElements {
  extendsFrom(configurations.getByName(net.fabricmc.loom.util.Constants.Configurations.MINECRAFT_NAMED))
}

indra {
  javaVersions {
    val generatorTarget: String by project
    target.set(generatorTarget.toInt())

    strictVersions.set(false)
  }
}

loom {
  remapMod = false // we don't need to produce production jars, just need to run the source generation
}