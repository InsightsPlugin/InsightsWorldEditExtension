plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version VersionConstants.shadowVersion
}

val name = "InsightsWorldEditExtension"
group = "dev.frankheijden.insights.extensions.worldedit"
version = "3.3.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    group = rootProject.group
    val subprojectName = project.name.toLowerCase()
    if (subprojectName != "plugin") {
        group = (group as String) + "." + subprojectName
    }
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.fvdh.dev/releases")
    }

    dependencies {
        compileOnly("dev.frankheijden.insights:Insights:${VersionConstants.insightsVersion}")
        compileOnly("io.papermc.paper:paper-api:${VersionConstants.minecraftVersion}")
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            sourceCompatibility = JavaVersion.VERSION_17.majorVersion
            targetCompatibility = JavaVersion.VERSION_17.majorVersion
        }
    }
}

dependencies {
    implementation(project(":Plugin", "shadow"))
    implementation(project(":WE", "shadow"))
    implementation(project(":FAWE", "shadow"))
}

tasks {
    clean {
        dependsOn("cleanJars")
    }

    build {
        dependsOn("shadowJar", "copyJars")
    }
}

tasks.register("cleanJars") {
    delete(file("jars"))
}

tasks.register<Copy>("copyJars") {
    from(tasks.findByPath("shadowJar"), {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    })
    into(file("jars"))
    rename("(.+)-all(.+)", "$1$2")
}
