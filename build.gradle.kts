plugins {
  id("net.kyori.indra")
  id("fabric-loom") apply false // use the extension to make our hacky repository business less hacky
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

// We have to include repos from the generator project, since we don't do central declaration yet :/
// Create a loom extension but do nothing else, to avoid some of the duplication
// This is still super ugly though
val loom = project.extensions.create("__loom", net.fabricmc.loom.LoomGradleExtension::class, project)
repositories {
  // Fabric and MC
  maven("https://maven.fabricmc.net/") {
    name = "fabric"
  }
  maven("https://libraries.minecraft.net/") {
    name = "mojang"
  }

  // Local caches for mapped mods
  // Copied from AbstractPlugin
  flatDir {
    name = "userLocalCacheFiles"
    dir(loom.rootProjectBuildCache)
  }

  flatDir {
    name = "userLocalRemappedMods"
    dir(loom.remappedModCache)
  }

  flatDir {
    name = "minecraftMapped"
    // from MinecraftMappedProvider.addDependencies
    dir(loom.userCache.resolve("$minecraftVersion-mapped-net.minecraft.mappings-$minecraftVersion-v2"))
  }
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

