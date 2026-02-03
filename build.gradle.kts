var envVersion: String = System.getenv("VERSION") ?: "9.9.9"
if (envVersion.startsWith("v"))
    envVersion = envVersion.trimStart('v')

plugins {
    id("java")
    id("maven-publish")
}

base.archivesName = "services"
group = "dev.compactmods"
version = envVersion

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation("it.unimi.dsi:fastutil:8.5.12")

    testImplementation(libs.bundles.jmh)
    testImplementation(mojang.dfu)
    testImplementation(libs.javafaker)
    testImplementation(libs.commons.io)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

val PACKAGES_URL = System.getenv("GH_PKG_URL") ?: "https://maven.pkg.github.com/compactmods/services"
publishing {
    publications.register<MavenPublication>("main") {
        from(components.getByName("java"))
    }

    repositories {
        // GitHub Packages
        maven(PACKAGES_URL) {
            name = "GitHubPackages"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}