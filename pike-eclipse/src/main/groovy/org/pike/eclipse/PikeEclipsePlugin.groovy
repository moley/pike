package org.pike.eclipse

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.pike.configuration.OperatingSystem
import org.pike.eclipse.configuration.OsToken
import org.pike.eclipse.configuration.PikeEclipseExtension
import org.pike.eclipse.configuration.UpdatesiteItem
import org.pike.eclipse.tasks.MirrorUpdatesiteTask
import org.pike.installers.ToolInstaller
import org.pike.installers.ToolInstallerBuilder
import org.pike.tasks.DownloadTask
import org.pike.utils.StringUtils

public class PikeEclipsePlugin implements Plugin<Project> {

    public final static String PIKE_ECLIPSE_GROUP = 'Pike Eclipse'

    private StringUtils stringUtils = new StringUtils()


    @Override
    void apply(Project project) {

        project.plugins.apply(BasePlugin) //for clean task

        PikeEclipseExtension pikeExtension = project.extensions.create(PikeEclipseExtension.NAME, PikeEclipseExtension, project)

        project.afterEvaluate {

            File toolingDir = project.file('build/pike/tooling')

            File versionInstallationDir = new File(pikeExtension.installationDir, pikeExtension.version)
            File distributionInstallationDir = new File(versionInstallationDir, 'distributions')
            File updatesiteInstallationDir = new File(versionInstallationDir, 'updatesites')
            String version = pikeExtension.version
            String distributionType = pikeExtension.distributionType

            DefaultTask mirrorEclipseTask = project.tasks.register("mirrorEclipse").get()
            mirrorEclipseTask.description "Download distributions and mirror updatesites of version $version"
            mirrorEclipseTask.group = PIKE_ECLIPSE_GROUP


            DefaultTask mirrorUpdatesitesTask = project.tasks.register('mirrorUpdatesites').get()
            mirrorUpdatesitesTask.description "Mirror all updatesites of eclipse version $version"
            mirrorUpdatesitesTask.group = PIKE_ECLIPSE_GROUP

            DefaultTask downloadDistributionsTask = project.tasks.register('downloadDistributions').get()
            downloadDistributionsTask.description "Download distributions of version $version"
            downloadDistributionsTask.group = PIKE_ECLIPSE_GROUP

            mirrorEclipseTask.dependsOn downloadDistributionsTask
            mirrorEclipseTask.dependsOn mirrorUpdatesitesTask

            ToolInstallerBuilder toolInstallerBuilder = new ToolInstallerBuilder(project, 'eclipse', pikeExtension.version).force()

            for (OsToken next : pikeExtension.getOsTokens()) {
                DownloadTask downloadTask = project.tasks.register("downloadDistribution${next.operatingSystem.displayName}", DownloadTask).get()
                downloadTask.group = PIKE_ECLIPSE_GROUP
                downloadTask.description = "Download distribution for operating system ${next.operatingSystem.displayName.toLowerCase()}"
                downloadTask.url = "http://ftp.fau.de/eclipse/technology/epp/downloads/release/${version}/R/eclipse-${distributionType}-$version-R-$next.osToken"
                downloadTask.toDir = distributionInstallationDir
                downloadTask.filename = "eclipse-$version-$next.osToken"
                downloadDistributionsTask.dependsOn downloadTask
                toolInstallerBuilder.platform(next.operatingSystem).url(downloadTask.url).installationPath(toolingDir)
            }

            ToolInstaller toolInstaller = toolInstallerBuilder.get(OperatingSystem.getCurrent())

            for (UpdatesiteItem next: pikeExtension.updateSites) {
                MirrorUpdatesiteTask mirrorUpdatesiteTask = project.tasks.register("mirrorUpdatesite${stringUtils.getFirstUpper(next.name)}", MirrorUpdatesiteTask).get()
                mirrorUpdatesiteTask.group = PIKE_ECLIPSE_GROUP
                mirrorUpdatesiteTask.description = "Mirror updatesite " + next.url
                mirrorUpdatesiteTask.toolInstaller = toolInstaller
                mirrorUpdatesiteTask.url = next.url
                mirrorUpdatesiteTask.version = version
                mirrorUpdatesiteTask.localname = next.name
                mirrorUpdatesiteTask.toPath = updatesiteInstallationDir
                mirrorUpdatesitesTask.dependsOn mirrorUpdatesiteTask
            }


        }


    }
}
