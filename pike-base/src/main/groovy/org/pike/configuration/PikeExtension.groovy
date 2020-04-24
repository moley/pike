package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.*
import org.pike.utils.ObjectMergeUtil

class PikeExtension extends Configurable {

    public static final String NAME = 'pike'

    Git git

    Eclipse eclipse

    Idea idea

    List<String> initialBuildTasks = ['testClasses']

    ObjectMergeUtil<Configuration> configurationMergeUtil = new ObjectMergeUtil<Configuration>()

    PikeExtension (final Project project) {
        this.project = project
    }

    void idea (Closure closure) {
        idea = new Idea()
        idea.project = project
        project.configure(idea, closure)

        InstallIdeaTask installIdeaTask = project.tasks.register('installIdea', InstallIdeaTask).get()

        ConfigureIdeaTask configureIdeaTask = project.tasks.register('configureIdea', ConfigureIdeaTask).get()

        StartIdeaTask startIdeaTask = project.tasks.register("startIdea", StartIdeaTask).get()
        startIdeaTask.dependsOn(installIdeaTask)
        startIdeaTask.dependsOn(configureIdeaTask)

    }

    void eclipse (Closure closure) {
        eclipse = new Eclipse()
        eclipse.project = project
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
        return configurationMergeUtil.merge(configuration, specificConfiguration)
    }

    void checkOverlappingConfigurations () {

        if (git == null)
            return

        Collection<Configuration> configurations = new ArrayList<Configuration>()

        for (Module next: git.modules) {
            Configuration mergedConfiguration = getMergedConfiguration(next.configuration)
            if (mergedConfiguration != null)
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
        }

    }


}
