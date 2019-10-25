package org.pike.installers

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper


class DmgInstaller implements Installer {

    Project project

    ProcessWrapper processWrapper = new ProcessWrapper()


    private File getAppDir (final String volumePath) {
        File appFolder = null
        File volumeFolder = new File(volumePath)
        for (File next: volumeFolder.listFiles()) {
            if (next.name.endsWith(".app")) {
                if (appFolder == null)
                  appFolder = next
                else
                    throw new IllegalStateException("More than one app folder found in " + volumePath + "(" + appFolder.name + "-" + next.name + ")")
            }
        }

        if (appFolder == null)
            throw new IllegalStateException("No app folder found in " + volumePath)

        return appFolder

    }

    @Override
    void install(File installationDir, File downloadedFile) {
        installationDir = new File ('/Applications').absoluteFile //because dmg must be installed to //Applications

        project.logger.lifecycle("Installing " + downloadedFile.name + " to " + installationDir.absolutePath)

        //Mount
        project.logger.info("Mounting " + downloadedFile.absolutePath)
        String[] mountCommand = ['hdiutil', 'mount', downloadedFile.absolutePath]
        ProcessResult resultMount = processWrapper.execute(mountCommand)
        if (resultMount.resultCode != 0)
            throw new IllegalStateException("Could not mount the dmg file ${downloadedFile.absolutePath}. Command $mountCommand results in error: " + resultMount.error)

        //Copy content
        String volumePath = getVolumePath(resultMount.output)
        File appFolderOrigin = getAppDir(volumePath)
        File appFolderInstalled = new File (installationDir, appFolderOrigin.name)

        if (!appFolderInstalled.exists()) {
            project.logger.lifecycle("Copy " + volumePath + " to " + installationDir.absolutePath)
            FileUtils.copyDirectory(appFolderOrigin, installationDir)
        }
        else
            project.logger.lifecycle("Installation dir " + appFolderInstalled.absolutePath + " already exists")

        //Unmount
        project.logger.info("Unmounting " + volumePath)
        String[] unmountCommand = ['hdiutil', 'unmount', volumePath]
        ProcessResult resultUnmount = processWrapper.execute(mountCommand)
        if (resultUnmount.resultCode != 0)
            throw new IllegalStateException("Could not unmount the dmg file ${volume}. Command $unmountCommand results in error: " + resultUnmount.error)

    }

    private String getVolumePath(final String outputMount) {
        String[] lines = outputMount.split("\n")
        String lastLine = lines.last()
        String[] tokens = lastLine.split("\t")
        return tokens.last()

    }
}
