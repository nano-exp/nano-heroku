plugins {
    id("io.spring.dependency-management")
    id("org.springframework.boot")
    id("java")
}

description = "Service and API"

group = "nano"

java {
    val javaVersion: JavaVersion by extra
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // pinyin
    implementation("com.github.houbb:pinyin:0.3.1")
    // user agent parser
    implementation("com.github.ua-parser:uap-java:1.5.2")
    // json path
    implementation("com.jayway.jsonpath:json-path:2.7.0")
    // webp
    implementation("org.sejda.imageio:webp-imageio:0.1.6")
    // javascript
    implementation("org.graalvm.js:js:22.0.0.2")
    implementation("org.graalvm.js:js-scriptengine:22.0.0.2")
    // aop
    implementation("org.aspectj:aspectjweaver:1.9.8")
    // postgresql
    implementation("org.postgresql:postgresql:42.3.3")
    // spring
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // modules
    implementation(project(":common"))
    implementation(project(":www"))
    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // test
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
