package org.pike.resolver.components

import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.04.13
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
interface Resolver {

    String resolve (Project project, String unresolvedString)

}
