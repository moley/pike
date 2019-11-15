package org.pike.configurators.file

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test

import java.nio.charset.Charset

class FileContentConfiguratorTest {


    @Test(expected = IllegalArgumentException)
    public void keyNotNull () {
        final File newFile = new File (Files.createTempDir(), getClass().simpleName)
        FileContentConfigurator fileContentConfigurator = new FileContentConfigurator()
        fileContentConfigurator.configure(null, newFile, "bla", "blub", true)

    }

    @Test
    public void dryRun () {
        final File newFile = new File (Files.createTempDir(), getClass().simpleName)
        final String CONTENT = """Hello
this is an example"""

        FileContentConfigurator fileContentConfigurator = new FileContentConfigurator()
        fileContentConfigurator.configure(null, newFile, null, CONTENT, true)

        Assert.assertFalse ("DryRun must not create a file", newFile.exists())
    }
    @Test
    public void configure () {

        final File newFile = new File (Files.createTempDir(), getClass().simpleName)
        final String CONTENT = """Hello
this is an example"""

        FileContentConfigurator fileContentConfigurator = new FileContentConfigurator()
        fileContentConfigurator.configure(null, newFile, null, CONTENT, false)

        List<String> fileContent = Files.readLines(newFile, Charset.defaultCharset())
        Assert.assertEquals ("Content1 invalid", "Hello", fileContent.get(0))
        Assert.assertEquals ("Content2 invalid", "this is an example", fileContent.get(1))
    }
}
