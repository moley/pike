package org.pike.resolver

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.pike.ModelLogger
import org.pike.autoinitializer.BaseClassAutoInitializer
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created by OleyMa on 23.09.14.
 */
@Slf4j
class ModelResolver {

    @TaskAction
    public resolveModel (Project project) {
        log.info("Resolving model")

        ModelLogger.logConfiguration("preAutoInitializing", project, true)
        autoinitializeParents(project)

        ModelLogger.logConfiguration("preResolving", project, true)
        resolveModelElements(project)

        ModelLogger.logConfiguration("postConfigureTasks", project, false)
        log.info("Model is resolved")

    }



    /**
     * autoinitialize properties from base item
     */
    private void autoinitializeParents (Project project) {
        if (log.debugEnabled)
            log.debug("autoinitializeParents")
        BaseClassAutoInitializer autoinitializer = new BaseClassAutoInitializer()

        if (log.debugEnabled)
            log.debug("after (" + project.operatingsystems + ")")
        for (Operatingsystem nextOs: project.operatingsystems) {
            if (log.debugEnabled)
                log.debug("Complete fields from baseclass in object " + nextOs)
            autoinitializer.initialize(nextOs, "parent")
        }
    }



    /**
     * resolve all model elements
     */
    private void resolveModelElements(Project project) {
        final DelegatingCompoundResolver resolver = new DelegatingCompoundResolver()
        HashSet<Object> resolvedObjects = new HashSet<>()

        List<ResolveItem> allResolveItems = new ArrayList<ResolveItem>()

        resolver.collectResolveItems(resolvedObjects, allResolveItems, project, project.defaults)

        for (Operatingsystem nextOs: project.operatingsystems)
            resolver.collectResolveItems(resolvedObjects, allResolveItems, project, nextOs)

        for (Host nextHost : project.hosts)
            resolver.collectResolveItems(resolvedObjects, allResolveItems, project, nextHost)

        for (Environment nextEnvironment : project.environments) {
            resolver.collectResolveItems(resolvedObjects, allResolveItems, project, nextEnvironment)
        }

        resolver.resolveAll(allResolveItems)

    }
}
