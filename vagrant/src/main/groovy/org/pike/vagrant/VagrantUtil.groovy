package org.pike.vagrant

import org.gradle.api.Project
import org.pike.model.host.Host

/**
 * Created by OleyMa on 07.08.14.
 */
class VagrantUtil {

    public static File getWorkingDir (final Project project, final Host host) {
        File buildDir = new File (project.buildDir, 'hosts')
        File hostDir = new File (buildDir, host.name)
        return hostDir
    }


}
