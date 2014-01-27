package org.pike.resolver

import groovy.util.logging.Log
import org.gradle.api.Project
import org.pike.common.NamedElement
import org.pike.resolver.components.CurrentPathResolver
import org.pike.resolver.components.CurrentUserResolver
import org.pike.resolver.components.Resolver

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 21.04.13
 * Time: 23:45
 * To change this template use File | Settings | File Templates.
 */
@Log
class ResolveItem {

    Object object
    MetaMethod getter
    MetaMethod setter

    boolean isResolved

    Project project

    private static List<Resolver> resolvers = new ArrayList<Resolver>()

    static {
        resolvers.add(new CurrentUserResolver())
        resolvers.add(new CurrentPathResolver())
    }

    public ResolveItem (Object object, MetaMethod getter, MetaMethod setter, Project project) {
        this.object = object
        this.getter = getter
        this.setter = setter
        this.project = project
    }

    /**
     * resolves current item
     * @param project       project
     */
    public void resolve (Project project) {

        log.fine("Resolving " + object + "- " + getter.name)

        if (isResolved)
            return

        String value = getValue()

        if (value != null) {
          //resolve this
          for (Resolver resolver : resolvers) {
            String valueBefore = value
            if (valueBefore.contains("\${")) {
              value = resolver.resolve(project, valueBefore)
              if (value == null)
                throw new IllegalStateException("Resolver " + resolver.class+ " replaced " + valueBefore + " with <code>null</code>")
            }
          }
          setValue(value)
        }

        isResolved = true
    }

    public void setValue (final String value) {
        setter.invoke(object, value)
    }

    public String getValue () {
        return getter.invoke(object)
    }



    /**
     * returns if current resolve item still contains unresolved variables
     * @return
     */
    public boolean isNotResolvable () {
        String value = getValue()
        if (value == null)
            return false

        if (! isResolved)
            throw new IllegalStateException("Current object not resolved, please call the resolve() method before asking if it is resolved")

        return (value.contains("\${"))

    }

    String toString () {
        String name = object instanceof NamedElement ? ((NamedElement) object).name : "unknown"

        return object.getClass().name + "($name)#${getter.name}=${getValue()}"
    }


}
