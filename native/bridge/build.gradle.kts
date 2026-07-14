import java.util.Properties

plugins { id("jasmine.android.library") }

val rustWorkspaceDir = rootProject.layout.projectDirectory.dir("rust")
val ffiEntryCrateDir = rustWorkspaceDir.dir("crates/ffi_entry")
val ffiEntryCargoManifest = ffiEntryCrateDir.file("Cargo.toml")
val ffiEntryUdlFile = ffiEntryCrateDir.file("src/jasmine_core.udl")
val generatedKotlinDir = layout.buildDirectory.dir("generated/uniffi/kotlin")
val generatedJniLibsDir = layout.buildDirectory.dir("generated/rustJniLibs")
val hostRustTargetDir = layout.buildDirectory.dir("rustHost")

abstract class GenerateUniFfiKotlinTask : Exec() {
  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty
}

fun cargoExecutable(): String =
  if (System.getProperty("os.name").startsWith("Windows")) "cargo.exe" else "cargo"

fun androidSdkDir(): File {
  val localProperties = rootProject.file("local.properties")
  if (localProperties.exists()) {
    val properties = Properties().apply { localProperties.inputStream().use(::load) }
    properties.getProperty("sdk.dir")?.let {
      return file(it)
    }
  }
  System.getenv("ANDROID_HOME")?.let {
    return file(it)
  }
  System.getenv("ANDROID_SDK_ROOT")?.let {
    return file(it)
  }
  error("Android SDK not found. Set sdk.dir in local.properties or ANDROID_HOME.")
}

fun androidNdkDir(): File {
  val ndkRoot = androidSdkDir().resolve("ndk")
  return ndkRoot.listFiles(File::isDirectory)?.maxByOrNull { it.name }
    ?: error("Android NDK not found under ${ndkRoot.absolutePath}.")
}

android {
  namespace = "com.lhzkml.jasmineagent.core.rust"

  sourceSets {
    getByName("main") {
      jniLibs.directories.add(generatedJniLibsDir.get().asFile.absolutePath)
    }
  }
}

val buildRustAndroid =
  tasks.register<Exec>("buildRustAndroid") {
    group = "build"
    description = "Builds the Rust UniFFI shared library for Android ABIs."
    workingDir = ffiEntryCrateDir.asFile
    environment("ANDROID_NDK_HOME", androidNdkDir().absolutePath)
    environment("ANDROID_NDK_ROOT", androidNdkDir().absolutePath)
    commandLine(
      cargoExecutable(),
      "ndk",
      "-t",
      "arm64-v8a",
      "-t",
      "armeabi-v7a",
      "-t",
      "x86_64",
      "-o",
      generatedJniLibsDir.get().asFile.absolutePath,
      "build",
      "--lib",
      "--release",
    )
    inputs.dir(ffiEntryCrateDir)
    outputs.dir(generatedJniLibsDir)
  }

val generateUniFfiKotlin =
  tasks.register<GenerateUniFfiKotlinTask>("generateUniFfiKotlin") {
    group = "build"
    description = "Generates Kotlin bindings from the Rust UniFFI shared library."
    dependsOn(buildRustAndroid)
    workingDir = ffiEntryCrateDir.asFile
    outputDirectory.set(generatedKotlinDir)
    doFirst {
      outputDirectory.get().asFile.deleteRecursively()
      outputDirectory.get().asFile.mkdirs()
    }
    commandLine(
      cargoExecutable(),
      "run",
      "--manifest-path",
      ffiEntryCargoManifest.asFile.absolutePath,
      "--features=uniffi-cli",
      "--bin",
      "uniffi-bindgen",
      "--",
      "generate",
      "--no-format",
      "--language",
      "kotlin",
      ffiEntryUdlFile.asFile.absolutePath,
      "--crate",
      "jasmine_core",
      "--out-dir",
      outputDirectory.get().asFile.absolutePath,
    )
    inputs.file(ffiEntryUdlFile)
    doLast {
      outputDirectory
        .get()
        .asFile
        .walkTopDown()
        .filter { it.isFile && it.extension == "kt" }
        .forEach { generatedFile ->
          val generatedText = generatedFile.readText()
          if ("UNUSED_EXPRESSION" !in generatedText) {
            generatedFile.writeText(
              generatedText.replace(
                """@file:Suppress("NAME_SHADOWING")""",
                """@file:Suppress("NAME_SHADOWING", "UNUSED_EXPRESSION")""",
              )
            )
          }
        }
    }
  }

androidComponents {
  onVariants { variant ->
    variant.sources.kotlin?.addGeneratedSourceDirectory(
      generateUniFfiKotlin,
      GenerateUniFfiKotlinTask::outputDirectory,
    )
  }
}

val hostLibraryName =
  if (System.getProperty("os.name").startsWith("Windows")) "jasmine_core.dll"
  else if (System.getProperty("os.name").startsWith("Mac")) "libjasmine_core.dylib"
  else "libjasmine_core.so"

val buildRustHost =
  tasks.register<Exec>("buildRustHost") {
    group = "verification"
    description = "Builds the host Rust UniFFI library for JVM unit tests."
    workingDir = ffiEntryCrateDir.asFile
    commandLine(
      cargoExecutable(),
      "build",
      "--manifest-path",
      ffiEntryCargoManifest.asFile.absolutePath,
      "--lib",
      "--release",
      "--target-dir",
      hostRustTargetDir.get().asFile.absolutePath,
    )
    inputs.dir(ffiEntryCrateDir)
    outputs.file(hostRustTargetDir.map { it.file("release/$hostLibraryName") })
  }

tasks.named("preBuild") { dependsOn(generateUniFfiKotlin) }

tasks.withType<Test>().configureEach {
  dependsOn(buildRustHost)
  systemProperty("jna.library.path", hostRustTargetDir.get().file("release").asFile.absolutePath)
  systemProperty(
    "uniffi.component.jasmine_core.libraryOverride",
    hostRustTargetDir.get().file("release/$hostLibraryName").asFile.absolutePath,
  )
}

dependencies {
  implementation("${libs.kotlin.jna.get()}@aar")
  testImplementation(libs.junit)
  testRuntimeOnly(libs.kotlin.jna)
}
