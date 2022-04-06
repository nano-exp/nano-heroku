plugins {
    id("io.spring.dependency-management")
    id("org.springframework.boot")
    id("java")
}

description = "Batch and scheduled tasks"

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
    // spring
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    // project common
    implementation(project(":common"))
    // test
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
