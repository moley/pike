package org.pike


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.pike.configuration.PikeExtension
import org.pike.tasks.InstallTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.04.13
 * Time: 09:43
 * To change this template use File | Settings | File Templates.
 */

public class PikePlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {

        project.plugins.apply(BasePlugin) //for clean task

        project.tasks.register('install', InstallTask)
        PikeExtension pikeExtension = project.extensions.create('pike', PikeExtension, project)


    }
}
