package org.pike.eclipse.configuration

import org.pike.configuration.OperatingSystem

class OsToken {

    OperatingSystem operatingSystem
    String osToken

    OsToken (final OperatingSystem operatingsystem, final String osToken) {
        this.operatingSystem = operatingsystem
        this.osToken = osToken
    }


}
