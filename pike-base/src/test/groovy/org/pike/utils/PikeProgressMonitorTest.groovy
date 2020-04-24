package org.pike.utils

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class PikeProgressMonitorTest {

    @Test
    void progress () {
        Project project = ProjectBuilder.builder().build()
        ProgressLoggerWrapper progressLoggerWrapper = new ProgressLoggerWrapper(project, "Description")
        PikeProgressMonitor pikeProgressMonitor = new PikeProgressMonitor(progressLoggerWrapper)
        pikeProgressMonitor.start(5)
        pikeProgressMonitor.beginTask("Task", 5)
        pikeProgressMonitor.update(5)
        pikeProgressMonitor.endTask()
        Assert.assertFalse (pikeProgressMonitor.isCancelled())

    }
}
