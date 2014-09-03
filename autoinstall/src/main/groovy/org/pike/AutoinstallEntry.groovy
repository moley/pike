package org.pike

import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created by OleyMa on 22.08.14.
 */
class AutoinstallEntry {
    Operatingsystem os
    BitEnvironment bitEnvironment

    public AutoinstallEntry (Operatingsystem os, BitEnvironment bitEnvironment) {
        this.os = os
        this.bitEnvironment = bitEnvironment
    }
}
