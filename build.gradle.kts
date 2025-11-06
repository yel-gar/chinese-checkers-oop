plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "ru.vsu.cs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "25"
    modules = listOf("javafx.controls", "javafx.graphics")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("Main")
}