package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.pike.PikePlugin
import org.pike.model.host.Host

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

        DefaultTask prepareVmsTask = project.tasks.create('prepareVms', DefaultTask)
        DefaultTask startVmsTask = project.tasks.create('startVms', DefaultTask)
        DefaultTask stopVmsTask = project.tasks.create('stopVms', DefaultTask)

        project.afterEvaluate {
            for (Host nextHost : project.hosts) {

                String hostSuffix = nextHost.name

                log.info("Creating prepareVm for host $nextHost with suffix $hostSuffix")

                PrepareVmTask prepareVmTask = project.tasks.create("prepareVm$hostSuffix", PrepareVmTask)
                prepareVmTask.host = nextHost
                prepareVmsTask.dependsOn prepareVmTask

                log.info("Creating startVm for host $nextHost")
                StartVmTask startVmTask = project.tasks.create("startVm$hostSuffix", StartVmTask)
                startVmTask.host = nextHost
                startVmsTask.dependsOn startVmTask

                log.info("Creating stopVm for host $nextHost")
                StopVmTask stopVmTask = project.tasks.create("stopVm$hostSuffix", StopVmTask)
                stopVmTask.host = nextHost
                stopVmsTask.dependsOn stopVmTask


            }


        }



    }
}
