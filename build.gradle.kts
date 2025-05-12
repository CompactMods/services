plugins {
    id("java")
    alias(neoforged.plugins.moddev)
}

group = "dev.compactmods"
version = "0.0.1"

repositories {
    mavenCentral()
}

neoForge {
    neoFormVersion = neoforged.versions.neoform.get()

    addModdingDependenciesTo(sourceSets.test.get())

    parchment {
        minecraftVersion = libs.versions.parchmentMC.get()
        mappingsVersion = libs.versions.parchment.get()
    }
}

dependencies {
    implementation("it.unimi.dsi:fastutil:8.5.12")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

tasks.test {
    useJUnitPlatform()
}