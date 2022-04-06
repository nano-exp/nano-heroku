plugins {
    id("java-library")
}

description = "Algorithm implementation"

group = "nano"

java {
    val javaVersion: JavaVersion by extra
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

repositories {
    mavenCentral()
}

dependencies {
    // math
    implementation("org.apache.commons:commons-math3:3.6.1")
    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

