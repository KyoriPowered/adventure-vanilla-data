plugins {
  id("net.kyori.indra")
  id("org.spongepowered.gradle.vanilla")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.license-header")
}

repositories {
  mavenCentral()
}

minecraft {
  version(project.property("minecraftVersion") as String)
}

dependencies {
  val adventureVersion: String by project

  implementation("net.kyori:adventure-api:$adventureVersion")
  implementation("com.squareup:javapoet:1.13.0")
  implementation("org.ow2.asm:asm:9.0")
  checkstyle("ca.stellardrift:stylecheck:0.1")
}

configurations.runtimeElements {
  extendsFrom(configurations.minecraftClasspath.get())
}

indra {
  javaVersions {
    val generatorTarget: String by project
    target.set(generatorTarget.toInt())

    strictVersions.set(false)
  }
}
