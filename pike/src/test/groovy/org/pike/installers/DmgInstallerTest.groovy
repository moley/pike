package org.pike.installers

import com.google.common.io.Files
import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper

class DmgInstallerTest {


    @Test(expected = IllegalArgumentException)
    void paramInstallDirNull () {
        File installationDir = Files.createTempDir()
        DmgInstaller dmgInstaller = new DmgInstaller()
        dmgInstaller.install(null, installationDir)
    }

    @Test(expected = IllegalArgumentException)
    void paramOutputDirNull () {
        File outputDir = Files.createTempDir()
        DmgInstaller dmgInstaller = new DmgInstaller()
        dmgInstaller.install(outputDir, null)
    }

    @Test
    void getDefaultInstallationDir () {
        DmgInstaller dmgInstaller = new DmgInstaller()
        File currentDir = new File ('').absoluteFile
        File installationDir = dmgInstaller.getDefaultInstallationDir(currentDir)
        Assert.assertNotEquals("InstallationDir incorrect", installationDir.absolutePath, currentDir.absolutePath)
    }

    @Test(expected = IllegalStateException)
    void noAppDir () {
        Project project = ProjectBuilder.builder().build()

        File tmpDir = Files.createTempDir()
        File mountDir = new File (tmpDir, 'mount')
        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {String[] parameters -> ProcessResult result =  new ProcessResult() //mount
            println parameters
            result.output = """/dev/disk2          \tGUID_partition_scheme          \t
/dev/disk2s1        \tApple_HFS                      \t${mountDir.absolutePath}"""
            return result
        }

        mockedProcessWrapper.use {

            File rootDir = Files.createTempDir()
            File outputDir = new File(rootDir, 'output')
            File downloadedFile = new File(rootDir, 'downloadedFile')

            DmgInstaller dmgInstaller = new DmgInstaller()
            dmgInstaller.project = project
            dmgInstaller.install(outputDir, downloadedFile)
        }
    }

    @Test(expected = IllegalStateException)
    void twoAppDirs () {

        Project project = ProjectBuilder.builder().build()

        File tmpDir = Files.createTempDir()
        File mountDir = new File (tmpDir, 'mount')
        File appDir = new File (mountDir, "Some.app")
        File appDir2 = new File (mountDir, "Some2.app")
        appDir.mkdirs()
        appDir2.mkdirs()

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {String[] parameters -> ProcessResult result =  new ProcessResult() //mount
            println parameters
            result.output = """/dev/disk2          \tGUID_partition_scheme          \t
/dev/disk2s1        \tApple_HFS                      \t${mountDir.absolutePath}"""
            return result
        }

        mockedProcessWrapper.use {

            File rootDir = Files.createTempDir()
            File outputDir = new File(rootDir, 'output')
            File downloadedFile = new File(rootDir, 'downloadedFile')

            DmgInstaller dmgInstaller = new DmgInstaller()
            dmgInstaller.project = project
            dmgInstaller.install(outputDir, downloadedFile)
        }

    }

    @Test(expected = IllegalStateException)
    void installUnmountError () {

        Project project = ProjectBuilder.builder().build()

        File tmpDir = Files.createTempDir()
        File mountDir = new File (tmpDir, 'mount')
        File appDir = new File (mountDir, "Some.app")
        appDir.mkdirs()

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {String[] parameters -> ProcessResult result =  new ProcessResult() //mount
            println parameters
            result.output = """/dev/disk2          \tGUID_partition_scheme          \t
/dev/disk2s1        \tApple_HFS                      \t${mountDir.absolutePath}"""
            return result
        }

        mockedProcessWrapper.demand.execute {String[] parameters ->
            println parameters
            ProcessResult processResult = new ProcessResult()
            processResult.resultCode = 1
            return processResult} //unmount
        mockedProcessWrapper.use {

            File rootDir = Files.createTempDir()
            File outputDir = new File(rootDir, 'output')
            File downloadedFile = new File(rootDir, 'downloadedFile')

            DmgInstaller dmgInstaller = new DmgInstaller()
            dmgInstaller.project = project
            dmgInstaller.install(outputDir, downloadedFile)
        }

    }

    @Test(expected = IllegalStateException)
    void installMountError () {
        Project project = ProjectBuilder.builder().build()

        File tmpDir = Files.createTempDir()
        File mountDir = new File (tmpDir, 'mount')
        File appDir = new File (mountDir, "Some.app")
        appDir.mkdirs()

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {String[] parameters -> ProcessResult result =  new ProcessResult() //mount
            result.resultCode = 1
            return result
        }

        mockedProcessWrapper.use {

            File rootDir = Files.createTempDir()
            File outputDir = new File(rootDir, 'output')
            File downloadedFile = new File(rootDir, 'downloadedFile')

            DmgInstaller dmgInstaller = new DmgInstaller()
            dmgInstaller.project = project
            dmgInstaller.install(outputDir, downloadedFile)
        }

    }

    @Test
    void install () {

        Project project = ProjectBuilder.builder().build()

        File tmpDir = Files.createTempDir()
        File mountDir = new File (tmpDir, 'mount')
        File appDir = new File (mountDir, "Some.app")
        appDir.mkdirs()

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {String[] parameters -> ProcessResult result =  new ProcessResult() //mount
            println parameters
            result.output = """/dev/disk2          \tGUID_partition_scheme          \t
/dev/disk2s1        \tApple_HFS                      \t${mountDir.absolutePath}"""
            return result
        }

        mockedProcessWrapper.demand.execute {String[] parameters ->
            println parameters
            return new ProcessResult()} //unmount
        mockedProcessWrapper.use {

            File rootDir = Files.createTempDir()
            File outputDir = new File(rootDir, 'output')
            File downloadedFile = new File(rootDir, 'downloadedFile')

            DmgInstaller dmgInstaller = new DmgInstaller()
            dmgInstaller.project = project
            dmgInstaller.install(outputDir, downloadedFile)
        }

    }

    @Test
    void getVolume () {

        DmgInstaller dmgInstaller = new DmgInstaller()
        String volume = dmgInstaller.getVolumePath ("""/dev/disk2          \tGUID_partition_scheme          \t
/dev/disk2s1        \tApple_HFS                      \t/Volumes/IntelliJ IDEA CE""")
        Assert.assertEquals ("/Volumes/IntelliJ IDEA CE", volume)
    }
}
