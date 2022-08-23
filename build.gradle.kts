plugins {
    id("java")

    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginYmlBukkit)
    alias(libs.plugins.runPaper)

}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    useJUnitPlatform()
}

group = "net.odinallfather.yggdrasil"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly(libs.paper)

    implementation(libs.annotations)
    implementation(libs.reflectasm)
    implementation(libs.anvilgui)

    testImplementation(libs.reflectasm)
    testImplementation(libs.mockbukkit)
    implementation("org.junit.jupiter:junit-jupiter:5.9.0")

}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
    }
    runServer {
        minecraftVersion("1.18.2")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}.${archiveExtension.getOrElse("jar")}")
    }
}

bukkit {
    main = "${rootProject.group}.YggdrasilInventoryPlugin"
    apiVersion = "1.18"
    name = "YggdrasilInventory"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    author = "OdinAllfather"

    commands {
        add(create("test"))
    }
}