plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "2.2.10"
    id("com.gradleup.shadow") version "8.3.5"
}

val groupName = "me.M64DiamondStar"
val artifactName = "EffectMaster"
val pluginVersion = "1.5.0-beta1+build2"

group = groupName
description = artifactName
version = pluginVersion

repositories {
    mavenLocal()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://ci.mg-dev.eu/plugin/repository/everything")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("com.bergerkiller.bukkit:TrainCarts:1.19.2-v1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    shadow("com.github.technicallycoded:FoliaLib:0.4.3")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    archiveFileName.set("EffectMaster-$pluginVersion.jar")
    configurations = listOf(project.configurations.getByName("shadow"))

    relocate("com.tcoded.folialib", "me.m64diamondstar.effectmaster.libs.folialib")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = groupName
            artifactId = artifactName
            version = pluginVersion

            from(components["java"])
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.5"
    distributionType = Wrapper.DistributionType.ALL
}