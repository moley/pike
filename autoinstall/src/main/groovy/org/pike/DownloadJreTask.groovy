package org.pike

import org.gradle.api.tasks.TaskAction

/**
 * Created by OleyMa on 01.08.14.
 */
class DownloadJreTask extends DownloadAndUnzipTask{

    @TaskAction
    public void downloadAndUnzip () {
        simplifyTo = 'jre'
        from = os.pikejre
        if (from == null)
            throw new IllegalStateException("pikeJre is not set for operatingsystem $os.name.")

        super.downloadAndUnzip()
    }
}