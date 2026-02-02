package com.example.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class BomGeneratorPlugin implements Plugin<Project> {
    void apply(Project project) {

// ==========================================================
// PART 1: Per-Module Tasks (Runs on every subproject)
// ==========================================================

// Helper to generate local reports
        def generateLocalReport = { configName, fileName, title ->
            def dependencies = [:]
            try {
// Safely check if configuration exists (e.g., skip modules without Java)
                if (project.configurations.findByName(configName)) {
                    project.configurations.getByName(configName).resolvedConfiguration.resolvedArtifacts.each { artifact ->
                        def id = artifact.moduleVersion.id
                        dependencies[id.group + ":" + id.name] = id.version
                    }
                }
            } catch (Exception e) {
// Ignore resolution errors for cleaner output
            }

            if (dependencies.isEmpty()) return

            def sb = new StringBuilder()
            sb.append("BOM: ${project.name} (${title})\n")
            sb.append("------------------------------------------------\n")
            dependencies.sort().each { name, version ->
                sb.append(String.format("%-40s | %s\n", name, version))
            }

// Print to console and write to file
            println sb.toString()
            def file = project.layout.buildDirectory.file("${fileName}.txt").get().asFile
            file.parentFile.mkdirs()
            file.text = sb.toString()
        }

        project.tasks.register("generateBossBOM") {
            group = "reporting"
            description = "Generates BOM for this specific module (Runtime)."
            doLast { generateLocalReport("runtimeClasspath", "bom-boss", "Production") }
        }

        project.tasks.register("generateDevBOM") {
            group = "reporting"
            description = "Generates BOM for this specific module (Dev)."
            doLast { generateLocalReport("compileClasspath", "bom-dev", "Development") }
        }

// ==========================================================
// PART 2: Aggregated Task (Runs ONLY on the Root Project)
// ==========================================================

// Only register this task if we are in the Root Project
        if (project == project.rootProject) {
            project.tasks.register("generateAggregatedBOM") {
                group = "reporting"
                description = "Scans ALL submodules and generates a master BOM."

                doLast {
                    def masterMap = [:] // Key: "Group:Artifact", Value: [Version: "1.0", Modules: ["api", "core"]]

// 1. Iterate over all subprojects
                    project.subprojects.each { sub ->
                        try {
// We look at runtimeClasspath for the master report
                            def config = sub.configurations.findByName("runtimeClasspath")
                            if (config) {
                                config.resolvedConfiguration.resolvedArtifacts.each { artifact ->
                                    def id = artifact.moduleVersion.id
                                    def key = id.group + ":" + id.name

                                    if (!masterMap.containsKey(key)) {
                                        masterMap[key] = [version: id.version, modules: []]
                                    }
// Add this module to the list of consumers
                                    masterMap[key].modules.add(sub.name)
                                }
                            }
                        } catch (Exception e) {
// Skip projects that can't be resolved (e.g. documentation modules)
                        }
                    }

// 2. Generate the Master CSV
                    def csv = new StringBuilder()
                    csv.append("Group,Artifact,Version,Used By Modules\n")

                    masterMap.sort().each { key, data ->
                        def parts = key.split(":")
// Join module names with a semi-colon for the CSV column
                        def moduleList = data.modules.join(";")
                        csv.append("${parts[0]},${parts[1]},${data.version},${moduleList}\n")
                    }

// 3. Output
                    def masterFile = project.layout.buildDirectory.file("bom-aggregated.csv").get().asFile
                    masterFile.parentFile.mkdirs()
                    masterFile.text = csv.toString()

                    println "\n[✓] Aggregated Master BOM generated: ${masterFile.absolutePath}"
                    println " (Contains dependencies from ${project.subprojects.size()} modules)"
                }
            }
        }
    }
}
