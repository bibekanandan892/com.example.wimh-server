@file:Suppress("PropertyName")

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongo_version: String by project
val bcrypt_version: String by project
val apache_commons_version: String by project
val mongodb_driver_version: String by project
val firebase_admin_version: String by project
val koin_version: String by project



plugins {
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"

}


group = "com.example"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    //region Server
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")

    //endregion

    //region Client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")



    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")
    implementation("com.google.code.gson:gson:2.10")


    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    //endregion

    //Region MongoDB
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-async:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-id:$kmongo_version")
    implementation("org.mongodb:mongodb-driver-sync:$mongodb_driver_version")
    //endregion

    implementation("at.favre.lib:bcrypt:$bcrypt_version")

    implementation("org.apache.commons:commons-email:$apache_commons_version")

    implementation("com.google.firebase:firebase-admin:$firebase_admin_version")
    implementation("io.ktor:ktor-auth-jwt:1.6.1")
    //region Koin
    // Koin Core features
    implementation("io.insert-koin:koin-ktor:$koin_version")
    // Testing
    // Koin Test features
//    testImplementation ("io.insert-koin:koin-test:$koin_version")
    // Koin for JUnit 4
//    testImplementation ("io.insert-koin:koin-test-junit4:$koin_version")
//    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    //endregion
}


tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.example.ApplicationKt"))
        }
    }
}


//tasks{
//    fatJar {
//        archiveFileName.set("com.example.wimh-server-$version-all.jar")
//    }
//}



//tasks {
//    shadowJar {
//        manifest {
//            attributes(Pair("Main-Class", "com.example.ApplicationKt"))
//        }
//    }
//
//    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "1.8"
//    }
//
//    // Define the fatJar task for ktor
//    val fatJar = tasks.register<Jar>("fatJar") {
//        archiveClassifier.set("all")
//        from(sourceSets.main.get().output)
//        manifest {
//            attributes["Main-Class"] = "com.example.ApplicationKt"
//        }
//        dependsOn(configurations.runtimeClasspath)
//        from({
//            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//        })
//    }
//    // Configure the ktor plugin to use the fatJar task
//    configure<io.ktor.gradle.KtorPluginExtension> {
//        application {
//            mainClass.set("com.example.ApplicationKt")
//            executable.set(false)
//        }
//        packageApplication {
//            dependsOn(fatJar)
//            from(fatJar.get().archiveFile)
//            archiveFileName.set("com.example.wimh-server-$version-all.jar")
//        }
//    }
//}


