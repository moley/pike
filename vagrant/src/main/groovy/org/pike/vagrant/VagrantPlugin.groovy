package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.pike.AutoinstallUtil
import org.pike.PikePlugin
import org.pike.model.Vagrant
import org.pike.model.host.Host
import org.pike.model.host.HostGroup

/**
 * Created by OleyMa on 07.06.14.
 */
@Slf4j
class VagrantPlugin implements Plugin<Project> {

    public final static String GROUP_VAGRANT = 'Vagrant'

    @Override
    void apply(Project project) {

        log.info("Apply plugin ${getClass().getName()}")

        project.plugins.apply(PikePlugin.class)

        //pro host:
        //      prepareVm
        //      startVm
        //      stopVm

        DefaultTask prepareVmsTask = project.tasks.create('createVms', DefaultTask)
        prepareVmsTask.group = GROUP_VAGRANT
        prepareVmsTask.description = 'Create all configured vms'
        DefaultTask startVmsTask = project.tasks.create('startVms', DefaultTask)
        startVmsTask.group = GROUP_VAGRANT
        startVmsTask.description = 'Start all configured vms'
        DefaultTask stopVmsTask = project.tasks.create('stopVms', DefaultTask)
        stopVmsTask.group = GROUP_VAGRANT
        stopVmsTask.description = 'Stop all configured vms'

        NamedDomainObjectContainer<Vagrant> vagrantContainer  = project.container(Vagrant, new NamedDomainObjectFactory<Vagrant>() {
            Vagrant create(String name) {
                def instantiator = project.services.get(Instantiator.class)
                return instantiator.newInstance(Vagrant, name, instantiator)
            }
        })
        project.extensions.vagrant = vagrantContainer


        project.afterEvaluate {

            log.info("hosts extensions: " + project.extensions.hosts.size())


            for (Host nextHost : project.extensions.hosts) {

                String hostname = nextHost.name

                log.info("Check host $hostname")

                Vagrant vagrant = VagrantUtil.findVagrant(project, nextHost, vagrantContainer)
                if (vagrant == null) {
                    log.warn("Could not find vagrant named $hostname")
                    continue
                }

                String hostSuffix = hostname

                log.info("Creating prepareVm for host $nextHost with suffix $hostSuffix")

                CreateVmTask createVmTask = project.tasks.create("createVm$hostSuffix", CreateVmTask)
                createVmTask.host = nextHost
                createVmTask.group = GROUP_VAGRANT
                createVmTask.vagrant = vagrant
                createVmTask.description = "Create configured vm belonging to host $hostname"
                prepareVmsTask.dependsOn createVmTask

                log.info("Creating startVm for host $nextHost")
                StartVmTask startVmTask = project.tasks.create("startVm$hostSuffix", StartVmTask)
                startVmTask.host = nextHost
                startVmTask.group = GROUP_VAGRANT
                startVmsTask.dependsOn startVmTask
                startVmTask.description = "Start configured vm belonging to host $hostname"

                log.info("Creating stopVm for host $nextHost")
                StopVmTask stopVmTask = project.tasks.create("stopVm$hostSuffix", StopVmTask)
                stopVmTask.host = nextHost
                stopVmTask.group = GROUP_VAGRANT
                stopVmsTask.dependsOn stopVmTask
                stopVmTask.description = "Stop configured vm for host $hostname"

            }

            InstallPikeInVmTask installPikeTask = project.tasks.create('installPikeVm', InstallPikeInVmTask)
            installPikeTask.group = GROUP_VAGRANT
            installPikeTask.description = "Install pike on configured vm belonging to parameterized or default host"


            StartRemoteBuildInVmTask startRemoteBuildInVmTask = project.tasks.create('provisionVm', StartRemoteBuildInVmTask)
            startRemoteBuildInVmTask.group = GROUP_VAGRANT
            startRemoteBuildInVmTask.description = "Provision plans to configured vm belonging to parameterized or default host"


        }
    }



}
