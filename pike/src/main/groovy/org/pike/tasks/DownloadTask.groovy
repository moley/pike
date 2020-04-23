package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.installers.Download

class DownloadTask extends DefaultTask{

    String url
    File toDir
    String filename

    @TaskAction
    public void download () {
        Download download = new Download()
        download.project = project
        download.source = url
        download.toDir = toDir
        download.downloadedFile = new File (toDir, filename)
        download.executeDownload()
    }
}
