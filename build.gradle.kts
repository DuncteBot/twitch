plugins {
    java
}

group = "com.dunctebot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")

    implementation(group = "com.github.twitch4j", name = "twitch4j", version = "1.5.0")
}

tasks {
    wrapper {
        gradleVersion = "7.1.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
