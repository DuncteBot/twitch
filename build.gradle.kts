plugins {
    java
    application
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
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = "2.12.4")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")

    implementation(group = "com.github.twitch4j", name = "twitch4j", version = "1.5.1")

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "3.14.9")
}

tasks {
    wrapper {
        gradleVersion = "7.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}
