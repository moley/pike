package org.pike.vagrant.integration

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.pike.vagrant.VagrantUtil
import spock.lang.Specification

/**
 * Created by OleyMa on 18.09.14.
 */
abstract class VagrantRoundtripTest extends Specification {


    protected abstract File getProject ()


    protected abstract boolean withLocal ()

    protected abstract String getHost ()

    protected abstract String getOs ()


    Project project
    File projectDir
    String output

    def setup() {
        projectDir = getProject()
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        if (VagrantUtil.doesVmExist(host)) {
            succeed("stopVmtesthost", "--info")
            succeed("deleteVmtesthost")

            if (VagrantUtil.doesVmExist(host))
              throw new IllegalStateException("Could not remove started box $host. Remove it manually with <vagrant box remove $host>.")
        }

        File box = new File (project.buildDir, "box/$host")
        if (box.exists())
            FileUtils.deleteDirectory(box)

        if (box.exists())
            throw new IllegalStateException("Box directory ${box.absolutePath} could not be removed, please remove it manually")
    }

    def "provisionLinux"() {
        given:

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
        succeed("installPikeVm", "--host",  host)
        then:
        output.contains("BUILD SUCCESSFUL")

        when:
        succeed("provision", "--host",  host)
        then:
        output.contains("BUILD SUCCESSFUL")

    }



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
            argsList.addAll(args)

            launcher.withArguments(argsList.toArray(new String [argsList.size()]))
            launcher.setStandardOutput(outputStream);
            launcher.setStandardError(outputStream);

            // Run the build
            launcher.run();
        } finally {
            // Clean up
            output = outputStream.toString()
            connection.close();
        }
    }




}
