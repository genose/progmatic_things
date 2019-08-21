// inspired by me.tatarka.gradle plugin
// see https://github.com/evant/gradle-retrolambda/blob/master/LICENSE.txt

package org.javafxports.jfxmobile.plugin

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.FileCollection
import org.gradle.process.JavaExecSpec
import org.gradle.util.VersionNumber


/**
 * Runs retrolambda with the given args.
 */
@CompileStatic
class RetrolambdaExec {

    private static final int COMMANDLINE_LENGTH_LIMIT = 3496

    FileCollection retrolambdaClasspath
    File inputDir
    File outputDir
    FileCollection includedFiles
    List<String> jvmArgs
    int bytecodeVersion
    boolean defaultMethods

    private final Project project

    RetrolambdaExec(Project project) {
        this.project = project
    }

    public void exec() {
        project.javaexec { JavaExecSpec exec ->
            def retrolambdaConfig = project.configurations.getByName("retrolambdaConfig")

            String path = retrolambdaClasspath.asPath

            exec.classpath = project.files(retrolambdaConfig)
            exec.main = 'net.orfjackal.retrolambda.Main'
            exec.jvmArgs = [
                    "-Dretrolambda.inputDir=$inputDir",
                    "-Dretrolambda.outputDir=$outputDir",
                    "-Dretrolambda.classpath=${path}",
                    "-Dretrolambda.bytecodeVersion=$bytecodeVersion",
            ]

            VersionNumber retrolambdaVersion = retrolambdaVersion(retrolambdaConfig)
            boolean requiresJavaAgent = !requireVersion(retrolambdaVersion, '1.6.0')
            if (requiresJavaAgent) {
                exec.jvmArgs "-javaagent:$exec.classpath.asPath"
            }

            boolean supportIncludeFiles = requireVersion(retrolambdaVersion, '2.1.0')
            if (supportIncludeFiles && classpathLengthGreaterThanLimit(path)) {
                File classpathFile = File.createTempFile("inc-", ".path")
                classpathFile.withWriter('UTF-8') { writer ->
                    for (String item : this.retrolambdaClasspath) {
                        writer.write(item + "\n")
                    }
                }
                classpathFile.deleteOnExit();
                exec.jvmArgs "-Dretrolambda.classpathFile=${classpathFile.absolutePath}"
            } else {
                exec.jvmArgs "-Dretrolambda.classpath=${path}"
            }

            if (includedFiles != null) {
                if (supportIncludeFiles && changeFileLengthGreaterThanLimit(includedFiles)) {
                    def includedFile = File.createTempFile("inc-", ".list")
                    includedFile.withWriter('UTF-8') { writer ->
                        for (File file : includedFiles) {
                            writer.write(file.toString() + "\n")
                        }
                    }
                    includedFile.deleteOnExit();
                    exec.jvmArgs "-Dretrolambda.includedFilesFile=${includedFile.absolutePath}"
                } else {
                    def includedArg = "-Dretrolambda.includedFiles=${includedFiles.join(File.pathSeparator)}"
                    exec.jvmArgs includedArg
                    project.logger.quiet(includedArg)
                }

            }

            if (defaultMethods) {
                exec.jvmArgs "-Dretrolambda.defaultMethods=true"
            }

            for (String arg : jvmArgs) {
                exec.jvmArgs arg
            }
        }
    }

    private static boolean classpathLengthGreaterThanLimit(String path) {
        return path.length() > COMMANDLINE_LENGTH_LIMIT
    }

    private static boolean changeFileLengthGreaterThanLimit(FileCollection includedFiles) {
        int total = 0
        for (File file : includedFiles) {
            total += file.toString().length()
            if (total > COMMANDLINE_LENGTH_LIMIT) {
                return true
            }
        }
        return false
    }

    private static VersionNumber retrolambdaVersion(Configuration retrolambdaConfig) {
        retrolambdaConfig.resolve()
        Dependency retrolambdaDep = retrolambdaConfig.dependencies.iterator().next()
        if (!retrolambdaDep.version) {
            // Don't know version
            return null
        }
        return VersionNumber.parse(retrolambdaDep.version)

    }

    private static boolean requireVersion(VersionNumber retrolambdaVersion, String version, boolean fallback = false) {
        if (retrolambdaVersion == null) {
            // Don't know version, assume fallback
            return fallback
        }
        def targetVersionNumber = VersionNumber.parse(version)
        return retrolambdaVersion >= targetVersionNumber
    }
    
    static String checkIfExecutableExists(String file) {
        new File(file).exists() || new File(file + '.exe').exists()
    }
}
