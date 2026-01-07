plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5"
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
}

val groupName = "me.M64DiamondStar"
val artifactName = "EffectMaster"
val pluginVersion = "1.5.0-beta3"

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
    maven {
        url = uri("https://repo.tcoded.com/releases")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("com.bergerkiller.bukkit:TrainCarts:1.19.2-v1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    shadow("io.ktor:ktor-client-core:3.2.3")
    shadow("io.ktor:ktor-client-cio:3.2.3")
    shadow("io.ktor:ktor-client-content-negotiation:3.2.3")
    shadow("io.ktor:ktor-serialization-kotlinx-json:3.2.3")

    shadow("com.tcoded:FoliaLib:0.5.1")
    shadow("org.bstats:bstats-bukkit:3.1.0")
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

    relocate("com.tcoded", "me.m64diamondstar.effectmaster.libs.FoliaLib")
    relocate("org.bstats", "me.m64diamondstar.effectmaster.libs.bstats")

    // Ktor core + client
    relocate("io.ktor", "me.m64diamondstar.effectmaster.libs.ktor")

    // Ktor transitive deps
    //relocate("kotlinx.coroutines", "me.m64diamondstar.effectmaster.libs.kotlinx.coroutines")
    //relocate("kotlinx.serialization", "me.m64diamondstar.effectmaster.libs.kotlinx.serialization")
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