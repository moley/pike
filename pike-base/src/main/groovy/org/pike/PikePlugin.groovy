package org.pike

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.pike.configuration.PikeExtension
import org.pike.configuration.WithGradlePropertiesEnricher
import org.pike.tasks.CloneTask
import org.pike.tasks.ConfigureTask
import org.pike.tasks.DeleteTask
import org.pike.tasks.InstallTask

/**
 * Plugin implementation class for pike
 **/
public class PikePlugin implements Plugin<Project> {

    public final static String PIKE_GROUP = 'Pike'

    @Override
    void apply(Project project) {

        project.plugins.apply(BasePlugin) //for clean task

        project.logger.lifecycle("JDK:        " + System.getProperty('java.version'))
        project.logger.lifecycle("JavaHome   " + System.getenv('JAVA_HOME'))
        project.logger.lifecycle("Gradle:     " + project.gradle.gradleVersion)


        PikeExtension pikeExtension = project.extensions.create(PikeExtension.NAME, PikeExtension, project)
        project.afterEvaluate {
            WithGradlePropertiesEnricher withGradlePropertiesEnricher = new WithGradlePropertiesEnricher()
            withGradlePropertiesEnricher.enrich(project, pikeExtension)

            pikeExtension.checkOverlappingConfigurations ()
        }

        InstallTask installTask = project.tasks.register('install', InstallTask).get()
        ConfigureTask configureTask = project.tasks.register('configure', ConfigureTask).get()
        CloneTask cloneTask = project.tasks.register('clone', CloneTask).get()
        DeleteTask deleteTask = project.tasks.register('delete', DeleteTask).get()

        installTask.dependsOn configureTask
        installTask.dependsOn cloneTask


    }
}
