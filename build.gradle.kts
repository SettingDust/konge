import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.PluginDependency

plugins {
    kotlin("jvm") version "1.5.0"

    id("org.spongepowered.gradle.plugin") version "1.1.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val kotlinVersion: String by project
val spongeVersion: String by project
val pluginId: String by project
val pluginName: String by project
val pluginVersion: String by project

group = "com.github.konge"
version = "$pluginVersion-$kotlinVersion"


repositories {
    mavenCentral()
}

sponge {
    apiVersion(spongeVersion)
    plugin(pluginId) {
        loader(PluginLoaders.JAVA_PLAIN)
        displayName(pluginName)
        mainClass("io.github.konge.Konge")
        description("Fluent Kotlin developing on Sponge")
        contributor("SettingDust") {
            description("Owner")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

fun DependencyHandlerScope.shadowApi(dep: Any) {
    api(dep)
    shadow(dep)
}

fun DependencyHandlerScope.shadowApi(
    dep: String,
    dependencyConfiguration: Action<ExternalModuleDependency>
) {
    api(dep, dependencyConfiguration)
    shadow(dep, dependencyConfiguration)
}

dependencies {
    shadowApi(kotlin("stdlib-jdk8"))
    shadowApi(kotlin("reflect"))
    shadowApi("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.0-RC")

    shadowApi("org.spongepowered:configurate-extra-kotlin:4.0.0") {
        isTransitive = false
    }
}

tasks {
    jar { enabled = false }
    build { dependsOn(shadowJar) }
    artifacts { archives(shadowJar) }
    shadowJar {
        configurations = listOf(project.configurations.shadow.get())
        archiveClassifier.set("")

        minimize {
            exclude(
                "META-INF/com.android.tools/**",
                "META-INF/proguard/**",
                "META-INF/maven/**",
                "META-INF/versions/**",
                "DebugProbesKt.bin",
                "org/intellij/lang/annotations/**",
                "org/jetbrains/annotations/**"
            )
        }
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}