package org.pike.test

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.GradleConnector
import org.pike.tasks.DelegatingTask
import org.pike.worker.PikeWorker

import java.nio.file.Files

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
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

    public static void getAllWorkers (final Task task, final List<PikeWorker> workers) {
        println("Check task $task.name")
        for (Object next: task.dependsOn) {
            if (next instanceof PikeWorker) {
                workers.add(0, next)
                getAllWorkers(next, workers)
            }
        }
    }

    /**
     * gets the worker task from dependencies of delegating task at given position
     * @param delegatingTask    delegating task
     * @param index  position to check
     * @return worker task
     * @throws IllegalStateException if no worker task at the given position was found
     */
    public static PikeWorker getWorker (final DelegatingTask delegatingTask, final int index = 0) {

        int currentIndex = 0
        for (Object next: delegatingTask.dependsOn) {
            if (next instanceof PikeWorker) {
                if (currentIndex == index) {
                    log.info(next.detailInfo)
                    return next
                }
                else
                    currentIndex++
            }
        }

        throw new IllegalStateException("Workertask not found on position $index(number of workertasks: ${currentIndex}")
    }

    /**
     * gets the worker task from dependencies of delegating task with given name
     * @param delegatingTask    delegating task
     *  @param name             name of workertask to search for
     * @return  worker task, not <code>null</code>
     * @throws IllegalStateException if no worker task with the given name was found
     */
    public static PikeWorker getWorker (final DelegatingTask delegatingTask, final String name) {
        String allTasks = ""
        for (Object next: delegatingTask.dependsOn) {
            if (next instanceof PikeWorker) {
                if (next.name.equals(name)) {
                    log.info(next.detailInfo)
                    return next
                }
                else
                    allTasks += " $next.name"
            }
        }

        throw new IllegalStateException("Workertask $name not found (available workertasks: ${allTasks.trim()}")

    }

    /**
     * create task in a project
     * @param clazz  clazz of task
     * @return task
     */
    public static DefaultTask createTask (final Class clazz) {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'pike'

        DefaultTask task = project.tasks.create(name:'sometask', type:clazz)
        project.evaluate()
        return  task
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
