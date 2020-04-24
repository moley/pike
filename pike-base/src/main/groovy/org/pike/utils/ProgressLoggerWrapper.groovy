package org.pike.utils


import org.gradle.api.Project
import org.gradle.internal.logging.progress.ProgressLoggerFactory

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

public class ProgressLoggerWrapper implements PropertyChangeListener {

    final def progressLogger

    public ProgressLoggerWrapper (final Project project, final String description) {
        def serviceRegistry = project.services
        ProgressLoggerFactory progressLoggerFactory = serviceRegistry.get(ProgressLoggerFactory.class)
        progressLogger = progressLoggerFactory.newOperation(description)
        setDescription(description)
        start(description)
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
    private void start(String startMessage){
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
