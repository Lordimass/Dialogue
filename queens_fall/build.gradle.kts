plugins {
    idea
    java
    id("com.azuredoom.hytale-tools")
    id("com.azuredoom.hytalepublisher") version "1.1.1"
}

group = property("group").toString()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(property("java_version").toString().toInt()))
}

dependencies {
    implementation(project(":common"))
}

hytaleTools {
    javaVersion = property("java_version").toString().toInt()
    hytaleVersion = property("hytale_version").toString()
    manifestGroup = property("manifest_group").toString()
    modId = "queens_fall"
    modDescription = property("mod_description").toString()
    modUrl = property("mod_url").toString()
    mainClass = "net.queensfall.Main"
    modCredits = property("mod_author").toString()
    manifestDependencies = property("manifest_dependencies").toString()
    manifestOptionalDependencies = property("manifest_opt_dependencies").toString()
    curseforgeId = property("queens_fall_curseforge_project_id").toString()
    disabledByDefault = property("disabled_by_default").toString().toBoolean()
    includesPack = property("includes_pack").toString().toBoolean()
    patchline = property("patchline").toString()
}

repositories {
    mavenCentral()
}


hytalePublisher {
    curseforge {
        enabled = true
        projectId = property("queens_fall_curseforge_project_id").toString()
    }
}

