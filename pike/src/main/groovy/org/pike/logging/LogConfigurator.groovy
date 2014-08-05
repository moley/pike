package org.pike.logging

import org.gradle.api.Project

import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.LogLevel
import org.slf4j.Logger

/**
 * Class to configure logging
 *
 * User: oleym
 */
class LogConfigurator {

    public final static String MAINLOGFILE = "build.log"

    public static int numberOfLogConfigurations = 0

    public static boolean verboseConfiguration = false




    private static File getValidConfigFile (final Project project) {
        final String logconf = "logback.xml"
        File projectFile = new File(project.projectDir, logconf)
        return projectFile.exists() ? projectFile : null
    }


    public static void consoleLog (final String message, final Gradle gradle) {
        if (isLogTheLogging(gradle))
            println (message)
    }

    private static isLogTheLogging (final Gradle gradle) {
        return verboseConfiguration || gradle.startParameter.projectProperties.get("loginfo") != null
    }

    /**
     * Method configures the log4j appropriate to the gradle loglevel.
     *
     * @param project project
     */
    public static configureLogging(Project project) {
        if (! (project.rootProject == project))
            return

        numberOfLogConfigurations ++

        File logConf = getValidConfigFile(project)
        if (logConf != null)
            configureLoggingByFile(project, logConf)
        else
            configureDefaultLogging(project)
    }

    /**
     * Method configured logging with an configuration file
     * @param project  project
     * @param logConfFile  configuration file
     */
    private static void configureLoggingByFile (final Project project, final File logConfFile) {
        consoleLog ("Using " + logConfFile.absolutePath + " to configure logging", project.gradle)

        def loggerfactory = loadClass(project, "org.slf4j.LoggerFactory")
        def joranconfigurator = loadClass(project, "ch.qos.logback.classic.joran.JoranConfigurator")
        def context = loggerfactory.getILoggerFactory();

        def configurator = joranconfigurator.newInstance()
        configurator.setContext(context)
        configurator.doConfigure(logConfFile)

    }

    /**
     * removes file appender and stops it to avoid locks in windows.
     * The console appender must not be detached, because we need it afterwards
     * @param project project
     */
    public static void shutdownLogging (Project project) {
        def loggerfactory = loadClass(project, "org.slf4j.LoggerFactory")
        def logger = loadClass(project, "ch.qos.logback.classic.Logger")
        def fileappender = loadClass(project, "ch.qos.logback.core.FileAppender")
        def rootLogger = loggerfactory.getLogger(logger.ROOT_LOGGER_NAME)
        for (def nextAppender : rootLogger.iteratorForAppenders()) {
            if (nextAppender.class.name.equals(fileappender.name)) {
                nextAppender.stop()
                rootLogger.detachAppender(nextAppender.name)
            }
        }
    }

    /**
     * load a class with gradle classloader
     * @param project project
     * @param fqn  fqn of class
     * @return class
     */
    private static Class loadClass (final Project project, final String fqn) {
        ClassLoader gradleClassloader = project.gradle.class.classLoader
        return gradleClassloader.loadClass(fqn)
    }



