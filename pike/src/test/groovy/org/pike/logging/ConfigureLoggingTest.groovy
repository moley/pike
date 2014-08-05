package org.pike.logging

import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.pike.test.TestUtils

/**
 * Tests configuration of logging
 */
class ConfigureLoggingTest {

    private File logfile = new File ("mylogging.log")


    @After
    public void after () {
        if (logfile.exists())
            Assert.assertTrue (logfile.delete())
    }


    @Test
    public void useLogConfigurationFile (){
        File tmpPath = Files.createTempDir()

        File logbackXml = TestUtils.projectfile("pike", "src/test/resources/default.logback.xml")
        FileUtils.copyFile(logbackXml, new File (tmpPath, "logback.xml"))

        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").withProjectDir(tmpPath).build()
        project.apply plugin: 'pike'

        Assert.assertTrue (logfile.text.contains("Debug testlog"))
    }
}
