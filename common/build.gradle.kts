plugins {
    `java-library`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(property("java_version").toString().toInt()))
}

repositories {
    mavenCentral()
}

val lombokVersion = "1.18.40"

dependencies {
    compileOnly("org.jetbrains:annotations:26.1.0")
    compileOnly("org.jspecify:jspecify:1.0.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}