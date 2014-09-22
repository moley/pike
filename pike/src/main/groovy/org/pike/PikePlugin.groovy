package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.internal.reflect.Instantiator
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

        configureDefaults(project)

        checkModel(project)
    }

    public configureDefaults (Project project) {
        project.operatingsystems {

            linux {
                homedir = "/home/${project.defaults.defaultuser}"
                servicedir = "/etc/init.d"
                pikedir = '/opt/pike'
                globalconfigfile = '/etc/profile'
                pikejre32 = 'http://installbuilder.bitrock.com/java/jre1.7.0_67-linux.zip'
                pikejre64 = 'http://installbuilder.bitrock.com/java/jdk1.7.0_67-linux-x64.zip'
            }

            macosx {
                homedir = "/Users/${project.defaults.defaultuser}"
                servicedir = "/etc/init.d"
                pikedir = '/opt/pike'
                globalconfigfile = '/etc/profile'
                pikejre32 = 'http://installbuilder.bitrock.com/java/jre1.7.0_67-osx.zip'
            }

            suse {
                parent=linux
            }

            redhat {
                parent = linux
            }

            windows {
                homedir = "C:\\Users/${project.defaults.defaultuser}"
                appdir = "${homedir}\\jenkins"
                programdir = "${homedir}\\jenkins\\tools"
                pikedir = 'C:\\opt\\pike'
                appconfigfile = "${appdir}\\jenkins_global.bat"
                pikejre32 = 'http://installbuilder.bitrock.com/java/jre1.7.0_67-windows.zip'
                pikejre64 = 'http://installbuilder.bitrock.com/java/jre1.7.0_67-windows-x64.zip'
            }
        }
    }

    public configureInfoTasks (Project project) {
        log.info("Configure task 'hosts' ")
        InfoHostsTask infoHostsTask = project.tasks.create('hosts', InfoHostsTask)
        infoHostsTask.group = PIKE_REMOTE_GROUP
        infoHostsTask.description = 'Shows infos about all hosts'
    }



    private void checkModel (Project project) {
        log.info("Check model")

        project.afterEvaluate {

            for (Host nextHost : project.hosts) {
                if (nextHost.operatingsystem == null)
                    throw new IllegalStateException("You have to configure a operatingsystem for host $nextHost.name")

                for (String nextEnv : nextHost.getAllEnvironments(project)) {
                    Environment env = project.environments.findByName(nextEnv)
                    if (env == null)
                        throw new IllegalStateException("Cannot find environment $nextEnv which is defined for host $nextHost.name")
                }
            }

            log.info("Model is checked")
        }

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

        project.extensions.operatingsystems = project.container(Operatingsystem , new NamedDomainObjectFactory<Operatingsystem>() {
            Operatingsystem create(String name) {
                def instantiator = project.services.get(Instantiator.class)
                return instantiator.newInstance(Operatingsystem, name, instantiator)
            }
        })

        project.extensions.hostgroups = project.container(HostGroup, new NamedDomainObjectFactory<HostGroup>() {
            HostGroup create(String name) {
                def instantiator = project.services.get(Instantiator.class)
                return instantiator.newInstance(HostGroup, name, instantiator)
            }
        })


        project.extensions.hosts = project.container(Host, new NamedDomainObjectFactory<Host>() {
            Host create(String name) {
                def instantiator = project.services.get(Instantiator.class)
                return instantiator.newInstance(Host, name, instantiator)
            }
        })
        project.hosts.all { Host host ->
          host.project = project
        }

        project.extensions.environments = project.container(Environment, new NamedDomainObjectFactory<Environment>() {
            Environment create(String name) {
                def instantiator = project.services.get(Instantiator.class)
                return instantiator.newInstance(Environment, name, instantiator)
            }
        })
        project.environments.all { Environment env->
            env.project = project
        }

        project.extensions.create("defaults", Defaults, project)



    }






}
