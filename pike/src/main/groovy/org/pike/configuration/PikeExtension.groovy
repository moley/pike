package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.PrepareEclipseTask
import org.pike.tasks.PrepareIdeaTask
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

        PrepareIdeaTask prepareIdeaTask = project.tasks.register('ideaPrepare', PrepareIdeaTask).get()
        prepareIdeaTask.pikeExtension = this

        StartIdeaTask startIdeaTask = project.tasks.register("ideaStart", StartIdeaTask).get()
        startIdeaTask.pikeExtension = this

    }

    void eclipse (Closure closure) {
        eclipse = new Eclipse()
        eclipse.pikeExtension = this
        project.configure(eclipse, closure)

        PrepareEclipseTask prepareEclipseTask = project.tasks.register('prepareEclipse', PrepareEclipseTask).get()
        prepareEclipseTask.pikeExtension = this
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
