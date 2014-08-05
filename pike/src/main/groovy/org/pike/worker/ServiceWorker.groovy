package org.pike.worker

/**
 * Worker which starts, stops or restarts any services
 */
class ServiceWorker {

    Collection<String> servicesToStart = new ArrayList<>()

    Collection<String> servicesToStop = new ArrayList<>()

    Collection <String> servicesToRestart = new ArrayList<>()

    void start (final String service) {
        this.servicesToStart.add(service)
    }

    void stop (final String service) {
        this.servicesToStop.add(service)
    }

    void restart (final String service) {
        this.servicesToRestart.add(service)
    }
}
