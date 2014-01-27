package org.pike.resolver.components

import groovy.util.logging.Log

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.04.13
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
@Log
abstract class AbstractResolver implements Resolver  {

    /**
     * resolves every occurance of variables in the unresolved string with with,
     * returns unresolved string, if any of variable or with is <code>null</code>
     * @param unresolved unresolved string
     * @param variable variable to resolve
     * @param with resolve with
     * @return resolved string
     */
    protected String resolve (final String unresolved, final String variable, final String with) {

        if (unresolved == null)
            return null

        String resolved = unresolved
        log.finer ("resolving " + unresolved + " with variable " + variable + " and data " + with)

        if (with != null && variable != null)
          resolved = unresolved.replace("\${" + variable + "}", with)

        log.finer ("-> resolved " + resolved)

        return resolved
    }



}
