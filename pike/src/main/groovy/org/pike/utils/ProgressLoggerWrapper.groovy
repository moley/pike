package org.pike.utils

import org.gradle.api.DefaultTask

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener


public class ProgressLoggerWrapper implements PropertyChangeListener {

    final def progressLogger

    public ProgressLoggerWrapper (final DefaultTask task, final String description) {
        //get ProgressLoggerFactory class
        Class<?> progressLoggerFactoryClass;
        try {
            //Gradle 2.14 and higher
            progressLoggerFactoryClass = Class.forName("org.gradle.internal.logging.progress.ProgressLoggerFactory");
        } catch (ClassNotFoundException e) {
            //prior to Gradle 2.14
            progressLoggerFactoryClass = Class.forName("org.gradle.logging.ProgressLoggerFactory");
        }
        if (task != null) {
            def progressLoggerFactory = task.services.get(progressLoggerFactoryClass)
            progressLogger = progressLoggerFactory.newOperation(task.class)
            progressLogger.setDescription(description)
            start(description)
        }
    }

    void setDescription (final String description) {
        progressLogger.setDescription(description)
    }

    String getDescription () {
        return progressLogger.description
    }

    /**
     * Start message.
     *
     * @param startMessage Message
     */
    void start(String startMessage){
        progressLogger.started(startMessage)
    }

    /**
     * Property Change event fired.
     *
     * @param evt Event
     */
    @Override
    void propertyChange(PropertyChangeEvent evt) {
        progressLogger.progress(evt.newValue.toString())
    }

    /**
     * log any progress
     * @param progress the progress
     */
    void progress (final String progress) {
        progressLogger.progress(progress)
    }

    /**
     * Ready. Finished. End.
     */
    void end(){
        progressLogger.completed()
    }
}
