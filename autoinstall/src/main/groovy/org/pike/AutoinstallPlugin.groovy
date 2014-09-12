package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip
import org.pike.holdertasks.ResolveModelTask
import org.pike.model.Autoinstall
import org.pike.model.defaults.Defaults
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.LinuxProvider
import org.pike.os.WindowsProvider

import java.nio.file.Files

@Slf4j
class AutoinstallPlugin implements Plugin<Project> {

    public final static String GROUP_AUTOINSTALL = 'Installation'

    @Override
	public void apply(Project project) {
        log.info("Apply plugin ${getClass().getName()}")

        project.plugins.apply(PikePlugin.class)

        Autoinstall autoinstall = project.extensions.create("autoinstall", Autoinstall, project)


        project.afterEvaluate {

            DefaultTask prepareInstallerTask = project.tasks.create('prepareInstallers', DefaultTask)
            prepareInstallerTask.group = GROUP_AUTOINSTALL
            prepareInstallerTask.description = 'Prepare all installers for configured operatingsystems'

            File installPathRoot = AutoinstallUtil.getInstallPath(project)

            getUsedOperatingsystems(project, autoinstall).each {
                AutoinstallEntry entry = it
                Operatingsystem os = entry.os
                BitEnvironment bitEnvironment = entry.bitEnvironment

                ResolveModelTask resolveModelTask = project.tasks.resolveModel

                String osName = os.name
                String osSuffix = os.name
                log.info("Create preparetasks for Operatingsystem " + os.name)
                Class clazz = os.provider instanceof  LinuxProvider ? Tar : Zip
                AbstractArchiveTask prepareOperatingsystemTask = project.tasks.create("prepareInstaller${osSuffix}", clazz)


                log.info("Create preparetask gradle for operatingsystem " + os.name)
                File installPathForOs = new File (installPathRoot, os.name)
                File installPathLibs = new File (installPathForOs, 'libs')
                File simplifiedJre = new File (installPathForOs, 'jre')
                File simplifiedGradle = new File (installPathForOs, 'gradle')

                //Gradle
                log.info("Configuring gradle ")
                DownloadGradleTask prepareGradle = project.tasks.create("prepareInstaller${osSuffix}Gradle", DownloadGradleTask.class)
                prepareGradle.group = GROUP_AUTOINSTALL
                prepareGradle.os = os
                prepareGradle.to installPathForOs
                prepareGradle.simplifyTo
                prepareGradle.description = "Prepare gradle for installer of operatingsystem $osName"
                prepareGradle.dependsOn resolveModelTask
                prepareOperatingsystemTask.dependsOn prepareGradle

                //JRE
                log.info("Configuring jre")

                DownloadJreTask prepareJre = project.tasks.create("prepareInstaller${osSuffix}Jre", DownloadJreTask.class)
                prepareJre.group = GROUP_AUTOINSTALL
                prepareJre.os = os
                prepareJre.bitEnvironment = bitEnvironment
                prepareJre.to installPathForOs
                prepareJre.simplifyTo 'jre'
                prepareJre.description = "Prepare jre for installer of operatingsystem $osName"
                prepareJre.dependsOn resolveModelTask
                prepareOperatingsystemTask.dependsOn prepareJre

                //libs
                log.info("Configuring libraries")
                Copy prepareLibs = project.tasks.create("prepareInstaller${osSuffix}Libs", Copy.class)
                prepareLibs.group = GROUP_AUTOINSTALL
                prepareLibs.from AutoinstallUtil.getLocalLibs(project)
                prepareLibs.into(installPathLibs)
                prepareLibs.description = "Prepare libraries for installer of operatingsystem $osName"
                prepareLibs.dependsOn resolveModelTask
                prepareOperatingsystemTask.dependsOn prepareLibs


                //scripts
                log.info("Configuring startscripts")
                Class startScriptTask = null

                if (os.provider instanceof LinuxProvider)
                    startScriptTask = CreateLinuxScript
                else if (os.provider instanceof WindowsProvider)
                    startScriptTask = CreateWindowsScript
                else log.error("Invalid os provider")
                DefaultTask createScript = project.tasks.create("prepareInstaller${osSuffix}Startscript", startScriptTask)
                createScript.group = GROUP_AUTOINSTALL
                createScript.toDir = installPathForOs
                createScript.jreDir = simplifiedJre
                createScript.gradleDir = simplifiedGradle
                createScript.osSuffix = osSuffix
                createScript.description = "Prepare startscript for installer of operatingsystem $osName"
                createScript.dependsOn resolveModelTask
                prepareOperatingsystemTask.dependsOn createScript


                log.info("Configuring build.gradle")
                CreateBootstrapScript bootstrapScriptTask = project.tasks.create("prepareInstaller${osSuffix}Bootstrap" , CreateBootstrapScript)
                bootstrapScriptTask.group = GROUP_AUTOINSTALL
                bootstrapScriptTask.toPath = installPathForOs
                bootstrapScriptTask.dependsOn resolveModelTask
                bootstrapScriptTask.description = "Prepare bootstrap script for installer of operatingsystem $osName"
                prepareOperatingsystemTask.dependsOn bootstrapScriptTask

                log.info("Configuring configurations")
                Copy prepareConfigurations = project.tasks.create("prepareInstaller${osSuffix}Configurations", Copy)
                prepareConfigurations.group = GROUP_AUTOINSTALL
                prepareConfigurations.from(project.projectDir)
                prepareConfigurations.include '**/*.gradle'
                prepareConfigurations.exclude 'build.gradle'
                prepareConfigurations.exclude 'build/**'
                prepareConfigurations.includeEmptyDirs false
                prepareConfigurations.into(installPathForOs)
                log.info("Configuration: $prepareConfigurations")
                prepareConfigurations.dependsOn resolveModelTask
                prepareConfigurations.description = "Prepare configurations for installer of operatingsystem $osName"
                prepareOperatingsystemTask.dependsOn prepareConfigurations

                //prepare installation container task
                prepareOperatingsystemTask.group = GROUP_AUTOINSTALL
                prepareOperatingsystemTask.description = "Prepare installer for operatingsystem $osName"
                prepareOperatingsystemTask.from(installPathForOs).into('.')
                prepareOperatingsystemTask.baseName = AutoinstallUtil.getInstallerFile(os)
                prepareOperatingsystemTask.extension = 'installer'
                prepareOperatingsystemTask.destinationDir = installPathRoot

                //Additional dependencies
                createScript.dependsOn prepareJre //because we must look for jre path
                createScript.dependsOn prepareGradle //because we must look for gradle path
                prepareInstallerTask.dependsOn prepareOperatingsystemTask

            }

            InstallPikeTask installPikeTask = project.tasks.create('installPike', InstallPikeTask)
            installPikeTask.group = GROUP_AUTOINSTALL
            installPikeTask.description = "Install pike on parameterized or default host"

            StartRemoteBuildTask startRemoteBuildTask = project.tasks.create('provision', StartRemoteBuildTask)
            startRemoteBuildTask.group = GROUP_AUTOINSTALL
            startRemoteBuildTask.dependsOn project.tasks.resolveModel
            startRemoteBuildTask.description = "Start provisioning of parameterized or default host"

        }

	}

    private Collection<AutoinstallEntry> getUsedOperatingsystems(Project project, Autoinstall autoinstall) {
        Set<AutoinstallEntry> operatingsystems = new HashSet<AutoinstallEntry>()

        Set<Operatingsystem> usedOs = new HashSet<Operatingsystem>()

        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        for (Host nextHost: hosts) {
            Operatingsystem os = AutoinstallUtil.getInstallerOs(autoinstall, nextHost)
            if (os != null) {
                if (! usedOs.contains(os)) {
                    usedOs.add(os)
                    operatingsystems.add(new AutoinstallEntry(os, nextHost.bitEnvironment))
                }
            }
        }

        return operatingsystems
    }



}
