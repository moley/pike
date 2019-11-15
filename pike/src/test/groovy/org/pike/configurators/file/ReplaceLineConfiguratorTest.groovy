package org.pike.configurators.file

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test


class ReplaceLineConfiguratorTest {
    ReplaceLineConfigurator configurator = new ReplaceLineConfigurator()

    @Test(expected = IllegalStateException)
    public void nonExisting () {

        File tmpDir = Files.createTempDir()
        configurator.configure(null, new File (tmpDir, 'nonExisting'), "hello", "world", false)

    }

    @Test
    public void existingContaining () {
        File tmpDir = Files.createTempDir()
        File configFile = new File (tmpDir, "configFile.txt")
        configFile.text = 'hellozeuchs\nhellozeuchs\nthirdline\n'
        configurator.configure(null,configFile, "hello", "world", false)
        Assert.assertEquals ("Content invalid", 'helloworld\nhelloworld\nthirdline\n', configFile.text)
    }

    @Test(expected = IllegalStateException)
    public void existingNotContaining () {
        File tmpDir = Files.createTempDir()
        File configFile = new File (tmpDir, "configFile.txt")
        configFile.text = 'bla\nbla\thirdline'
        configurator.configure(null,configFile, "hello", "world", false)
    }

    @Test
    public void dryRun () {
        File tmpDir = Files.createTempDir()
        File configFile = new File (tmpDir, "configFile.txt")
        configFile.text = 'hellozeuchs\nhellozeuchs\nthirdline\n'
        configurator.configure(null,configFile, "hello", "world", true)
        Assert.assertEquals ("Content invalid", 'hellozeuchs\nhellozeuchs\nthirdline\n', configFile.text)

    }
}
