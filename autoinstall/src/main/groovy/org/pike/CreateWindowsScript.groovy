package org.pike

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created by OleyMa on 06.03.14.
 */
@Slf4j
class CreateWindowsScript extends DefaultTask {

    File toDir
    File jreDir
    File gradleDir
    String osSuffix
    File scriptFileCreated

    @TaskAction
    public create () {

        if (gradleDir == null)
            throw new IllegalStateException("Path to gradle must be set")

        if (jreDir == null)
            throw new IllegalStateException("Path to jre must be set")

        if (toDir == null)
            throw new IllegalStateException("toDir must be set")

        File jreBinDir = findBin(jreDir, 'bin/java.exe')
        String localJavaHome = getRelative(jreBinDir.parentFile.parentFile)

        File gradleBinDir = findBin(gradleDir, 'bin/gradle.bat')
        String localGradleGome = getRelative(gradleBinDir.parentFile.parentFile)


        log.info ("Found local gradle home ${localGradleGome}")
        log.info ("Found local jre home ${localJavaHome}")

        scriptFileCreated = new File (toDir, 'configureHost.sh')
        scriptFileCreated.parentFile.mkdirs()
        scriptFileCreated.text =
                """ set GRADLE_HOME=$localGradleGome
set JAVA_HOME=$localJavaHome
set PATH=%GRADLE_HOME%/bin:%JAVA_HOME%/bin:%PATH%

echo "GRADLE_HOME set to %GRADLE_HOME% and added to PATH"
echo "JAVA_HOME set to %JAVA_HOME% and added to PATH"
echo "Startparameter = %1%"

%GRADLE_HOME%\\bin\\gradle %1% --stacktrace"""

        ant.chmod(perm:"0755", file:scriptFileCreated)

    }

    private String getRelative (final File dir) {
        return (dir.absolutePath - toDir.absolutePath).substring(1)
    }

    private File findBin (final File inPath, final String path) {
        log.info("Find path $path in path $inPath.absolutePath")
        if (inPath == null)
            throw new NullPointerException("Parameter inPath must not be null")
        File binFound

        inPath.eachFileRecurse(FileType.FILES) {
            if(it.absolutePath.endsWith(path)) {
                println (it.absolutePath + "->" + it.name + "-> found")
                binFound = it
            }
        }

        if (binFound != null)
            return binFound
        else
            throw new IllegalStateException("Path $path not found in directory $inPath")
    }
}
