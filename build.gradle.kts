plugins {
    idea
    java
    id("com.azuredoom.hytale-workspace") version "1.0.+"
}

subprojects {
    tasks.register("prepareKotlinBuildScriptModel") {
        dependsOn(rootProject.tasks.named("prepareKotlinBuildScriptModel"))
    }
}

hytaleWorkspace {
    modProjects = listOf(":queens_fall", ":dialogue")
    hostProject = ":queens_fall"

    manifestGroup = property("manifest_group").toString()
    hytaleVersion = property("hytale_version").toString()
    patchline = property("patchline").toString()
}
