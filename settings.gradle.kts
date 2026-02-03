dependencyResolutionManagement {
    addVersionCatalog(this, "mojang")
}

pluginManagement {
    plugins {
        id("idea")
        id("eclipse")
        id("maven-publish")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

include(":services")

fun addVersionCatalog(dependencyResolutionManagement: DependencyResolutionManagement, name: String) {
    dependencyResolutionManagement.versionCatalogs.create(name) {
        from(files("./gradle/$name.versions.toml"))
    }
}
