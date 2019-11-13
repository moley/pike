package org.pike.installers

import com.google.common.io.Files
import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.FileType
import org.pike.configuration.OperatingSystem

class ToolInstallerTest {

    @Test
    public void call() {

        File downloadedFile = new File(Files.createTempDir(), 'bla.zip')
        File installPath = new File(Files.createTempDir(), 'installpath')

        final String NAME = "name"
        final String SOURCE = "https://bla.zip"

        def mockedInstallerFactory = new MockFor(InstallerFactory)
        mockedInstallerFactory.demand.getInstaller { return new NoImplInstaller(installPath) }
        def mockedDownload = new MockFor(Download)
        mockedDownload.demand.setName { String name -> Assert.assertEquals("Name invalid", name, NAME) }
        mockedDownload.demand.setProject { Project project -> Assert.assertNotNull(project) }
        mockedDownload.demand.setSource { String source -> Assert.assertEquals("Source invalid", source, SOURCE) }
        mockedDownload.demand.setFileType { FileType fileType -> println "FileType: " + fileType }
        mockedDownload.demand.setToDir { File toDir -> Assert.assertEquals("nameDownloaded", toDir.name) }
        mockedDownload.demand.setForce { boolean force -> println "Force: " + force }
        mockedDownload.demand.executeDownload {}
        mockedDownload.demand.getDownloadedFile { return downloadedFile }
        mockedDownload.use {
            mockedInstallerFactory.use {
                Project project = ProjectBuilder.builder().build()
                project.plugins.apply(PikePlugin)
                ToolInstallerBuilder toolInstallerBuilder = new ToolInstallerBuilder(project, NAME, "version")
                toolInstallerBuilder.all().url(SOURCE).installationPath(project.file('build/something'))
                ToolInstaller toolInstaller = toolInstallerBuilder.get(OperatingSystem.getCurrent())
                File installedPath = toolInstaller.install()
                Assert.assertEquals ("InstalledPath not valid", installPath, installedPath)
            }
        }

    }
}
