package org.pike

/**
 * Created by OleyMa on 22.08.14.
 */
public enum BitEnvironment {
    _32,
    _64

    public String getId () {
        return name().substring(1)
    }
}