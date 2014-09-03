package org.pike.os

/**
 * Provider containing os specialities for redhat systems
 */
class RedhatProvider extends LinuxProvider{
    @Override
    boolean isActive() {
        return false //TODO
    }
}
