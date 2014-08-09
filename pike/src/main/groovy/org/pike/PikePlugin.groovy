package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.pike.holdertasks.*
import org.pike.info.InfoHostsTask
import org.pike.logging.LogConfigurator
import org.pike.model.defaults.Defaults
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.host.HostGroup
import org.pike.model.operatingsystem.Operatingsystem
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.04.13
 * Time: 09:43
 * To change this template use File | Settings | File Templates.
 */

public class PikePlugin implements Plugin<Project> {


    private Collection <DefaultTask> containerTasks = new ArrayList<DefaultTask>()

    private String PIKE_REMOTE_GROUP = "Pike remote"

    private Logger log

    /**
     * {@inheritDoc}
     */
    public void apply(Project project) {

        LogConfigurator.configureLogging(project)

        log = LoggerFactory.getLogger(PikePlugin.class)
        log.info("Info testlog")
        log.debug("Debug testlog")
        log.trace("Trace testLog")

        log.info("Applying " + getClass().name)

        project.plugins.apply(BasePlugin.class)

        configureContainerTasks(project) // must be first

        configureInfoTasks(project)
        configureModel(project)
    }

    public configureInfoTasks (Project project) {
        log.info("Configure task 'hosts' ")
        InfoHostsTask infoHostsTask = project.tasks.create('hosts', InfoHostsTask)
        infoHostsTask.group = PIKE_REMOTE_GROUP
        infoHostsTask.description = 'Shows infos about all hosts'
    }





    /**
     * configures containertasks as holder for the concrete tasks
     * @param project project
     */
    public void configureContainerTasks (Project project) {
        //Task holds all the installPike sub tasks of all current modelelements
        log.info("Configure task 'install' ")
        InstallTask installTask = project.tasks.create("install", InstallTask)
        installTask.description = "Installs all environments configured for the current host"
        installTask.group = PIKE_REMOTE_GROUP
        containerTasks.add(installTask)


        //Task holds all the deinstall sub tasks of all current modelelements
        log.info("Configure task 'deinstall' ")
        DeinstallTask deinstallTask = project.tasks.create("deinstall", DeinstallTask)
        deinstallTask.description = "Deinstalls all environments configured for the current host"
        deinstallTask.group = PIKE_REMOTE_GROUP
        containerTasks.add(deinstallTask)

        //Task holds all the checkenv sub tasks of all current modelelements
        CheckTask checkTask = project.tasks.create("checkenv", CheckTask)
        checkTask.description = "Checks all environments configured for the current host to be installed"
        checkTask.group = PIKE_REMOTE_GROUP
        containerTasks.add(checkTask)

        //task is triggered first to checkenv model and make some resolving things

        log.info("Configure task 'resolveModel' ")
        ResolveModelTask resolveModelTask = project.tasks.create("resolveModel", ResolveModelTask)

        log.info("Configure task 'deriveTasks' ")
        DeriveTasksTask deriveTasksTask = project.tasks.create("deriveTasks", DeriveTasksTask)
        deriveTasksTask.dependsOn resolveModelTask

        for (DefaultTask nextContainerTast : containerTasks) {
            if (log.debugEnabled)
                log.debug("Task ${nextContainerTast} dependsOn ${deriveTasksTask}")
            nextContainerTast.dependsOn deriveTasksTask
        }
    }

    /**
     * configures model, registers extensions
     * @param project project
     */
    public void configureModel (Project project) {
        log.info("Creating extensions for the model")
        project.plugins.apply(BasePlugin)

        def operatingsystems = project.container(Operatingsystem)
        project.extensions.operatingsystems = operatingsystems

        def hostgroups = project.container(HostGroup)
        project.extensions.hostgroups = hostgroups

        NamedDomainObjectContainer<Environment> hosts = project.container(Host)
        project.extensions.hosts = hosts
        hosts.all { Host host ->
          host.project = project
        }

        NamedDomainObjectContainer<Environment> environments = project.container(Environment)
        environments.all { Environment env->
            env.project = project
        }
        project.extensions.environments = environments

        project.extensions.create("defaults", Defaults, project)



    }






}
