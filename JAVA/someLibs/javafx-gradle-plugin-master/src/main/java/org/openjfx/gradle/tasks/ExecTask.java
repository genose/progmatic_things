/*
 * Copyright (c) 2019, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjfx.gradle.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;
import org.javamodularity.moduleplugin.tasks.ModuleOptions;
import org.openjfx.gradle.JavaFXModule;
import org.openjfx.gradle.JavaFXOptions;
import org.openjfx.gradle.JavaFXPlatform;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class ExecTask extends DefaultTask {

    private final Project project;
    private JavaExec execTask;

    @Inject
    public ExecTask(Project project) {
        this.project = project;
        project.getPluginManager().withPlugin(ApplicationPlugin.APPLICATION_PLUGIN_NAME, e -> {
            execTask = (JavaExec) project.getTasks().findByName(ApplicationPlugin.TASK_RUN_NAME);
            if (execTask != null) {
                execTask.dependsOn(this);
            } else {
                throw new GradleException("Run task not found.");
            }
        });
    }

    @TaskAction
    public void action() {
        if (execTask != null) {
            JavaFXOptions javaFXOptions = project.getExtensions().getByType(JavaFXOptions.class);
            JavaFXModule.validateModules(javaFXOptions.getModules());

            var definedJavaFXModuleNames = new TreeSet<>(javaFXOptions.getModules());
            if (!definedJavaFXModuleNames.isEmpty()) {
                ModuleOptions moduleOptions = execTask.getExtensions().findByType(ModuleOptions.class);
                if (moduleOptions != null) {
                    definedJavaFXModuleNames.forEach(javaFXModule -> moduleOptions.getAddModules().add(javaFXModule));
                } else {
                    var javaFXModuleJvmArgs = List.of(
                            "--module-path", execTask.getClasspath()
                                    .filter(jar -> isJavaFXJar(jar, javaFXOptions.getVersion(), javaFXOptions.getPlatform()))
                                    .getAsPath());

                    var jvmArgs = new ArrayList<String>();

                    jvmArgs.add("--add-modules");
                    jvmArgs.add(String.join(",", definedJavaFXModuleNames));

                    List<String> execJvmArgs = execTask.getJvmArgs();
                    if (execJvmArgs != null) {
                        jvmArgs.addAll(execJvmArgs);
                    }
                    jvmArgs.addAll(javaFXModuleJvmArgs);

                    execTask.setJvmArgs(jvmArgs);
                }
            }
        } else {
            throw new GradleException("Run task not found. Please, make sure the Application plugin is applied");
        }
    }

    private static boolean isJavaFXJar(File jar, String version, JavaFXPlatform platform) {
        return jar.isFile() &&
                Arrays.stream(JavaFXModule.values()).anyMatch(javaFXModule ->
                        javaFXModule.getPlatformJarFileName(version, platform).equals(jar.getName()) ||
                        javaFXModule.getModuleJarFileName().equals(jar.getName()));
    }
}
