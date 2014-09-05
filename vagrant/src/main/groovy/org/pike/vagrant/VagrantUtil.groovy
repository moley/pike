package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.pike.model.Vagrant
import org.pike.model.host.Host

/**
 * Created by OleyMa on 07.08.14.
 */
@Slf4j
class VagrantUtil {

    public static File getWorkingDir (final Project project, final Host host) {
        File buildDir = new File (project.buildDir, 'hosts')
        File hostDir = new File (buildDir, host.name)
        return hostDir
    }

    public static Vagrant findVagrant (Project project, Host host, NamedDomainObjectContainer<Vagrant> vagrantContainer) {

        String nameOfOs = host.operatingsystem.name
        log.info("findVagrant host $host.name with os $nameOfOs")

        for (Vagrant vagrant: vagrantContainer) {
            if (vagrant.name == nameOfOs)
                return vagrant
        }

        return null
    }



}
