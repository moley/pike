package org.pike.autoinstall

import org.gradle.api.internal.AbstractTask
import org.gradle.internal.logging.progress.ProgressLogger
import org.gradle.logging.ProgressLoggerFactory

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 00:15
 * To change this template use File | Settings | File Templates.
 */
class PropertyChangeProgressLogging implements PropertyChangeListener{

    /** Progresslogger */
    ProgressLogger progressLogger;

    /**
     * Constructor.
     *
     * @param factory Logger Factory
     * @param task Task to log
     */
    PropertyChangeProgressLogging(ProgressLoggerFactory factory, Class<? extends AbstractTask> task) {
        progressLogger = factory.newOperation(task)
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
     * Ready. Finished. End.
     */
    void end(){
        progressLogger.completed()
    }
}
