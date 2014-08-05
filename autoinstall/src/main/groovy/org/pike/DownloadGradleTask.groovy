package org.pike

import org.gradle.api.tasks.TaskAction

/**
 * Created by OleyMa on 01.08.14.
 */
class DownloadGradleTask extends DownloadAndUnzipTask{

    @TaskAction
    public void downloadAndUnzip () {
        from = project.extensions.defaults.pikegradle
        simplifyTo = 'gradle'

        if (from == null)
            throw new IllegalStateException("No pike gradle set, configure one in \ndefault{\n  pikegradle = ...\n}\n")

        super.downloadAndUnzip()
    }
}
