plugins {
    id("java")
}

group = "net.azisaba"
version = "1.16+1.0.0"

// MythicMobs 5.12 targets Java 21, which is also required by Paper 1.21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "azisaba-repo"
        url = uri("https://repo.azisaba.net/repository/maven-public/")
    }
    maven {
        name = "lumine"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    // MythicMobs 5.2+ changed MythicItem#getFile() to return java.io.File.
    compileOnly("io.lumine:Mythic-Dist:5.12.0")
    compileOnly("net.azisaba:lifepvelevel:1.3.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
