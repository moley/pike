package org.pike.resolver.components

import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.04.13
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
class CurrentUserResolver extends AbstractResolver {
    @Override
    String resolve(Project project, String unresolvedString) {
        String user = System.getProperty("user.name")

        return resolve(unresolvedString, "user", user)
    }
}
