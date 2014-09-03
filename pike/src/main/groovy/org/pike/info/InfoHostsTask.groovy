package org.pike.info

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.TaskAction
import org.pike.model.host.Host
import org.pike.tasks.PikeTask

/**
 * Created by OleyMa on 30.07.14.
 */
class InfoHostsTask extends PikeTask{

    String completeString = ""

    @TaskAction
    public void show () {
        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        for (Host nextHost: hosts) {
            completeString += sprintf("%-20s %-20s %-15s %-10s", nextHost.name, nextHost.hostname, nextHost.ip, nextHost.operatingsystem.name) + '\n'
        }

        println(completeString)
    }
}
