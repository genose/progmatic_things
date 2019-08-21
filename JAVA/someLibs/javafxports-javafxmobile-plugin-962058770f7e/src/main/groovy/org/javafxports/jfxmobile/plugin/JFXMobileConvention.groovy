package org.javafxports.jfxmobile.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 *
 * @author joeri
 */
class JFXMobileConvention {

    /**
     * The fully qualified name of the application's Preloader class.
     */
    String preloaderClassName

    final Project project

    JFXMobileConvention(Project project) {
        this.project = project
    }

    def explodeAarDependencies(Configuration...configurations) {
        configurations.each { configuration ->
            Set<File> files = configuration.copy().resolve()
            files.findAll {
                it.name.endsWith('.aar')
            }.each { aarFile ->
                def aarFileWithoutExtension = aarFile.name.take(aarFile.name.lastIndexOf('.'))
                final File explodedDirectory = new File(aarFile.parent, "exploded")
                if (!explodedDirectory.exists()) {
                    project.logger.info("Explode aar file: $aarFile into $explodedDirectory")

                    if (!explodedDirectory.mkdirs()) {
                        throw new GradleException("Unable to create base directory to explode aar into: " + explodedDirectory)
                    }

                    project.copy {
                        from project.zipTree(aarFile)
                        into project.file(explodedDirectory)
                        include 'classes.jar'
                        include 'libs/*.jar'
                        rename('classes.jar', "${aarFileWithoutExtension}.jar")
                    }
                }

                if (project.file("$explodedDirectory.absolutePath/${aarFileWithoutExtension}.jar").exists()) {
                    project.dependencies.add(configuration.name, project.files("$explodedDirectory.absolutePath/${aarFileWithoutExtension}.jar"))
                }
                if (project.file("$explodedDirectory.absolutePath/libs").exists()) {
                    project.files(project.file("$explodedDirectory.absolutePath/libs").listFiles()).findAll {
                        it.name.endsWith('.jar')
                    }.each {
                        project.dependencies.add(configuration.name, project.files(it))
                    }
                }
            }
        }
    }
}
