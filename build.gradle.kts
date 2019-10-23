import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.Options
import com.wynprice.cursemaven.CurseMavenResolver
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.jetbrains.kotlin.fir.resolve.calls.TowerScopeLevel
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*
import java.io.*

buildscript {
    repositories {
        mavenLocal()
        maven(url = "https://maven.fabricmc.net/")
    }

}

plugins {
    id("fabric-loom")
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.wynprice.cursemaven") version "1.2.2"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


val archives_base_name: String by project
val mod_version: String by project
val maven_group: String by project

val standalone: String by project

val isStandalone: Boolean = !project.hasProperty("standalone") || standalone == "true"

fun prop(name : String) = project.property(name)

base {
    archivesBaseName = archives_base_name
}

version = mod_version
group = maven_group

minecraft {
}

repositories {
    maven(url = "http://maven.fabricmc.net/")
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://mod-buildcraft.com/maven")
    maven(url = "https://minecraft.curseforge.com/api/maven")
    maven(url = "http://tehnut.info/maven")
    maven(url = "https://maven.jamieswhiteshirt.com/libs-release/")
    jcenter()

}


dependencies {

    fabric()

    modDependency("net.fabricmc:fabric-language-kotlin:${prop("fabric_kotlin_version")}")
    modDependency("com.lettuce.fudge:fabric-drawer:${prop("drawer_version")}")

    devEnvMod("me.shedaniel:RoughlyEnoughItems:${prop("rei_version")}") {
        exclude(group = "io.github.prospector.modmenu")
    }
    devEnvMod("mcp.mobius.waila:Hwyla:${prop("waila_version")}")
    devEnvMod("com.jamieswhiteshirt:developer-mode:1.0.14")
    devEnvMod("gamemodeoverhaul:GamemodeOverhaul:1.0.1.0")

}

fun DependencyHandlerScope.fabric() {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    mappings("net.fabricmc:yarn:${prop("yarn_mappings")}")
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_version")}")
}

fun String.runCommand(logFile: String/*workingDir: File*/) {
    ProcessBuilder(*split(" ").toTypedArray())
            .redirectOutput(ProcessBuilder.Redirect.to(File(logFile)))
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(300, TimeUnit.SECONDS)
}

tasks.register("publishVariants") {
    group = "upload"
    doLast {
        ("gradlew.bat curseforge300824 -Pstandalone=true").runCommand("standalone_log.txt")
        ("gradlew.bat curseforge300824 -Pstandalone=false").runCommand("modpack_log.txt")
    }

}



fun DependencyHandlerScope.modDependency(dep: String, dependencyConfiguration: Action<ExternalModuleDependency> = Action {}) {
    modImplementation(dep) {
        exclude(group = "net.fabricmc.fabric-api")
        dependencyConfiguration.execute(this)
    }

    if (isStandalone) include(dep)
}

fun DependencyHandlerScope.devEnvMod(dep: String, dependencyConfiguration: Action<ExternalModuleDependency> = Action {}) {
    modRuntime(dep) {
        exclude(group = "net.fabricmc.fabric-api")
        dependencyConfiguration.execute(this)
    }
}

fun DependencyHandlerScope.devEnvMod(dep: Dependency) {
    modRuntime(dep)
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
                mutableMapOf(
                        "version" to mod_version
                )
        )
    }
}


// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")
val remapSourcesJar = tasks.getByName<RemapSourcesJarTask>("remapSourcesJar")


// configure the maven publication
publishing {
    publications {
        create("standalone", MavenPublication::class.java) {
            // add all the jars that should be included when publishing to maven
            artifact(remapJar) {
                builtBy(remapJar)
            }
            artifact(sourcesJar) {
                builtBy(remapSourcesJar)
            }
            groupId = maven_group
            artifactId = archives_base_name + "-standalone"
            version = mod_version
        }

        create("modpack", MavenPublication::class.java) {
            // add all the jars that should be included when publishing to maven
            artifact(remapJar) {
                builtBy(remapJar)
            }
            artifact(sourcesJar) {
                builtBy(remapSourcesJar)
            }
            groupId = maven_group
            artifactId = archives_base_name + "-modpack"
            version = mod_version
        }
    }

    // select the repositories you want to publish to
    repositories {
        // uncomment to publish to the local maven
        // mavenLocal()
    }
}

curseforge {
    apiKey = if (project.hasProperty("curseforge_api_key")) prop("curseforge_api_key") else ""
    project(closureOf<CurseProject> {
        id = "300824"
        releaseType = "release"
        addGameVersion("Fabric")
        changelogType = "markdown"
        changelog = file("changelog.md")

        mainArtifact(remapJar, closureOf<CurseArtifact> {
            displayName = "${prop("mod_name")} $mod_version(${if (isStandalone) "Standalone" else "Modpack"})"
        })

        afterEvaluate {
            mainArtifact(remapJar)
            uploadTask.dependsOn(remapJar)
        }
    })

    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}


tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses", "-Xuse-experimental=kotlin.Experimental")
    kotlinOptions.jvmTarget = "1.8"
}
