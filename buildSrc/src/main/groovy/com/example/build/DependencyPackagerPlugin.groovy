package com.example.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Compression

class DependencyPackagerPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.register("packageDependencies", Tar) {
            group = "distribution"
            description = "Packages all runtime dependencies into a single tar.gz file."

            archiveBaseName.set("${project.name}-dependencies")
            compression = Compression.GZIP
            destinationDirectory.set(project.layout.buildDirectory)

            doFirst { println "Packaging runtime dependencies..." }

            from(project.configurations.getByName("runtimeClasspath")) {
                into "libs"
            }

            doLast {
                println "[✓] Dependencies packaged: ${archiveFile.get().asFile.absolutePath}"
            }
        }
    }
}