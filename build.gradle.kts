plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "mx.edu.itson"
version = "0.0.1-SNAPSHOT"
description = "Proyecto-NOVA"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 1. Core de Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 2. Base de Datos: SQLite + Dialecto de la Comunidad (CORREGIDO)
    runtimeOnly("org.xerial:sqlite-jdbc")
    implementation("org.hibernate.orm:hibernate-community-dialects")


    // 3. Inteligencia Artificial (Motor para leer archivos .onnx)
    implementation("com.microsoft.onnxruntime:onnxruntime:1.17.1")

    // 4. Herramientas de Desarrollo (Lombok y DevTools)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // 5. Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 6. Documentación (Swagger)
    implementation("org.springdoc:springdoc-openapi-ui:1.6.10")
}

tasks.withType<Test> {
    useJUnitPlatform()
}