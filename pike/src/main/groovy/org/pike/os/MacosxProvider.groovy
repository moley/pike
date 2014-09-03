package org.pike.os

/**
 * provider for mac osx
 */
class MacosxProvider extends LinuxProvider {

    @Override
    boolean isActive() {
        return System.getProperty('os.name').toLowerCase().contains('mac os x')
    }

    @Override
    String getId() {
        return 'mac'
    }
}
