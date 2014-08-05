package org.pike.worker

import groovy.util.logging.Slf4j

/**
 * Created by OleyMa on 06.05.14.
 */
@Slf4j
abstract class CombinedWorker extends PikeWorker {

    protected Collection<PikeWorker> workers = new ArrayList<PikeWorker>()

    public abstract void configure ()

    @Override
    void install() {
        if (workers.isEmpty())
          configure()

        if (workers.isEmpty())
            throw new IllegalStateException("No sub workers are added in a CombinedWorker")

        workers.each {
            if (log.debugEnabled)
              log.debug("Calling install of subworker " + it)
            it.configure(this)
            if (! it.uptodate())
              it.install()
        }
    }



    @Override
    boolean uptodate() {
        if (log.debugEnabled)
            log.debug("Check uptodate")
        if (workers.isEmpty())
            configure()

        if (workers.isEmpty())
            throw new IllegalStateException("No sub workers are added in a CombinedWorker")

        for (PikeWorker nextWorker: workers) {
            nextWorker.configure(this)
            boolean uptodate = nextWorker.uptodate()
            if (log.debugEnabled)
                log.debug(" - from worker ${nextWorker}: ${uptodate}")
            if (! uptodate)
                return false
        }

        return true
    }
}
