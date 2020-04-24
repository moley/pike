package org.pike.utils

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class PikePropertiesTest {

    @Test
    public void saveInNewFile () {
        Project project = ProjectBuilder.builder().build()
        PikeProperties pikeProperties = new PikeProperties(project)
        pikeProperties.setProperty('key', 'value')
        File propertiesFile = PikeProperties.getPropertiesFile(project.projectDir)
        Assert.assertTrue ("Configuration not saved", propertiesFile.text.contains("key=value"))
    }

    @Test
    public void saveInExistingFile () {
        Project project = ProjectBuilder.builder().build()

        File propertiesFile = PikeProperties.getPropertiesFile(project.projectDir)
        propertiesFile.parentFile.mkdirs()
        propertiesFile.text = 'key=value'
        PikeProperties pikeProperties = new PikeProperties(project)
        Assert.assertEquals ("Invalid property", 'value', pikeProperties.getProperty('key'))
    }
}
