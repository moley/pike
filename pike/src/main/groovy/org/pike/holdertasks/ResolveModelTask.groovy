package org.pike.holdertasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.ModelLogger
import org.pike.autoinitializer.BaseClassAutoInitializer
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.resolver.DelegatingCompoundResolver
import org.pike.resolver.ResolveItem

import java.util.logging.Level

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class ResolveModelTask extends DefaultTask{

    @TaskAction
    public resolveModel () {
        log.info("Resolving model")

        ModelLogger.logConfiguration("preAutoInitializing", project, true)
        autoinitializeParents()

        ModelLogger.logConfiguration("preResolving", project, true)
        resolveModelElements()

        ModelLogger.logConfiguration("postConfigureTasks", project, false)
        log.info("Model is resolved")

        checkModel()


    }

    private void checkModel () {
        log.info("Check model")

        for (Host nextHost : project.hosts) {
            if (nextHost.operatingsystem == null)
                throw new IllegalStateException("You have to configure a operatingsystem for host ${nextHost.name}")
        }

        log.info("Model is checked")

    }

    /**
     * autoinitialize properties from base item
     */
    private void autoinitializeParents () {
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
    private void resolveModelElements() {
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
