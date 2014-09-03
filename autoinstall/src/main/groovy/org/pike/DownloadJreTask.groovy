package org.pike

import org.gradle.api.tasks.TaskAction

/**
 * Created by OleyMa on 01.08.14.
 */
class DownloadJreTask extends DownloadAndUnzipTask{

    BitEnvironment bitEnvironment

    @TaskAction
    public void downloadAndUnzip () {

        simplifyTo = 'jre'

        from = getPikeJreFrom()
        if (from == null)
            throw new IllegalStateException("pikeJre is not set for operatingsystem $os.name.")

        super.downloadAndUnzip()
    }

    String getPikeJreFrom () {
        if (bitEnvironment.equals(BitEnvironment._64))
            return os.pikejre64
        else
            return os.pikejre32
    }
}