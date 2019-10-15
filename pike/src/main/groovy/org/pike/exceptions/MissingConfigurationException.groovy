package org.pike.exceptions


class MissingConfigurationException extends RuntimeException {

    public MissingConfigurationException (final String message) {
        super(message)
    }
}
