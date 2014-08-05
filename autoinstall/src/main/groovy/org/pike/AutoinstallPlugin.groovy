package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.pike.cache.CacheManager
import org.pike.holdertasks.ResolveModelTask
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.LinuxProvider
import org.pike.os.WindowsProvider

@Slf4j
class AutoinstallPlugin implements Plugin<Project> {

    public final static String GROUP_AUTOINSTALL = 'Installation'

    @Override
	public void apply(Project project) {
        log.info("Apply plugin ${getClass().getName()}")

        project.plugins.apply(PikePlugin.class)

        project.afterEvaluate {

            DefaultTask prepareInstallerTask = project.tasks.create('prepareInstallers', DefaultTask)
            prepareInstallerTask.group = GROUP_AUTOINSTALL

            File installPathRoot = project.file('build/install')

            getUsedOperatingsystems(project).each {
                Operatingsystem os = it

                ResolveModelTask resolveModelTask = project.tasks.resolveModel

                String osSuffix = os.name
                log.info("Create preparetasks for Operatingsystem " + os.name)

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
                prepareGradle.dependsOn resolveModelTask

                //JDK
                log.info("Configuring jre")

                DownloadJreTask prepareJre = project.tasks.create("prepareInstaller${osSuffix}Jre", DownloadJreTask.class)
                prepareJre.group = GROUP_AUTOINSTALL
                prepareJre.os = os
                prepareJre.to installPathForOs
                prepareJre.simplifyTo 'jre'
                prepareJre.dependsOn resolveModelTask

                //libs
                log.info("Configuring libraries")
                Copy prepareLibs = project.tasks.create("prepareInstaller${osSuffix}Libs", Copy.class)
                prepareLibs.group = GROUP_AUTOINSTALL
                prepareLibs.from project.buildscript.configurations.classpath
                prepareLibs.into(installPathLibs)
                prepareLibs.dependsOn resolveModelTask

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
                createScript.dependsOn resolveModelTask

                //create build.gradle
                log.info("Configuring build.gradle")
                CreateBootstrapScript bootstrapScriptTask = project.tasks.create("prepareInstaller${osSuffix}Bootstrap" , CreateBootstrapScript)
                bootstrapScriptTask.group = GROUP_AUTOINSTALL
                bootstrapScriptTask.toPath = installPathForOs
                bootstrapScriptTask.dependsOn resolveModelTask


                //configurations
                log.info("Configuring configurations")
                Copy prepareConfigurations = project.tasks.create("prepareInstaller${osSuffix}Configurations", Copy)
                prepareConfigurations.group = GROUP_AUTOINSTALL
                prepareConfigurations.from(project.projectDir)
                prepareConfigurations.include '**/*.gradle'
                prepareConfigurations.exclude 'build.gradle'
                prepareConfigurations.exclude 'build/**'
                prepareConfigurations.into(installPathForOs)
                log.info("Configuration: $prepareConfigurations")
                prepareConfigurations.dependsOn resolveModelTask

                //prepare installation container task
                Zip prepareOperatingsystemTask = project.tasks.create("prepareInstaller${osSuffix}", Zip)
                prepareOperatingsystemTask.group = GROUP_AUTOINSTALL
                prepareOperatingsystemTask.description = 'Prepares installer for operatingsystem ${osSuffix}'
                prepareOperatingsystemTask.from(installPathForOs).into('.')
                prepareOperatingsystemTask.baseName = AutoinstallUtil.getInstallerFile(os)
                prepareOperatingsystemTask.destinationDir = installPathRoot

                prepareOperatingsystemTask.dependsOn prepareGradle
                prepareOperatingsystemTask.dependsOn prepareJre
                prepareOperatingsystemTask.dependsOn prepareLibs
                prepareOperatingsystemTask.dependsOn prepareConfigurations
                prepareOperatingsystemTask.dependsOn bootstrapScriptTask
                createScript.dependsOn prepareJre //because we must look for jre path
                createScript.dependsOn prepareGradle //because we must look for gradle path
                prepareOperatingsystemTask.dependsOn createScript
                prepareInstallerTask.dependsOn prepareOperatingsystemTask

            }

            InstallPikeTask installPikeTask = project.tasks.create('installPike', InstallPikeTask)
            installPikeTask.group = GROUP_AUTOINSTALL
            installPikeTask.installPathRoot = installPathRoot

        }

	}

    private Collection<Operatingsystem> getUsedOperatingsystems(Project project) {
        Set<Operatingsystem> operatingsystems = new HashSet<Operatingsystem>()

        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        for (Host nextHost: hosts) {

            Operatingsystem os = nextHost.operatingsystem

            while (true) {
                if (os.createInstaller) {
                    operatingsystems.add(os)
                    break
                }
                if (os.parent == null)
                    break
                else
                    os = os.parent
            }


        }

        return operatingsystems
    }



}
