import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
//    implementation(project(":data:common"))

    api("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}