    /**
     * Method configures the logging appropriate to the gradle loglevel.
     * configures a fileappenders that always logs one loglevel stronger than console logger
     *
     * @param project project
     */
    private static void configureDefaultLogging (Project project) {

        Gradle gradle = project.gradle

        File logfile = new File (MAINLOGFILE)
        consoleLog ("set logfile to " +  logfile.absolutePath + ", Gradle Loglevel = " + gradle.startParameter.logLevel.name(), gradle)
        println ("If you need to see more details of the build watch the logfile " + logfile.absolutePath)

        def logger = loadClass(project, "ch.qos.logback.classic.Logger")
        def level = loadClass(project, "ch.qos.logback.classic.Level")
        def loggerfactory = loadClass(project, "org.slf4j.LoggerFactory")
        def fileappender = loadClass(project, "ch.qos.logback.core.FileAppender")
        def patternlayoutencoder = loadClass(project, "ch.qos.logback.classic.encoder.PatternLayoutEncoder")
        def thresholdfilter = loadClass(project, "ch.qos.logback.classic.filter.ThresholdFilter")



        def joranconfigurator = loadClass(project, "ch.qos.logback.classic.joran.JoranConfigurator").newInstance()
        joranconfigurator.setContext(loggerfactory.getILoggerFactory())


        def rootLogger = loggerfactory.getLogger(logger.ROOT_LOGGER_NAME)

        def levelFilterConsole = thresholdfilter.newInstance()
        def levelFilterFile = thresholdfilter.newInstance()

        rootLogger.level = level.INFO
        levelFilterConsole.level = level.WARN
        levelFilterFile.level = level.INFO

        if (gradle.startParameter.logLevel == LogLevel.INFO) {
            consoleLog("set to info", gradle)
            rootLogger.level = level.DEBUG
            levelFilterConsole.level = level.INFO
            levelFilterFile.level = level.DEBUG
        }

        if (gradle.startParameter.logLevel == LogLevel.DEBUG) {
            consoleLog ("set to debug", gradle)
            rootLogger.level = level.TRACE
            levelFilterConsole.level = level.DEBUG
            levelFilterFile.level = level.TRACE
        }

        consoleLog ("level of rootlogger                = " + rootLogger.level, gradle)
        consoleLog ("level of console appender filter   = " + levelFilterConsole.level, gradle)
        consoleLog ("level of file appender filter      = " + levelFilterFile.level, gradle)

        def fileappenderInstance

        for (def nextAppender : rootLogger.iteratorForAppenders()) {

            consoleLog ("Remove all filters at appender " + nextAppender, gradle)
            nextAppender.clearAllFilters()

            if (nextAppender.class.name.equals(fileappender.name)) {
                consoleLog("Appender " + fileappenderInstance + " is current fileappender", gradle)
                fileappenderInstance = nextAppender
            } else {
                nextAppender.addFilter (levelFilterConsole)
                levelFilterConsole.start()
                consoleLog ("Add console appender filter with level " + levelFilterConsole.level + " to consoleappender", gradle)
            }
        }

        if (fileappenderInstance == null) {
            consoleLog ("Creating new fileappender instance", gradle)
            fileappenderInstance = fileappender.newInstance()
            def lc = loggerfactory.getILoggerFactory()
            def ple = patternlayoutencoder.newInstance()

            ple.setPattern("%date %level [%thread] %logger{15} %msg%n");
            ple.setContext(lc)
            ple.start()

            consoleLog ("Add filter with level " + levelFilterFile.level + " to fileappender", gradle)
            fileappenderInstance.addFilter (levelFilterFile)
            levelFilterFile.start()


            fileappenderInstance.setEncoder(ple)
            fileappenderInstance.setContext(lc)

            rootLogger.addAppender(fileappenderInstance)
        }

        fileappenderInstance.setAppend (false)
        fileappenderInstance.setFile (logfile.absolutePath)
        fileappenderInstance.start()
        rootLogger.setAdditive(false)

        rootLogger.loggerContext.resetTurboFilterList()


        logTheLogConfiguration(project)



    }

    public static void logTheLogConfiguration (final Project project) {
        Gradle gradle = project.gradle
        if (isLogTheLogging(gradle)) {

            def logger = loadClass(project, "ch.qos.logback.classic.Logger")
            def loggerfactory = loadClass(project, "org.slf4j.LoggerFactory")
            def rootLogger = loggerfactory.getLogger(logger.ROOT_LOGGER_NAME)

            consoleLog("Turbofilters        : " + rootLogger.loggerContext.turboFilterList.size(), gradle)

            consoleLog("", gradle)
            consoleLog("Rootlevel           : " + rootLogger.level, gradle)
            consoleLog("Rootlogger Class    : " + rootLogger.class, gradle)
            consoleLog("Rootlevel effective : " + rootLogger.effectiveLevel, gradle)

            for (def nextAppender : rootLogger.iteratorForAppenders()) {
                consoleLog ("Appender Class      : " + nextAppender.class, gradle)
                consoleLog ("Appender isStarted  : " + nextAppender.isStarted(), gradle)
                consoleLog ("Number of filters   : " + nextAppender.getCopyOfAttachedFiltersList().size(), gradle)
                if (! nextAppender.getCopyOfAttachedFiltersList().isEmpty()) {
                    consoleLog ("Filter Class        : " + nextAppender.getCopyOfAttachedFiltersList().get(0).class, gradle)
                    consoleLog ("Filter Level        : " + nextAppender.getCopyOfAttachedFiltersList().get(0).level, gradle)
                    consoleLog ("Filter isStarted    : " + nextAppender.getCopyOfAttachedFiltersList().get(0).isStarted(), gradle)
                }
            }

            consoleLog("", gradle)

            rootLogger.error("Rootlogger error logged")
            rootLogger.warn("Rootlogger warn logged")
            rootLogger.info("Rootlogger info logged")
            rootLogger.debug("Rootlogger debug logged")
            rootLogger.trace("Rootlogger trace logged")

            //Check all the configuration stuff

            Logger log = Logger.getLogger(LogConfigurator.class)

            log.error("Log4jlogger error logged")
            log.warn("Log4jlogger warn logged")
            log.info("Log4jlogger info logged")
            log.debug("Log4jlogger debug logged")
            log.trace("Log4jlogger trace logged")

        }
    }


}