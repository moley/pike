package org.pike.worker

import com.google.common.io.Files
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.worker.java.CertUtils

/**
 * Created by OleyMa on 15.09.14.
 */
class JavaWorkerTest {

    @Test@Ignore //TODO pruefen, haette nicht funktionieren duerfen
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

    @Test
    public void testAddCertificateWithoutArtefact () {

        Project project = ProjectBuilder.builder().build()
        File javaHome = new File (System.getProperty('java.home')).absoluteFile
        File tmpJava = Files.createTempDir()
        org.apache.commons.io.FileUtils.copyDirectory(javaHome, tmpJava)

        String host = 'github.com'

        File binDir = new File (tmpJava, 'bin')
        File keytool = new File (binDir, 'keytool')
        keytool.setExecutable(true, true)

        File certsFile = new File (tmpJava, 'lib' + File.separator + 'security' + File.separator + 'cacerts')
        if (certsFile.exists())
            Assert.assertTrue ("Certsfile could not be removed", certsFile.delete())

        Thread.sleep(200) //TODO check md5 sum changed

        JavaWorker javaworker = TestUtils.createTask(JavaWorker)
        javaworker.project = project
        javaworker.jdkHome(tmpJava.absolutePath)
        javaworker.certificate(host)
        javaworker.install()

        Assert.assertFalse (certsFile.exists())



    }
}
