plugins {
  id("net.kyori.indra")
  id("net.kyori.indra.publishing")
}

// Project layout

val minecraftVersion: String by project
val adventureVersion: String by project

description = "Constants generated from Minecraft $minecraftVersion for use in Adventure"
allprojects {
  group = "net.kyori"
  version = "$minecraftVersion+b1" // TODO: how should this be versioned?

  repositories {
    mavenCentral()
  }
}

repositories {
  maven("https://libraries.minecraft.net")
}

indra {
  github("KyoriPowered", "adventure-vanilla-data") {
    ci = true
  }
  mitLicense()
}

// Set up the actual generation

val generator by configurations.creating {
  attributes {
    attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class, Category.LIBRARY))
    attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
    // attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 15) // TODO: Our project metadata doesn't seem to be right?
  }
}

dependencies {
  api("net.kyori:adventure-api:$adventureVersion")
  generator(project(":generator"))
}

// Plug in the generator

/** Customized GenerateSources task that has an output directory property */
abstract class GenerateSources : JavaExec() {

  /**
   * The directory to write sources to
   */
  @get:org.gradle.api.tasks.OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  init {
    group = "generation"
    description = "Generate sources based on Minecraft game data"
  }
}

val generateSources by tasks.registering(GenerateSources::class) {
  val generatorTarget: String by project
  // Execute generator contained in subproject
  classpath = generator
  mainClass.set("net.kyori.adventure.data.generator.GameDataGenerator")
  javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(generatorTarget)) })

  // Arguments: <output directory>
  outputDirectory.set(project.layout.buildDirectory.dir("generated/data-src"))
  argumentProviders += CommandLineArgumentProvider {
    listOf(outputDirectory.get().asFile.absolutePath)
  }
}

sourceSets.main {
  java.srcDir(generateSources)
}

