plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.26.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.example.demo")
    mainClass.set("com.example.demo.Launcher")
}

// include Main-Class in jar manifest so the built jar can be executed directly
tasks.named<Jar>("jar") {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
}

// Runnable fat JAR including JavaFX, MySQL driver, and all dependencies
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("all")
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
    // Exclude module descriptors so the JAR runs on classpath (not module path)
    exclude("module-info.class")
    exclude("META-INF/versions/*/module-info.class")
    mergeServiceFiles()
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.mysql:mysql-connector-j:9.6.0")
    implementation("at.favre.lib:bcrypt:0.10.2")
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}