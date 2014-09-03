package org.pike.test

import org.gradle.api.Project
import org.gradle.tooling.GradleConnector
import org.pike.holdertasks.DeriveTasksTask
import org.pike.holdertasks.ResolveModelTask

import java.nio.file.Files

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class TestUtils {

    private final static String OS = System.getProperty("os.name")

    private static File gradleHome = new File ("/Users/OleyMa/programs/gradle-2.0")

    public static String CURRENT_DIST = 'gradle-2.0-all.zip'

    /**
     * gradle connector from tooling api
     * @return
     */
    public static GradleConnector getGradleConnector () {
        return GradleConnector.newConnector().useInstallation(gradleHome) //TODO with gradle wrapper
    }


    public static void prepareModel (final Project project) {

        project.evaluate()

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()

        DeriveTasksTask deriveTasksTask = project.tasks.deriveTasks
        deriveTasksTask.deriveTasks()

    }

    public static File getTmpFile () {
        return Files.createTempFile('tmp', 'file').toFile()
    }

    public static File projectfile (final String project, final String relativeFile) {

        File projectDir = new File (project)
        if (projectDir.exists())
            return new File (projectDir, relativeFile)
        else
            return new File (relativeFile)
    }




    /**
     * returns, if I'm a windows-system
     * @return true if windows, else false
     */
    public static boolean isWindows() {
        return OS.toUpperCase().indexOf("WIN") >= 0
    }

    /**
     * Gets toPath to a gradleplugins-module
     *
     * @param project name of the project, e.g. 'marvin'
     * @return toPath
     */
    public static File getProjectPath(final String project) {

        File pathAsPath = new File(project)
        if (!pathAsPath.exists()) {
            println("Path " + pathAsPath.absolutePath + " doesn't exist, look up in parent")
            pathAsPath = new File(new File("").absoluteFile.parentFile, project)
        }
        else
            println("Using projectpath " + pathAsPath.absolutePath)

        if (!pathAsPath.exists())
            throw new IllegalStateException("Path " + pathAsPath.absolutePath + " not found in project.")

        return pathAsPath
    }


}
