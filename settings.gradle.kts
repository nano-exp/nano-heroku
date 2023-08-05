pluginManagement {
    plugins {
        id("org.springframework.boot") version "2.6.7"
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
    }
}

rootProject.name = "nano-api"
include(
    "algorithm",
    "common",
    "service",
    "worker",
    "www",
)
