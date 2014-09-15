package org.pike.worker

import com.google.common.io.Files
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.worker.java.CertUtils

/**
 * Created by OleyMa on 15.09.14.
 */
class JavaWorkerTest {

    @Test
    public void testAddCertificate () {

        Project project = ProjectBuilder.builder().build()
        File javaHome = new File (System.getProperty('java.home')).absoluteFile
        File tmpJava = Files.createTempDir()
        org.apache.commons.io.FileUtils.copyDirectory(javaHome, tmpJava)

        String host = 'github.com'

        File binDir = new File (tmpJava, 'bin')
        File keytool = new File (binDir, 'keytool')
        keytool.setExecutable(true, true)

        long lastModified = CertUtils.getCertsFile(tmpJava).lastModified()

        Thread.sleep(200)

        JavaWorker javaworker = new JavaWorker()
        javaworker.project = project
        javaworker.jdkHome(tmpJava.absolutePath)
        javaworker.certificate(host)
        javaworker.install()

        Assert.assertNotSame (CertUtils.getCertsFile(tmpJava).lastModified(), lastModified)

    }
}
