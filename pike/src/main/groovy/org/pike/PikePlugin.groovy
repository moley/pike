package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.pike.autoinstall.AutoinstallTask
import org.pike.common.ProjectInfo
import org.pike.holdertasks.*
import org.pike.model.defaults.Defaults
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.host.HostGroup
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.remotetasks.StartRemoteBuildTask
import org.pike.tasks.CheckModelTask
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

    public static String LOGPATH = "logs"


    /**
     * Method configured logging with an configuration file
     * @param project  project
     * @param logConfFile  configuration file
     */
    private static void configureLoggingByFile (final Project project, final File logConfFile) {
        println ("Using " + logConfFile.absolutePath + " to configure logging")

        def loggerfactory = loadClass(project, "org.slf4j.LoggerFactory")
        def joranconfigurator = loadClass(project, "ch.qos.logback.classic.joran.JoranConfigurator")
        def context = loggerfactory.getILoggerFactory();

        def configurator = joranconfigurator.newInstance()
        configurator.setContext(context)
        configurator.doConfigure(logConfFile)

    }

    /**
     * load a class with gradle classloader
     * @param project project
     * @param fqn  fqn of class
     * @return class
     */
    private static Class loadClass (final Project project, final String fqn) {
        ClassLoader gradleClassloader = project.gradle.class.classLoader
        return gradleClassloader.loadClass(fqn)
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Project project) {
        File logfile = project.file("logback.xml")
        if (logfile.exists())
          configureLoggingByFile(project, logfile)
        else
          println ("No logback configuration $logfile.absolutePath available")

        log = LoggerFactory.getLogger(PikePlugin.class)

        log.info("Applying " + getClass().name)

        project.plugins.apply(BasePlugin.class)

        configureContainerTasks(project) // must be first

        configureRemoteTasks(project)
        configureAutoinstallFeatures(project)
        configureModel(project)
    }

    /**
     * configures autoinstall features
     * @param project project
     */
    public void configureAutoinstallFeatures (Project project) {
        AutoinstallTask autoinstallTask = project.tasks.create("autoinstall", AutoinstallTask)
        autoinstallTask.group = PIKE_REMOTE_GROUP
        autoinstallTask.dependsOn project.tasks.resolveModel
        autoinstallTask.dependsOn project.tasks.assemble
        autoinstallTask.description = "Installs pike runtime on all hosts configured in the model"
    }

    /**
     * configures remote task to start a build on all hosts
     * @param project
     */
    public void configureRemoteTasks (Project project) {
        StartRemoteBuildTask startremotebuildTask = project.tasks.create("configureRemotes", StartRemoteBuildTask)
        startremotebuildTask.group = PIKE_REMOTE_GROUP
        startremotebuildTask.dependsOn project.tasks.resolveModel
        startremotebuildTask.description = "Start pike on all hosts configured in the model"
    }

    /**
     * configures containertasks as holder for the concrete tasks
     * @param project project
     */
    public void configureContainerTasks (Project project) {
        log.debug("configure container tasks")

        //Task holds all the installPike sub tasks of all current modelelements
        InstallTask installTask = project.tasks.create("install", InstallTask)
        installTask.description = "Installs all environments configured for the current host"
        installTask.group = PIKE_REMOTE_GROUP
        containerTasks.add(installTask)


        //Task holds all the deinstall sub tasks of all current modelelements
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

        ResolveModelTask resolveModelTask = project.tasks.create("resolveModel", ResolveModelTask)

        DeriveTasksTask deriveTasksTask = project.tasks.create("deriveTasks", DeriveTasksTask)
        deriveTasksTask.dependsOn resolveModelTask

        CheckModelTask checkmodelTask = project.tasks.create(ProjectInfo.CHECKMODELTASK, CheckModelTask)
        checkmodelTask.dependsOn deriveTasksTask

        for (DefaultTask nextContainerTast : containerTasks) {
            nextContainerTast.dependsOn checkmodelTask
        }




    }

    /**
     * configures model, registers extensions
     * @param project project
     */
    public void configureModel (Project project) {
        project.plugins.apply(BasePlugin)

        def operatingsystems = project.container(Operatingsystem)
        project.extensions.operatingsystems = operatingsystems

        def hostgroups = project.container(HostGroup)
        project.extensions.hostgroups = hostgroups

        def hosts = project.container(Host)
        project.extensions.hosts = hosts

        NamedDomainObjectContainer<Environment> environments = project.container(Environment)
        environments.all { Environment env->
            env.project = project
        }
        project.extensions.environments = environments

        project.extensions.create("defaults", Defaults, project)



    }






}
