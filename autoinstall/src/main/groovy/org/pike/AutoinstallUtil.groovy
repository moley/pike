package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.pike.model.host.Host
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
     * gets installer operatingsystem, the next in the hierarchie that is configured as autoinstall os
     * @param autoinstall   autoinstall
     * @param host      host
     * @return installer operatingsystem
     */
    public static Operatingsystem getInstallerOs (final org.pike.model.Autoinstall autoinstall, final Host host) {
        Operatingsystem os = host.operatingsystem
        if (os == null)
            return

        while (true) {
            if (autoinstall.os.contains(os)) {
                return os
                break
            }
            if (os.parent == null)
                break
            else
                os = os.parent
        }
    }

    /**
     * getter
     * @param project  project
     * @return path to lay installers into
     */
    public static File getInstallPath (final Project project) {
        return project.file('build/install')
    }

    /**
     * getter filecollection that contain local libs
     * @param project  project
     * @return collection
     */
    public static FileCollection getLocalLibs (final Project project) {
        FileCollection libs = project.buildscript.configurations.classpath
        libs.each {log.info("getLocalLibs->" + it)}
        return libs
    }

    /**
     * getter pike dir on remote host
     * @param host   host
     * @return pike dir
     */
    public static String getPikeDirRemote (final Host host) {
        log.info("determine pike dir remote " + host.operatingsystem.pikedir)
        String pikeDir = host.operatingsystem.pikedir
        if (pikeDir == null)
            throw new IllegalStateException("PikeDir on host " + host.hostname + " not set")
        return pikeDir
    }
}
