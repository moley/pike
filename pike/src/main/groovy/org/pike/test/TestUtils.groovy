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


    public static String CURRENT_DIST = 'gradle-2.0-all.zip'

    /**
     * gradle connector from tooling api
     * @return
     */
    public static GradleConnector getGradleConnector () {
        return GradleConnector.newConnector().useGradleVersion("2.0") //TODO with gradle wrapper
    }

    /**
     * gets testproject path with given name
     * @param testprojectname name of testproject
     * @return testproject path
     */
    public static File getTestproject (final String testprojectname) {
        File testprojects = new File ('testprojects')
        if (! testprojects.exists())
            testprojects = new File ('../testprojects')

        File concreteTestProject = new File (testprojects, testprojectname)
        if (! concreteTestProject.exists())
            throw new IllegalStateException("Could not find testproject $testprojectname")
        return concreteTestProject
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




}
