package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.InstallEclipseTask
import org.pike.tasks.InstallIdeaTask
import org.pike.tasks.StartEclipseTask
import org.pike.tasks.StartIdeaTask


class PikeExtension extends Configurable {

    public static final String NAME = 'pike'

    Git git

    boolean force

    Eclipse eclipse

    Idea idea

    public PikeExtension (final Project project) {
        this.project = project
    }

    void idea (Closure closure) {
        idea = new Idea()
        idea.pikeExtension = this
        project.configure(idea, closure)

        InstallIdeaTask prepareIdeaTask = project.tasks.register('ideaInstall', InstallIdeaTask).get()
        prepareIdeaTask.pikeExtension = this

        StartIdeaTask startIdeaTask = project.tasks.register("ideaStart", StartIdeaTask).get()
        startIdeaTask.pikeExtension = this

    }

    void eclipse (Closure closure) {
        eclipse = new Eclipse()
        eclipse.pikeExtension = this
        project.configure(eclipse, closure)

        project.apply plugin: 'com.diffplug.gradle.oomph.ide' // because adding tasks to the graph is only allowed in configuration phase

        InstallEclipseTask prepareEclipseTask = project.tasks.register('eclipseInstall', InstallEclipseTask).get()
        prepareEclipseTask.pikeExtension = this
        prepareEclipseTask.finalizedBy(project.tasks.ideSetupP2) // Dependency to ide task of goomph
        prepareEclipseTask.finalizedBy(project.tasks.ideSetupWorkspace) // Dependency to ide task of goomph

        StartEclipseTask startEclipseTask = project.tasks.register("eclipseStart", StartEclipseTask).get()
        startEclipseTask.pikeExtension = this
        startEclipseTask.dependsOn(project.tasks.ide)
        startEclipseTask.dependsOn(prepareEclipseTask)
    }

    void git (Closure closure) {
        git = new Git()
        git.pikeExtension = this
        project.configure(git, closure)
    }

    Configuration getMergedConfiguration (Configuration specificConfiguration) {
        Configuration globalConfiguration = configuration

        Configuration mergedConfiguration = new Configuration()
        mergedConfiguration.properties.each { prop, val ->
            if(prop in ["metaClass","class"]) return

            if (globalConfiguration != null) {
                Object valueFromGlobalConfiguration = globalConfiguration.getProperty(prop)
                if (valueFromGlobalConfiguration != null)
                    mergedConfiguration.setProperty(prop, valueFromGlobalConfiguration)
            }

            if (specificConfiguration != null) {
                Object valueFromSpecificConfiguration = specificConfiguration.getProperty(prop)
                if (valueFromSpecificConfiguration != null)
                    mergedConfiguration.setProperty(prop, valueFromSpecificConfiguration)
            }
        }

        return mergedConfiguration



    }


}
