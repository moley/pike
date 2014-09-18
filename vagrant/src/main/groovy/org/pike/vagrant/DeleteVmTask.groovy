package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

/**
 * Created by OleyMa on 18.09.14.
 */
@Slf4j
class DeleteVmTask extends VagrantCliTask {

    @TaskAction
    public void start () {
        commands.add('box')
        commands.add('remove')
        commands.add(host.name)
        executeCli()

        File boxDir = new File (project.buildDir, 'hosts' + File.separator + host.name)
        if (boxDir.exists()) {
            log.info("Delete path $boxDir.absolutePath")
            FileUtils.deleteDirectory(boxDir)
        }
    }
}
