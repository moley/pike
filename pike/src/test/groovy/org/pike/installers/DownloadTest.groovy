package org.pike.installers

import com.google.common.io.Files
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class DownloadTest {

    @Test
    public void executeDownload () {

        Project project = ProjectBuilder.builder().build()

        File toDir = Files.createTempDir()
        File cacheDir = Files.createTempDir()


        Download download = new Download()
        download.source = getClass().getResource("/encodings.xml").toString()
        download.project = project
        download.cacheDir = cacheDir
        download.toDir = toDir
        download.executeDownload()
        Assert.assertTrue ("No bytes are downloaded the first time", download.processedBytes > 0)

        download.executeDownload()
        Assert.assertEquals ("The second time no processed bytes are expected", 0, download.processedBytes)



    }
}
