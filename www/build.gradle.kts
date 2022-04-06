plugins {
    id("java-library")
}

description = "Website"

group = "nano"

java {
    val javaVersion: JavaVersion by extra
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    register<Exec>("setupNode") {
        commandLine("bash", "-c", "./get-node.sh && . .profile && pnpm install")
    }

    register("checkNode") {
        if (!file("node_modules").exists()) {
            dependsOn("setupNode")
        }
    }

    register<Exec>("start") {
        dependsOn("checkNode")
        commandLine("bash", "-c", ". .profile && npm start")
    }

    register<Exec>("buildAssets") {
        dependsOn("checkNode")
        commandLine("bash", "-c", ". .profile && npm run build")
    }

    register<Exec>("cleanAssets") {
        dependsOn("checkNode")
        commandLine("bash", "-c", ". .profile && npm run clean")
    }

    clean {
        dependsOn("cleanAssets")
    }

    processResources {
        if (!file("dist").exists() || gradle.startParameter.taskNames.contains("clean")) {
            dependsOn("buildAssets")
        }

        from("dist") {
            into("/META-INF/resources")
        }
    }
}
