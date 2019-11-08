package org.pike.configurators.file

import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test

import java.nio.charset.Charset

class XmlConfiguratorTest {

    private XmlConfigurator xmlConfigurator = new XmlConfigurator()

    @Test
    void patchXmlNewFile () {
        String relativepath = 'subdir/idea.editor.xml'
        File globalConfigPath = Files.createTempDir()
        File globalConfigFile = new File (globalConfigPath, relativepath)
        Assert.assertFalse ("Global config file must not exist before test", globalConfigFile.exists())

        xmlConfigurator.configure(null, globalConfigFile, "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", Boolean.FALSE.toString(), false)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertTrue ("Option not contained (" + after + ")", after.contains('<option name="ARE_LINE_NUMBERS_SHOWN" value="false"/>'))

    }

    @Test
    void patchXmlExisting () {
        String relativepath = 'subdir/idea.editor.xml'
        File globalConfigPath = Files.createTempDir()
        InputStream originConfigFile = getClass().getResourceAsStream("/idea.editor.xml")
        File globalConfigFile = new File (globalConfigPath, relativepath)

        FileUtils.copyInputStreamToFile(originConfigFile, globalConfigFile)

        String before = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        Assert.assertFalse("Option not contained (" + before + ")", before.contains('<option name="ARE_LINE_NUMBERS_SHOWN" value="false"/>'))

        xmlConfigurator.configure(null, globalConfigFile, "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", Boolean.FALSE.toString(), false)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertTrue ("Option not contained (" + after + ")", after.contains('<option name="ARE_LINE_NUMBERS_SHOWN" value="false"/>'))
    }

    @Test
    void patchXmlExistingDryRun () {
        String relativepath = 'subdir/idea.editor.xml'
        File globalConfigPath = Files.createTempDir()
        InputStream originConfigFile = getClass().getResourceAsStream("/idea.editor.xml")
        File globalConfigFile = new File (globalConfigPath, relativepath)

        FileUtils.copyInputStreamToFile(originConfigFile, globalConfigFile)

        String before = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        Assert.assertFalse("Option not contained (" + before + ")", before.contains('<option name="ARE_LINE_NUMBERS_SHOWN" value="false"/>'))

        xmlConfigurator.configure(null, globalConfigFile, "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", Boolean.FALSE.toString(), true)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertFalse ("Option contained (" + after + ")", after.contains('<option name="ARE_LINE_NUMBERS_SHOWN" value="false"/>'))
    }

    @Test
    void patchXmlNewOne () {
        String relativepath = 'subdir/idea.editor.xml'
        File globalConfigPath = Files.createTempDir()
        InputStream originConfigFile = getClass().getResourceAsStream("/idea.editor.xml")
        File globalConfigFile = new File (globalConfigPath, relativepath)

        FileUtils.copyInputStreamToFile(originConfigFile, globalConfigFile)

        String before = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        Assert.assertFalse("Option not contained (" + before + ")", before.contains('<option name="BLA" value="2"/>'))

        xmlConfigurator.configure(null, globalConfigFile, "/application/component[@name='EditorSettings']/option[@name='BLA']", "2", false)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertTrue ("Option not contained (" + after + ")", after.contains('<option name="BLA" value="2"/>'))
    }

    @Test
    void attributeInKey () {
        String relativepath = 'encodings.xml'
        File globalConfigPath = Files.createTempDir()
        InputStream originConfigFile = getClass().getResourceAsStream("/encodings.xml")
        File globalConfigFile = new File (globalConfigPath, relativepath)
        FileUtils.copyInputStreamToFile(originConfigFile, globalConfigFile)
        xmlConfigurator.configure(null, globalConfigFile, "/project/component[@name='Encoding']/file[@url='PROJECT']->charset", "UTF-16", false)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertTrue ("Project encoding (" + after + ")", after.contains('file charset="UTF-16"'))
    }

    @Test(expected = IllegalStateException)
    void moreTokens () {
        File globalConfigPath = Files.createTempDir()
        xmlConfigurator.configure(null, globalConfigPath, "/project/component[@name='Encoding']/file[@url='PROJECT']->charset->bla", "UTF-16", false)

    }
}
