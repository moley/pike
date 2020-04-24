package org.pike.utils

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

import java.beans.PropertyChangeEvent


class ProgressLoggerWrapperTest {

    @Test
    public void progress () {
        Project project = ProjectBuilder.builder().build()
        ProgressLoggerWrapper progressLoggerWrapper = new ProgressLoggerWrapper(project, "description")
        progressLoggerWrapper.progress("10%")
        progressLoggerWrapper.progress("10%")
        Assert.assertNotNull (progressLoggerWrapper.description)
        progressLoggerWrapper.propertyChange(new PropertyChangeEvent("", "property", "oldValue", "newValue"))
        progressLoggerWrapper.end()

    }
}
