plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.mj8lnir"
version = "0.0.1-SNAPSHOT"
description = "audio-tag-editor"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.github.RouHim:jaudiotagger:1.4.24")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml")
}

tasks.register<Exec>("jpackageExe") {
    dependsOn(tasks.named("bootJar"))

    val jarTask = tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar").get()
    val jarName = jarTask.archiveFileName.get()
    val jarDir = jarTask.destinationDirectory.get().asFile
    val jarPath = jarDir.resolve(jarName).absolutePath
    val outputDir = layout.buildDirectory.dir("jpackage").get().asFile

    doFirst {
        outputDir.mkdirs()
    }

    commandLine(
        "jpackage",
        "--name", "AudioTagEditor",
        "--input", jarDir.absolutePath,
        "--main-jar", jarName,
        "--main-class", "com.mj8lnir.audiotageditor.AudioTagEditorApplicationKt",
        "--type", "exe",
        "--dest", outputDir.absolutePath,
        "--win-console"
    )
}

