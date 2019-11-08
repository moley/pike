package org.pike.configurators.file

import org.junit.Assert
import org.junit.Test

import java.nio.charset.Charset
import java.nio.file.Files

class PropertiesConfiguratorTest {

    PropertiesConfigurator propertiesConfigurator = new PropertiesConfigurator()

    @Test
    public void patchNewFile () {

        File propertiesFile = Files.createTempFile(getClass().simpleName, 'patchNewFile').toFile()

        propertiesConfigurator.configure(null, propertiesFile, "key", "value", false)

        Assert.assertEquals ("value", key(propertiesFile, 'key') )

    }

    private String key (final File file, final String key) {
        Properties properties = new Properties()
        properties.load(new FileReader(file))
        return properties.get(key)
    }

    @Test
    public void patchExistingFile () {

        File propertiesFile = Files.createTempFile(getClass().simpleName, 'patchExistingFile').toFile()
        org.apache.commons.io.FileUtils.write(propertiesFile, "key=oldValue", Charset.defaultCharset())

        propertiesConfigurator.configure(null, propertiesFile, 'key', 'value', false)

        Assert.assertEquals ("value", key(propertiesFile, 'key') )

    }

    @Test
    public void patchExistingFileDryRun () {

        File propertiesFile = Files.createTempFile(getClass().simpleName, 'patchExistingFile').toFile()
        org.apache.commons.io.FileUtils.write(propertiesFile, "key=oldValue", Charset.defaultCharset())

        propertiesConfigurator.configure(null, propertiesFile, 'key', 'value', true)

        Assert.assertEquals ("oldValue", key(propertiesFile, 'key') )

    }
}
