package org.pike.utils

import org.junit.Assert
import org.junit.Test

class ProcessWrapperTest {

    @Test
    public void startProcess () {
        ProcessWrapper processWrapper = new ProcessWrapper()
        File gradlewFile = new File (new File ("").absoluteFile.parentFile, 'gradlew')
        String [] commands = [gradlewFile.absolutePath,'-version']
        ProcessResult processResult = processWrapper.execute(commands)
        Assert.assertTrue ("VersionString Groovy not found (" + processResult.output + ")", processResult.output.contains("Groovy:"))
        Assert.assertTrue ("VersionString Gradle not found (" + processResult.output + ")", processResult.output.contains("Gradle "))
        Assert.assertTrue ("Error not valid", processResult.error.trim().isEmpty())


    }
}
