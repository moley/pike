package org.pike.utils


class StringUtils {

    public String getFirstUpper (String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1)
    }
}
