package org.pike.utils

import org.eclipse.jgit.lib.ProgressMonitor

class PikeProgressMonitor implements ProgressMonitor {

    private ProgressLoggerWrapper progressLoggerWrapper

    public PikeProgressMonitor(ProgressLoggerWrapper progressLoggerWrapper) {
        this.progressLoggerWrapper = progressLoggerWrapper
    }

    private int totalWork
    private int currentWork

    private String currentTask

    @Override
    void start(int totalTasks) {
    }

    @Override
    void beginTask(String title, int totalWork) {
        this.currentTask = title
        this.currentWork = 0
        this.totalWork = totalWork
        progressLoggerWrapper.progress(progressLoggerWrapper.getDescription() + ":  " + currentTask + "(" + currentWork + " of " + totalWork + " finished)")
    }

    @Override
    void update(int completed) {
        currentWork += completed
        progressLoggerWrapper.progress(progressLoggerWrapper.getDescription() + ":  " + currentTask + "(" + currentWork + " of " + totalWork + " finished)")
    }

    @Override
    void endTask() {

    }

    @Override
    boolean isCancelled() {
        return false
    }
}
