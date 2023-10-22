pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
//        maven("https://github.com/psiegman/mvn-repo/raw/master/releases")
    }
}

rootProject.name = "AI Text Translation"
include(":app")
