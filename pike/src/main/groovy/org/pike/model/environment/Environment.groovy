package org.pike.model.environment

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.reflect.Instantiator
import org.pike.common.NamedElement
import org.pike.environment.EnvCollector
import org.pike.tasks.DelegatingTask
import org.pike.worker.CombinedWorker
import org.pike.worker.PikeWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 16.04.13
 * Time: 08:16
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class Environment extends NamedElement {

    Project project

    EnvCollector envCollector = new EnvCollector()

    private String TASKGROUP_PLANS = "Pike Plans"
    private String TASKGROUP_WORKER = 'Pike Workers'

    Collection<String> createdTaskNames = new ArrayList<String>()

    HashMap<String, Integer> currentIndexHash = new HashMap<String, Integer>()

    HashMap<String, String> matrixParams = new HashMap<String, String>()

    Boolean active

    /**
     * constructor
     * @param name name of environment
     */
    public Environment(String name, Instantiator instantiator = null) {
        super(name, instantiator)
        matrixParams.put("", "")
    }

    public void matrix(final HashMap<String, String> matrixIds) {
        if (matrixParams.size() == 1 && matrixParams.get('').trim().isEmpty())
            matrixParams.clear()

        for (String nextMatrix : matrixIds.keySet()) {
            String value = matrixIds.get(nextMatrix)
            if (log.debugEnabled)
                log.debug("Add matrix " + nextMatrix + "->" + value)
            matrixParams.put(nextMatrix, value)
        }
    }

    public String toString() {
        String objectAsString = "Environment <" + name + ">$NEWLINE"

        for (String nextTask : createdTaskNames) {
            objectAsString += "    * task                    : $nextTask $NEWLINE"
        }

        return objectAsString
    }

    /**
     * we handle the action configuration snippets
     * with methodMissing to be generic
     *
     * @param name name of the method
     * @param args args of method
     */
    def methodMissing(String workername, args) {

        Integer currentIndex = currentIndexHash.get(workername)
        if (currentIndex == null)
            currentIndex = 0
        else
            currentIndex++
        currentIndexHash.put(workername, currentIndex)

        if (!envCollector.isEnvironmentActive(project, name))
            return

        if (createdTaskNames.isEmpty()) {
            log.info("Creating tasks from environment definitions")

            String nameToUpper = name.charAt(0).toUpperCase().toString() + name.substring(1)

            for (String matrixParamKey : matrixParams.keySet()) {
                String matrixParamValue = matrixParams.get(matrixParamKey)
                String taskName = 'install' + nameToUpper + matrixParamKey
                if (log.infoEnabled)
                    log.info("Creating task " + taskName)
                DelegatingTask genericTaskObject = project.task([type: DelegatingTask], taskName) as DelegatingTask
                genericTaskObject.group = TASKGROUP_PLANS
                genericTaskObject.environment = this
                genericTaskObject.group = TASKGROUP_PLANS
                genericTaskObject.project = project
                genericTaskObject.paramkey = matrixParamKey
                genericTaskObject.paramvalue = matrixParamValue

                Task holdertasks = project.tasks.findByName('install')
                holdertasks.dependsOn genericTaskObject
                if (log.debugEnabled)
                    log.debug("Adding dependency from holdertask " + holdertasks.name + " to " + genericTaskObject.name)

                createdTaskNames.add(genericTaskObject.name)

            }


            PikeWorker preTask = null
            for (String nextTaskName : createdTaskNames) {
                DelegatingTask task = project.tasks.findByName(nextTaskName)
                Class workerClass = getWorkerClass(workername)
                Closure autoconfigClosure = args[0] instanceof Closure ? args[0] : null

                final String workerTaskName = "_" + nextTaskName + currentIndex + workername

                PikeWorker pikeTask = project.task([type: workerClass], workerTaskName) as PikeWorker
                pikeTask.autoconfigClosure = autoconfigClosure
                pikeTask.paramkey = task.paramkey
                pikeTask.paramvalue = task.paramvalue
                pikeTask.environment = task.environment
                pikeTask.group = TASKGROUP_WORKER
                pikeTask.onlyIf {! pikeTask.uptodate()}

                task.dependsOn pikeTask

                if (preTask != null)
                    pikeTask.dependsOn preTask

                log.info("Create pike task $pikeTask.name")

                preTask = pikeTask

            }
        }
    }

    /**
     * looks for the first uppercase and derives classname from name before upper
     * @param workername name of the worker, has to be a class xWorker in package org.pike.worker
     * @return determined classname
     */
    Class getWorkerClass(final String workername) {

        String classname = "org.pike.worker." + workername[0].toUpperCase() + workername.substring(1) + "Worker"
        if (log.debugEnabled)
            log.debug("create taskclass <" + classname + "> from dsl name " + workername)

        try {
            Class clazz = getClass().getClassLoader().loadClass(classname)
            return clazz
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find workerclass ${classname} for environment ${name} and workername ${workername}")
            //TODO output the existing workerclasses
        }

    }


}
