plugins {
    id("io.spring.dependency-management")
    id("org.springframework.boot") apply false
    id("java-library")
}

description = "Common utilities"

group = "nano"

java {
    val javaVersion: JavaVersion by extra
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // jetbrains annotations
    api("org.jetbrains:annotations:23.0.0")
    // spring
    compileOnly("org.springframework.boot:spring-boot-starter-mail")
    compileOnly("org.springframework.boot:spring-boot-starter-jdbc")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    // test
    testImplementation("org.springframework.boot:spring-boot-starter-json")
    testImplementation("org.springframework.boot:spring-boot-starter-mail")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
