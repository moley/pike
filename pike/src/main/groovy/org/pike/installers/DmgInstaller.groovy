package org.pike.installers

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper


class DmgInstaller implements Installer {

    Project project

    ProcessWrapper processWrapper = new ProcessWrapper()

    private File getAppDir(final File volumeDir) {
        for (File next : volumeDir.listFiles()) {
            if (next.name.endsWith(".app"))
                return next
        }

        throw new IllegalStateException("No path found  suffixed with app in " + volumeDir.absolutePath)
    }

    @Override
    void install(final File installationDir, File downloadedFile) {

        project.logger.lifecycle("Installing " + downloadedFile.name + " to " + installationDir.absolutePath)

        //Mount
        String[] mountCommand = ['hdiutil', 'mount', downloadedFile.absolutePath]
        ProcessResult resultMount = processWrapper.execute(mountCommand)
        if (resultMount.resultCode != 0)
            throw new IllegalStateException("Could not mount the dmg file ${downloadedFile.absolutePath}. Command $mountCommand results in error: " + resultMount.error)

        //Copy content
        String volumePath = getVolumePath(resultMount.output)
        String volumeName = volumePath.split("/").last()
        if (!installationDir.name.equals(volumeName))
            throw new IllegalStateException("Installation dir wrong " + installationDir.absolutePath + "-" + volumeName)

        if (!installationDir.exists()) {
            project.logger.lifecycle("Copy " + volumePath + " to " + installationDir.absolutePath)
            File volume = new File(volumePath)
            File app = getAppDir(volume)
            FileUtils.copyDirectory(app, installationDir)
        }
        else
            project.logger.lifecycle("Installation dir " + installationDir.absolutePath + " already exists")

        //Unmount
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
