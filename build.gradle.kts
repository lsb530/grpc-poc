import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.text.SimpleDateFormat
import com.google.protobuf.gradle.id
import java.util.*

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("com.google.protobuf") version "0.9.4"
}

group = "org.boki"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val grpcVersion = "4.26.1"
val grpcProtoVersion = "1.62.2"
val grpcKotlinStubVersion = "1.4.1"
val tomcatAnnotationVersion = "6.0.53"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.google.protobuf:protobuf-kotlin:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcProtoVersion")
//    implementation("io.grpc:grpc-netty:$grpcProtoVersion")
//    implementation("io.grpc:grpc-stub:$grpcProtoVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinStubVersion")
    api("org.apache.tomcat:annotations-api:$tomcatAnnotationVersion") // necessary for Java 9+
}

val protobufRootDirPath = "build/generated/source/proto/main"

sourceSets {
    getByName("main") {
        java {
            srcDirs(
                "$protobufRootDirPath/java",
                "$protobufRootDirPath/kotlin",
                "$protobufRootDirPath/grpc",
//                "build/generated/source/proto/main/java",
//                "build/generated/source/proto/main/kotlin",
//                "build/generated/source/proto/main/grpc"
            )
        }
        proto {
            srcDir("src/main/resources/proto")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$grpcVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcProtoVersion"
        }
        id("grpc-kt") {
            // jdk8@jar를 붙이지 않으면 Could not find protoc-gen-grpc-kotlin-1.4.1-osx-aarch_64.exe (io.grpc:protoc-gen-grpc-kotlin:1.4.1) 에러가 발생함
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinStubVersion:jdk8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") // { option("lite") }
                id("grpc-kt") // { option("lite") }
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

tasks.getByName<BootJar>("bootJar") {
    val dateFormat = SimpleDateFormat("yyMMdd_HHmm")
    archiveFileName.set("${project.name}-${dateFormat.format(Date())}_${project.version}.jar")
    enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.getByName<BootRun>("bootRun") {
    environment("spring.output.ansi.console-available", "true")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
