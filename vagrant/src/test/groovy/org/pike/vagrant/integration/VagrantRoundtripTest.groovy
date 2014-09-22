package org.pike.vagrant.integration

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.pike.vagrant.VagrantUtil
import spock.lang.Specification

/**
 * baseclass that implements the roundtrip to a vagrant integration test
 */
@Slf4j
abstract class VagrantRoundtripTest extends Specification {

    /**
     * must be overriden to return the path to the used project
     * @return path
     */
    protected abstract File getProject ()

    /**
     * must be overridden to return if local.gradle or build.gradle should be used
     * @return true: local.gradle is used as build file, false: build.gradle is used
     */
    protected abstract boolean withLocal ()

    /**
     * must be overriden to enable debugging
     * @return true: start with --debug, false: start without debug infos
     */
    protected abstract boolean debug ()

    /**
     * must be overriden to return if a clean test run should be processed or only a provisioning of the vm
     * @return true: recreate the vm, false: only provision
     */
    protected abstract boolean alwaysClean ()

    /**
     * must be overriden to return the host that should be tested
     * @return host
     */
    protected abstract String getHost ()

    /**
     * must be overridden to return the os that this host should use
     * @return operatingsystem
     */
    protected abstract String getOs ()


    Project project
    File projectDir
    String output

    /**
     * setup the project (removes an old vm if wanted)
     */
    def setup() {
        projectDir = getProject()
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        if (VagrantUtil.doesVmExist(host) && alwaysClean()) {
            succeed("stopVmtesthost", "--info")
            succeed("deleteVmtesthost")

            if (VagrantUtil.doesVmExist(host))
              throw new IllegalStateException("Could not remove started box $host. Remove it manually with <vagrant box remove $host>.")
        }
        else
            log.info('Skip removing old vm due to alwaysClean is disabled')

        File box = new File (project.buildDir, "box/$host")
        if (box.exists() && alwaysClean()) {
            FileUtils.deleteDirectory(box)

            if (box.exists())
                throw new IllegalStateException("Box directory ${box.absolutePath} could not be removed, please remove it manually")
        }
        else
            log.info('Skip removing files of old vm due to alwaysClean is disabled')
    }

    /**
     * test provisioning
     */
    def "provisionRoundtrip"() {
        given:

        if (! VagrantUtil.doesVmExist(host) || alwaysClean()) {
            when:
            succeed("createVm$host")
            then:
            output.contains("BUILD SUCCESSFUL")

            when:
            succeed("startVm$host")
            then:
            output.contains("BUILD SUCCESSFUL")

            when:
            succeed("prepareInstaller$os")
            then:
            output.contains("BUILD SUCCESSFUL")

            when:
            succeed("installPikeVm", "--host", host)
            then:
            output.contains("BUILD SUCCESSFUL")
        }
        else
            log.info('Skip creating vm due to alwaysClean is disabled or host does not exist')

        when:
        succeed("provisionVm", "--host",  host)
        then:
        output.contains("BUILD SUCCESSFUL")

    }

    /**
     * starts a gradle build with the correct settings
     * @param args  args
     */
    def succeed(String... args) {
        // Configure the connector and create the connection
        GradleConnector connector = GradleConnector.newConnector();

        connector.forProjectDirectory(projectDir)

        ProjectConnection connection = connector.connect()
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // Configure the build
            BuildLauncher launcher = connection.newBuild();

            Collection<String> argsList = new ArrayList<String>()
            if (withLocal()) {
                argsList.add('--build-file')
                argsList.add('local.gradle')
            }

            if (debug())
                argsList.add('--info')
            argsList.addAll(args)

            launcher.withArguments(argsList.toArray(new String [argsList.size()]))
            launcher.setStandardOutput(outputStream);
            launcher.setStandardError(outputStream);

            // Run the build
            launcher.run();
        } finally {
            // Clean up
            output = outputStream.toString()
            connection.close()
        }
    }




}
