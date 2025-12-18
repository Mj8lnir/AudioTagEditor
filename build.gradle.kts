plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.mj8lnir"
version = "1.0.0.0"
description = "audio-tag-editor"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

springBoot {
    mainClass.set("com.mj8lnir.audiotageditor.AudioTagEditorApplicationKt")
}

tasks.bootJar {
    archiveClassifier.set("")
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
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

tasks.register<Exec>("jpackageExe") {
    dependsOn("bootJar")

    commandLine(
        "jpackage",
        "--type", "msi",
        "--name", "AudioTagEditor",
        "--app-version", project.version.toString(),
        "--vendor", "Nikolay Yabrov",
        "--input", "build/libs",
        "--main-jar", "${project.name}-${project.version}.jar",
        "--main-class", "org.springframework.boot.loader.launch.JarLauncher",
        "--dest", "build/installer",
        "--icon", "src/main/resources/bug.ico",
        "--description", "Audio .mp3 tags editor and garmin playlist manager",
        "--verbose",
        "--win-menu",
        "--win-shortcut",
        // JVM options
        "--java-options", "-Xmx512m",
        "--java-options", "-XX:NewRatio=4",
        "--java-options", "-XX:MaxHeapFreeRatio=10",
        "--java-options", "-XX:MinHeapFreeRatio=10",
        "--java-options", "-XX:ParallelGCThreads=4",
        "--java-options", "-XX:+UseStringDeduplication",
    )

    isIgnoreExitValue = true
}




