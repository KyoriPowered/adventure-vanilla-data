plugins {
  alias(libs.plugins.indra)
  id("org.spongepowered.gradle.vanilla")
  alias(libs.plugins.indra.checkstyle)
  alias(libs.plugins.indra.licenserSpotless)
}

minecraft {
  version(project.property("minecraftVersion") as String)
}

dependencies {
  implementation(libs.adventure.api)
  implementation(libs.javapoet)
  implementation(libs.asm)
  checkstyle(libs.stylecheck)
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
