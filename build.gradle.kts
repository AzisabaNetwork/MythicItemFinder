plugins {
    id("java")
}

group = "net.azisaba"
version = "1.0-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://repo.azisaba.net/repository/maven-public/") }
    maven {
        name = "lumine"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.15.2-R0.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:4.13.0")
    compileOnly("net.azisaba:lifepvelevel:1.3.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
