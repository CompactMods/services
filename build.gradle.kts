plugins {
    id("java")
}

group = "dev.compactmods"
version = "0.0.1"

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