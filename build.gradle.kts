plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.140"
    id("idea")
}

val minecraftVersion: String by extra
val modVersion: String by extra
val neoVersion: String by extra
val parchmentMappingsVersion: String by extra
val parchmentMinecraftVersion: String by extra
val neoforgeVersionRange: String by extra
val chemlibVersionRange: String by extra
val chemlibVersion: String by extra

val localRuntime: Configuration by configurations.creating

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

version = "$minecraftVersion-$modVersion"
group = "com.smashingmods.alchemylib"

repositories {
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

base {
    archivesName = "alchemylib"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    accessTransformers.from("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", "alchemylib")
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", "alchemylib")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", "alchemylib")
        }

        create("data") {
            data()
            programArguments.addAll("--mod", "alchemylib", "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            jvmArguments.add("-XX:+AllowEnhancedClassRedefinition")
            logLevel = org.slf4j.event.Level.INFO
        }
    }

    mods {
        create("alchemylib") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

configurations {
    runtimeClasspath.get().extendsFrom(localRuntime)
}

dependencies {
    implementation(files("../ChemLib-1211/build/libs/chemlib-${chemlibVersion}.jar"))
}

tasks.processResources {
    var replaceProperties = mapOf("minecraftVersion" to minecraftVersion, "neoVersion" to neoVersion,
        "neoforgeVersionRange" to neoforgeVersionRange, "modVersion" to modVersion, "chemlibVersionRange" to chemlibVersionRange
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

/*
publishing {
    publications {
        mavenJava(MavenPublication) {
            afterEvaluate {
                artifact project.jar
                artifact project.sourcesJar
                artifact project.javadocJar
            }
            setGroupId "smashingmods"
            setArtifactId "alchemylib"
        }
    }
    repositories {
        maven {
            url "https://maven.tamaized.com/releases"
            credentials {
                username secrets.getProperty("maven_username")
                password secrets.getProperty("maven_password")
            }
        }
    }
}
 */