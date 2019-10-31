package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.ConfigureEclipseTask
import org.pike.tasks.ConfigureIdeaTask
import org.pike.tasks.EclipseConfiguration
import org.pike.tasks.IdeaConfiguration
import org.pike.tasks.InstallEclipseTask
import org.pike.tasks.InstallIdeaTask
import org.pike.tasks.InstallVsCodeTask
import org.pike.tasks.StartEclipseTask
import org.pike.tasks.StartIdeaTask


class PikeExtension extends Configurable {

    public static final String NAME = 'pike'

    Git git

    boolean force

    Eclipse eclipse

    Idea idea

    VsCode vsCode

    List<String> initialBuildTasks = ['testClasses']

    public PikeExtension (final Project project) {
        this.project = project
    }

    void vscode (Closure closure) {
        vsCode = new VsCode()
        project.configure(vsCode, closure)

        project.tasks.register('installVisual', InstallVsCodeTask)
    }

    void idea (Closure closure) {
        idea = new Idea()
        project.configure(idea, closure)

        InstallIdeaTask installIdeaTask = project.tasks.register('installIdea', InstallIdeaTask).get()

        ConfigureIdeaTask configureIdeaTask = project.tasks.register('configureIdea', ConfigureIdeaTask).get()

        StartIdeaTask startIdeaTask = project.tasks.register("startIdea", StartIdeaTask).get()
        startIdeaTask.dependsOn(installIdeaTask)
        startIdeaTask.dependsOn(configureIdeaTask)

    }

    void eclipse (Closure closure) {
        eclipse = new Eclipse()
        project.configure(eclipse, closure)

        project.apply plugin: 'com.diffplug.gradle.oomph.ide' // because adding tasks to the graph is only allowed in configuration phase

        InstallEclipseTask prepareEclipseTask = project.tasks.register('installEclipse', InstallEclipseTask).get()
        prepareEclipseTask.finalizedBy(project.tasks.ideSetupP2) // Dependency to ide task of goomph
        prepareEclipseTask.finalizedBy(project.tasks.ideSetupWorkspace) // Dependency to ide task of goomph


        ConfigureEclipseTask configureEclipseTask = project.tasks.register('configureEclipse', ConfigureEclipseTask).get()

        StartEclipseTask startEclipseTask = project.tasks.register("startEclipse", StartEclipseTask).get()
        startEclipseTask.dependsOn(project.tasks.ide)
        startEclipseTask.dependsOn(prepareEclipseTask)
        startEclipseTask.dependsOn(configureEclipseTask)
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

    void checkOverlappingConfigurations () {

        if (git == null)
            return

        Collection<Configuration> configurations = new ArrayList<Configuration>()

        for (Module next: git.modules) {
            Configuration mergedConfiguration = getMergedConfiguration(next.configuration)
            configurations.add(mergedConfiguration)
        }

        if (configurations.size() >= 2) {
            if (eclipse) {
                EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
                eclipseConfiguration.check(configurations)
            }
            if (idea) {
                IdeaConfiguration ideaConfiguration = new IdeaConfiguration()
                ideaConfiguration.check(configurations)
            }

            if (vsCode) {
                //TODO
            }
        }

    }


}
