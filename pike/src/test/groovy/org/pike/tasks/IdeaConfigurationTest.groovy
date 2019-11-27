package org.pike.tasks

import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test
import org.pike.configuration.Configuration
import org.pike.configuration.Formatter

import java.nio.charset.Charset


class IdeaConfigurationTest {


    @Test(expected = IllegalStateException)
    public void formatterWithoutName () {
        Formatter formatter = new Formatter()
        formatter.tabWidth(7)
        IdeaConfiguration ideaConfiguration = new IdeaConfiguration()
        ideaConfiguration.getFormatterXml(formatter)

    }

    @Test
    public void formatter () {
        Formatter formatter = new Formatter()
        formatter.name("MyFormatter")
        formatter.tabWidth(7)
        formatter.lineSplit(80)
        formatter.spacesForTabs(true)
        IdeaConfiguration ideaConfiguration = new IdeaConfiguration()
        String formatterString = ideaConfiguration.getFormatterXml(formatter)
        println formatterString
        Assert.assertTrue("Header incorrect", formatterString.startsWith("""<component name="ProjectCodeStyleConfiguration">
  <code_scheme name="Project" version="173">
"""))
        Assert.assertTrue("Header incorrect", formatterString.endsWith("""</codeStyleSettings>
  </code_scheme>
</component>"""))

        Assert.assertTrue ("TabChar invalid", formatterString.contains('<option name="USE_TAB_CHARACTER" value="false"/>'))
        Assert.assertTrue ("TabSize invalid", formatterString.contains(' <option name="TAB_SIZE" value="7"/>'))
    }

    @Test
    public void ideaConfiguration () {
        File rootPath = Files.createTempDir()
        File globalConfigPath = new File (rootPath, "global")
        File workspaceConfigPath = new File (rootPath, "workspace")
        File projectConfigPath = new File (rootPath, "project")

        Configuration configuration = new Configuration()
        configuration.encoding 'ISO-8859-15'
        configuration.showLineNumbers Boolean.TRUE
        configuration.showMemory Boolean.TRUE


        Formatter formatter = new Formatter()
        formatter.name("MyFormatter")
        formatter.tabWidth 2
        formatter.spacesForTabs Boolean.TRUE
        configuration.formatter = formatter

        IdeaConfiguration ideaConfiguration = new IdeaConfiguration()
        ideaConfiguration.apply(null, globalConfigPath, workspaceConfigPath, projectConfigPath , configuration, false)
        println rootPath.absolutePath
    }


}
