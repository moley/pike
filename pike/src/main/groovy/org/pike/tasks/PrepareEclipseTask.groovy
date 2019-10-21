package org.pike.tasks


import org.gradle.api.tasks.TaskAction

class PrepareEclipseTask extends PikeTask {

    {
        description = 'Installs and configures an eclipse instance for the given project'
    }

    String version


    @TaskAction
    public void prepareEclipse () {

        getLogger().info("prepare eclipse ide")

    }
}
