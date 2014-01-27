package org.pike

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.junit.Assert
import org.pike.common.ProjectInfo
import org.pike.holdertasks.DeriveTasksTask
import org.pike.holdertasks.ResolveModelTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
class TestUtils {

    private final static String OS = System.getProperty("os.name")


    public static void prepareModel (final Project project) {

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()

        DeriveTasksTask checkmodelTask = project.tasks.deriveTasks
        checkmodelTask.deriveTasks()
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

    /**
     * Calls gradle with the given task in the given directory
     * Does NOT work without patching the gradle start stript
     *
     * @param debug Flag to start gradle in debug mode (for remote debugging)
     * @param debugPort debug port (only used when debugmode is true)
     * @param refreshDependencies used to add a commandline parameter to refresh all dependencies
     * @param path directory to start gradle in
     * @param task task to start, can contain blank separeted tasknames
     * @return output
     */
    public static ArrayList callGradleBuild(boolean debug, int debugPort, boolean refreshDependencies, int debugLevel, final String path, final String task, final Map properties) throws GradleException {
        File pathAsPath = getProjectPath(path)
        String gradleHome = System.getenv("GRADLE_HOME")
        if (gradleHome == null) throw new IllegalStateException("Environment Variable GRADLE_HOME is not set")
        gradleHome = gradleHome.replace("~",System.getProperty("user.home")) //to resolve tilde in unix

        def env = []
        System.getenv().each { k, v ->
            env << "$k=$v"
        }


        File selectedGradleHome = gradleHome != null ? new File (gradleHome) : pathAsPath

        env.each { println it }

        def returnMessages = []
        try {
            def commands = []
            def gradlecommands = []

            if (isWindows())
              commands << (selectedGradleHome.absolutePath + File.separator + "bin" + File.separator + "gradle.bat")
            else
              commands << (selectedGradleHome.absolutePath + File.separator + "bin" + File.separator + "gradle")

            if (debug) {
                gradlecommands << "debug"
                gradlecommands << debugPort
            }
            gradlecommands.addAll(task.split(" "))
            gradlecommands << "--stacktrace"

            if (debugLevel == 1) {
                gradlecommands << "--info"
            } else if ( debugLevel == 2) {
                gradlecommands << "--debug"
            }

            if (refreshDependencies) {
                gradlecommands << "--refresh-dependencies"
            }

            if (properties != null) {
                properties.each { k, v ->
                    gradlecommands << "-P$k=$v"
                }
            }

            commands.addAll(gradlecommands)

            println ("Commands:" + commands + "- Current toPath: " + new File ("").absolutePath + "- GRADLE_HOME: " + selectedGradleHome.absolutePath)

            println ("Output of gradlebuild " + task + " in toPath " + path + ":\n" + commands)
            def process = commands.execute(env, pathAsPath)

            def inThread = Thread.start {
                process.in.eachLine {
                    returnMessages << it
                    println(it)
                }
            }

            def errThread = Thread.start {
                process.err.eachLine {
                    returnMessages << it
                    println(it)
                }
            }

            inThread.join()
            errThread.join()

            process.waitFor()
        } catch (Exception e) {
            throw new GradleException(e.toString() + "(GRADLE_HOME=" + selectedGradleHome.absolutePath + ")", e);
        }

        return returnMessages;
    }
}
