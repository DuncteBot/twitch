plugins {
    java
}

group = "com.dunctebot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "com.github.twitch4j", name = "twitch4j", version = "1.5.0")
}

tasks {
    wrapper {
        gradleVersion = "7.1.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
