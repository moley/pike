package org.pike.resolver.components

import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.04.13
 * Time: 00:33
 * To change this template use File | Settings | File Templates.
 */
class CurrentPathResolver extends AbstractResolver {
    @Override
    String resolve(Project project, String unresolvedString) {
        String path = new File ("").absolutePath
        return resolve(unresolvedString, "currentPath", path)
    }

}
