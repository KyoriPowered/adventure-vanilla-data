plugins {
  id("net.kyori.indra")
  id("org.spongepowered.gradle.vanilla")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.licenser.spotless")
}

minecraft {
  version(project.property("minecraftVersion") as String)
}

dependencies {
  val adventureVersion: String by project

  implementation("net.kyori:adventure-api:$adventureVersion")
  implementation("com.squareup:javapoet:1.13.0")
  implementation("org.ow2.asm:asm:9.4")
  checkstyle("ca.stellardrift:stylecheck:0.2.0")
}

configurations.runtimeElements {
  extendsFrom(configurations.minecraft.get())
}

indra {
  javaVersions {
    val generatorTarget: String by project
    target(generatorTarget.toInt())
  }
}
