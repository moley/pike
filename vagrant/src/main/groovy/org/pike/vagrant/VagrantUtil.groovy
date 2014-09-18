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

    /**
     * checks if a vm for a specific host does exist
     * @param project   project
     * @param host      host to check
     * @return  true: vm is added in vagrant, false: vm is not added in vagrant
     */
    public static boolean doesVmExist (final String hostname) {
        new ByteArrayOutputStream().withStream { os ->

            Process process = "vagrant box list".execute()
            process.consumeProcessOutputStream(os)
            if (process.waitFor() < 0)
                throw new IllegalStateException("vagrant box returned $process.exitValue()")

            String outputAsString = os.toString()
            for (String next : outputAsString.split(System.lineSeparator())) {
                if (next.startsWith(hostname + " "))
                    return true
                else
                    log.debug("Line <$next> does not contain hostname $hostname")
            }

            return false
        }
    }


    /**
     * get working dir for the given host
     * @param project   project
     * @param host      host to get working dir for
     * @return  working dir
     */
    public static File getWorkingDir (final Project project, final Host host) {
        File buildDir = new File (project.buildDir, 'hosts')
        File hostDir = new File (buildDir, host.name)
        return hostDir
    }

    /**
     * finds vagrant dsl
     * @param project       project
     * @param host          host to check if vagrant is configured for
     * @param vagrantContainer    container
     * @return vagrant dsl or null
     */
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
