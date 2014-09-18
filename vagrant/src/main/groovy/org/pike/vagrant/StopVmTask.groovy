package org.pike.vagrant

import org.gradle.api.tasks.TaskAction

/**
 * Created by OleyMa on 05.08.14.
 */
class StopVmTask extends VagrantCliTask {

    @TaskAction
    public void stop () {
        commands.add('halt')
        executeCli()
    }
}