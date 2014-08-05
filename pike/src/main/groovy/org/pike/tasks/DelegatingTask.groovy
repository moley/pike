package org.pike.tasks

import groovy.util.logging.Log
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil
import org.pike.common.TaskContext
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.worker.PikeWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
@Log
class DelegatingTask extends DefaultTask {


    List <PikeWorker> workers = new ArrayList<PikeWorker>()

    TaskContext context

    Project project

    String paramkey

    String paramvalue

    Environment environment

    public void addWorkerClass (final Class undoableWorkerClass, final Closure autoconfigClosure, final String name) {
        log.fine("Adding worker " + undoableWorkerClass + "to delegating task " + name)
        PikeWorker worker = undoableWorkerClass.newInstance()
        worker.name = name
        worker.autoconfigClosure = autoconfigClosure
        worker.context = context
        worker.project = project
        worker.paramkey = paramkey
        worker.paramvalue = paramvalue
        worker.environment = environment
        workers.add(worker)
    }

    /**
     * installPike the task, set all relevant model elements
     * @param host
     */
    public void configure (final Host host) {
        for (PikeWorker worker: workers) {
          worker.host = host
          worker.operatingsystem = host.operatingsystem
          if (worker.autoconfigClosure != null)
            ConfigureUtil.configure(worker.autoconfigClosure, worker)
        }
    }

    @TaskAction
    final void call() {
        if (uptodate() == true || context == TaskContext.checkenv) {
            return
        }

        if (context == TaskContext.install) {
            install()
        }
        else if (context == TaskContext.deinstall) {
            deinstall()
        }
        else
            throw new IllegalStateException("UndoableTask doesn't support context " + context + " only " + TaskContext.values())


    }

    /**
     * installPike all workers of the task
     */
    public void install () {
        log.info("Calling installPike of task " + name)
        log.info(getDetailInfo())
        for (PikeWorker worker:workers) {
            if (! worker.uptodate()) {
                println ("Worker $worker.name is not uptodate")
                worker.install()
            }
            else
              println ("Worker $worker.name is uptodate, skip")
        }
        log.info("_______________" + PikeWorker.NEWLINE + PikeWorker.NEWLINE)
    }

    /**
     * deinstall all workers of the task
     */
    public void deinstall () {
        throw new IllegalStateException("NYI")

    }

    /**
     * check if all workers are uptodate
     * @return true: all workers are uptodate, false: not
     */
    public boolean uptodate () {
        log.info("Calling uptodate of task " + name)

        boolean isUpToDate = true

        for (PikeWorker worker:workers) {
            boolean workerIsUptodate = worker.uptodate()
            String uptoDateString = workerIsUptodate ? "...is uptodate" : "... is not uptodate"
            log.info("Worker " + worker.name + uptoDateString)

            if (! workerIsUptodate)
                isUpToDate = false
        }

        return isUpToDate
    }

    public  String getDetailInfo() {
        String completeString = "Task " + name + PikeWorker.NEWLINE
        for (PikeWorker worker:workers) {
            completeString+= worker.detailInfo
        }
        return completeString
    }


}
