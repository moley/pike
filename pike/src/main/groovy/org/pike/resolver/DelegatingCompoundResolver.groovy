package org.pike.resolver

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.04.13
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class DelegatingCompoundResolver {



    public void resolveAll (final List<ResolveItem> resolveItems) {

        //resolve all items
        for (ResolveItem next: resolveItems) {
            next.resolve()
        }

        //throw an error if not all items could be resolved
        Collection<ResolveItem> nonResolvableItems = new ArrayList<ResolveItem>()
        for (ResolveItem next: resolveItems) {
            if (next.isNotResolvable())
                nonResolvableItems.add(next)

        }
        if (! nonResolvableItems.isEmpty()) {
            String message = nonResolvableItems.size() + "item(s) not resolvable: \n"
            for (ResolveItem nextItem: nonResolvableItems) {
                message += nextItem.toString() + "\n"
            }
            throw new ResolveException(message, nonResolvableItems)
        }
    }

    /**
     * collects items to be resolved
     * @param collectedObjects    objects that are already collected
     *
     * @param resolveItems   list of items to be resolved
     * @param project  project
     * @param object current object
     */
    public void collectResolveItems(final HashSet<Object> collectedObjects, final List<ResolveItem> resolveItems, final Project project, Object object) {
        if (object == null)
            throw new NullPointerException("resolvable object must not be <code>null</code>")

        if (collectedObjects.contains(object))
            return
        else
            collectedObjects.add(object)

            if (log.debugEnabled)
              log.debug ("Resolve variables in object of type " + object.getClass())

            Collection<MetaMethod> filteredMethods = object.metaClass.methods.findAll {it->
                it.name.startsWith("set") &&
                        it.parameterTypes.length == 1 &&
                        it.parameterTypes [0].name.equals(String.class.name)
            }

            for (MetaMethod setter: filteredMethods) {
                if (log.debugEnabled)
                    log.debug ("- Resolving setter " + setter.name)

                String getterName = setter.name.replaceFirst("set", "get")

                MetaMethod getter = object.metaClass.methods.find {it->it.name.startsWith(getterName)}
                ResolveItem newItem = new ResolveItem(object, getter, setter, project)

                resolveItems.add(newItem)
            }

            for (MetaProperty next : object.metaClass.properties) {

                try {

                    if (next.type.package == null) //simple types
                        continue

                    if (next instanceof MetaBeanProperty) {
                        MetaBeanProperty nextMeta = next
                        if (nextMeta.getter == null || nextMeta.setter == null)
                          continue
                    }

                    if (next.type.package.name.startsWith('org.pike.model')) {  //object reference of a model element
                        if (next.getProperty(object) != null)
                            collectResolveItems(collectedObjects, resolveItems, project, next.getProperty(object))
                    } else if (next.getProperty(object) in Collection) { //collection
                        Collection collection = next.getProperty(object)
                        for (Object nextInCollection : collection) {
                            if (nextInCollection != null)
                                collectResolveItems(collectedObjects, resolveItems, project, nextInCollection)
                        }
                    }

                } catch (GroovyRuntimeException e) {
                    String enrichedMessage = "In object ${object} ${e.toString()}"
                    log.error(enrichedMessage, e)
                    throw new GroovyRuntimeException(enrichedMessage, e)
                }
            }

        }

}
