plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.dunctebot"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

application {
    mainClass.set("com.dunctebot.twitch.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "mysql", name = "mysql-connector-java", version = "8.0.27")
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = "2.12.4")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.9")

    implementation(group = "com.github.twitch4j", name = "twitch4j", version = "1.9.0")

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "3.14.9")
    implementation("net.sf.trove4j:trove4j:3.0.3")
}

tasks {
    compileJava {
        // options.compilerArgs.add("--enable-preview")
        options.isIncremental = true
    }
    wrapper {
        gradleVersion = "7.2"
        distributionType = Wrapper.DistributionType.ALL
    }
    shadowJar {
        archiveClassifier.set("")
    }
}
