package org.pike.tasks

import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test

import java.nio.charset.Charset


class IdeaConfigurationTest {

    @Test
    void patchXmlNewFile () {
        String relativepath = 'subdir/idea.editor.xml'
        File globalConfigPath = Files.createTempDir()
        File globalConfigFile = new File (globalConfigPath, relativepath)
        Assert.assertFalse ("Global config file must not exist before test", globalConfigFile.exists())

        IdeaConfiguration ideaConfiguration = new IdeaConfiguration(null, globalConfigPath, [])
        ideaConfiguration.global(relativepath, "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", Boolean.FALSE.toString(), false)

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

        IdeaConfiguration ideaConfiguration = new IdeaConfiguration(null, globalConfigPath, [])
        ideaConfiguration.global(relativepath, "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", Boolean.FALSE.toString(), false)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertTrue ("Option not contained (" + after + ")", after.contains('<option name="ARE_LINE_NUMBERS_SHOWN" value="false"/>'))
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

        IdeaConfiguration ideaConfiguration = new IdeaConfiguration(null, globalConfigPath, [])
        ideaConfiguration.global(relativepath, "/application/component[@name='EditorSettings']/option[@name='BLA']", "2", false)

        String after = FileUtils.readFileToString(globalConfigFile, Charset.defaultCharset())
        System.out.println (after)
        Assert.assertTrue ("Option not contained (" + after + ")", after.contains('<option name="BLA" value="2"/>'))
    }
}
