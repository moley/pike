package org.pike.tasks

import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test
import org.pike.configuration.Configuration

import java.nio.charset.Charset


class IdeaConfigurationTest {

    @Test
    public void ideaConfiguration () {
        File rootPath = Files.createTempDir()
        File workspaceConfigPath = new File (rootPath, "workspace")
        File projectConfigPath = new File (rootPath, "project")

        Configuration configuration = new Configuration()
        configuration.encoding 'ISO-8859-15'
        configuration.showLineNumbers Boolean.TRUE
        configuration.showMemory Boolean.TRUE
        configuration.tabWidth 2
        configuration.spacesForTabs Boolean.TRUE

        IdeaConfiguration ideaConfiguration = new IdeaConfiguration(null, workspaceConfigPath, Arrays.asList(projectConfigPath) )
        ideaConfiguration.apply(configuration, false)
        println rootPath.absolutePath
    }


}
