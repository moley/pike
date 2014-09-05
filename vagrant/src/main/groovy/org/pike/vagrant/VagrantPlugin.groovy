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
        DefaultTask startVmsTask = project.tasks.create('startVms', DefaultTask)
        startVmsTask.group = GROUP_VAGRANT
        DefaultTask stopVmsTask = project.tasks.create('stopVms', DefaultTask)
        stopVmsTask.group = GROUP_VAGRANT

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

                log.info("Check host $nextHost.name")

                Vagrant vagrant = VagrantUtil.findVagrant(project, nextHost, vagrantContainer)
                if (vagrant == null) {
                    log.warn("Could not find vagrant named $nextHost.name")
                    continue
                }

                String hostSuffix = nextHost.name

                log.info("Creating prepareVm for host $nextHost with suffix $hostSuffix")

                CreateVmTask createVmTask = project.tasks.create("createVm$hostSuffix", CreateVmTask)
                createVmTask.host = nextHost
                createVmTask.group = GROUP_VAGRANT
                createVmTask.vagrant = vagrant
                prepareVmsTask.dependsOn createVmTask

                log.info("Creating startVm for host $nextHost")
                StartVmTask startVmTask = project.tasks.create("startVm$hostSuffix", StartVmTask)
                startVmTask.host = nextHost
                startVmTask.group = GROUP_VAGRANT
                startVmsTask.dependsOn startVmTask

                log.info("Creating stopVm for host $nextHost")
                StopVmTask stopVmTask = project.tasks.create("stopVm$hostSuffix", StopVmTask)
                stopVmTask.host = nextHost
                stopVmTask.group = GROUP_VAGRANT
                stopVmsTask.dependsOn stopVmTask

            }

            InstallPikeInVmTask installPikeTask = project.tasks.create('installPikeVm', InstallPikeInVmTask)
            installPikeTask.group = GROUP_VAGRANT


            StartRemoteBuildInVmTask startRemoteBuildInVmTask = project.tasks.create('provisionVm', StartRemoteBuildInVmTask)
            startRemoteBuildInVmTask.group = GROUP_VAGRANT


        }
    }



}
