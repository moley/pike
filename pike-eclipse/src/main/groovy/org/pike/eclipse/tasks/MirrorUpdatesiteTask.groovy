package org.pike.eclipse.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction
import org.pike.configuration.OperatingSystem
import org.pike.eclipse.utils.CreateUpdatesiteMetadata
import org.pike.installers.ToolInstaller
import org.pike.tasks.ForcableTask
import org.pike.utils.ProgressLoggerWrapper

class MirrorUpdatesiteTask extends ForcableTask {

    String url

    String localname

    String version

    File toPath

    ToolInstaller toolInstaller

    EclipsePaths eclipsePaths = new EclipsePaths()

    @TaskAction
    public void mirror() {

        File updatesiteToDir = new File (toPath, localname)
        if (force) {
            if (updatesiteToDir.exists())
                FileUtils.deleteDirectory(updatesiteToDir)
        }

        if (updatesiteToDir.exists())
            logger.lifecycle("Path " + updatesiteToDir.absolutePath + " already exists. Clean the path to reimport updatesite " + url)
        else {
            toolInstaller.force = force
            project.logger.lifecycle("Mirroring " + url + " with name " + localname + " to " + toPath.absolutePath + "(force: " + force + ")")

            String progressLoggerPrefix = "Mirroring updatesite " + url + " "
            ProgressLoggerWrapper progressLoggerWrapper = new ProgressLoggerWrapper(project, progressLoggerPrefix)
            progressLoggerWrapper.progress("Installing tooling...")

            File mirrorInstallDir = toolInstaller.install()
            File mirrorWorkingDir = eclipsePaths.getWorkingDir(mirrorInstallDir)
            File mirroredUpdatesitesBaseDir = new File(mirrorWorkingDir, 'updatesites')
            File mirroredUpdatesiteDir = new File (mirroredUpdatesitesBaseDir, localname)

            File mirrorConfigDir = eclipsePaths.getConfigurationDir(mirrorInstallDir)

            project.logger.lifecycle("Using installDir " + mirrorInstallDir.absolutePath)
            project.logger.lifecycle("Using workingDir " + mirrorWorkingDir.absolutePath)
            project.logger.lifecycle("Using configDir " + mirrorConfigDir.absolutePath)

            File proxySettings = new File(mirrorConfigDir, '.settings/org.eclipse.core.net.prefs')
            if (System.getProperty("http.proxyHost") != null) {
                logger.lifecycle("Configure proxy in file " + proxySettings.absolutePath)
                String httpProxyHost = System.getProperty("http.proxyHost")
                String httpProxyPort = System.getProperty("http.proxyHost")
                String httpsProxyHost = System.getProperty("https.proxyHost")
                String httpsProxyPort = System.getProperty("https.proxyPort")
                String nonProxy = System.getProperty("http.nonProxyHosts")
                logger.lifecycle("Configuring proxy in file ${proxySettings.absolutePath} ")
                proxySettings.parentFile.mkdirs()
                proxySettings.text = """eclipse.preferences.version=1
nonProxiedHosts=${nonProxy}
org.eclipse.core.net.hasMigrated=true
proxiesEnabled=true
proxyData/HTTP/hasAuth=false
proxyData/HTTP/host=${httpProxyHost}
proxyData/HTTP/port=${httpProxyPort}
proxyData/HTTPS/hasAuth=false
proxyData/HTTPS/host=${httpsProxyHost}
proxyData/HTTPS/port=${httpsProxyPort}
systemProxiesEnabled=false"""
            } else {
                logger.lifecycle("No proxy configuration necessary")
                if (proxySettings.exists()) {
                    logger.lifecycle("Remove proxy configuration in ${proxySettings.absolutePath}")
                    proxySettings.delete()
                }

            }

            //Mirror the metadata
            progressLoggerWrapper.progress("Mirroring the metadata of ${url}...")
            project.exec({
                workingDir mirrorWorkingDir
                commandLine "./eclipse",
                        "--nosplash",
                        "-application", "org.eclipse.equinox.p2.metadata.repository.mirrorApplication",
                        "-source",
                        url,
                        "-destination",
                        "updatesites/$localname",
                        "--debug"

            })

            //Mirror the artifacts
            progressLoggerWrapper.progress("Mirroring the artifacts of ${url}...")
            project.exec({
                workingDir mirrorWorkingDir
                commandLine "./eclipse",
                        "--nosplash",
                        "-application", "org.eclipse.equinox.p2.artifact.repository.mirrorApplication",
                        "-source",
                        url,
                        "-destination",
                        "updatesites/$localname",
                        "--debug"

            })

            //Move the mirrored artifacts
            progressLoggerWrapper.progress("Moving data to distribution area")
            if (!mirroredUpdatesitesBaseDir.exists())
                throw new IllegalStateException("Updatesites outputdir does not exist")



            FileUtils.moveDirectory(mirroredUpdatesiteDir, updatesiteToDir)
            if (!mirroredUpdatesitesBaseDir.delete()) {
                throw new IllegalStateException("Could not remove dir $mirroredUpdatesitesBaseDir.absolutePath")
            }

            progressLoggerWrapper.end()
        }

        CreateUpdatesiteMetadata createUpdatesiteMetadata = new CreateUpdatesiteMetadata()
        createUpdatesiteMetadata.createMetadata(toPath, version)



    }


}
