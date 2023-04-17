plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.0.0"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}


group = "dev.kugge"
version = "0.0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v2.4.0")
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
}

tasks.processResources {
    filter { line -> line.replace("\${version}", project.version.toString()) }
}

tasks.shadowJar {
    archiveFileName.set("SignMarkers-${project.version}.jar")
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
