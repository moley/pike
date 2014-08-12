package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider
import org.pike.os.LinuxProvider

/**
 * Created by OleyMa on 01.08.14.
 */
@Slf4j
class AutoinstallUtil {

    /**
     * gets installer file name
     * @param os  operatingsystem
     * @return name
     */
    public static String getInstallerFile (Operatingsystem os) {
        return "pikeinstaller-${os.name}"
    }

    /**
     * getter
     * @param project  project
     * @return path to lay installers into
     */
    public static File getInstallPath (final Project project) {
        return project.file('build/install')
    }

    public static FileCollection getLocalLibs (final Project project) {
        FileCollection libs = project.buildscript.configurations.classpath
        libs.each {log.info("getLocalLibs->" + it)}
        return libs
    }
}
