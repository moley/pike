package org.pike.autoinitializer

import org.pike.resolver.ResolveItem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 24.04.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
public class BaseClassAutoInitializer {

    public void initialize (final Object object, final String parentField) {

        MetaBeanProperty parentProperty = object.metaClass.properties.find {it->it.name.equals(parentField)}
        if (parentProperty == null)
            throw new IllegalStateException("parentfield " + parentField + " not found in object " + object)

        MetaMethod parentGetter = parentProperty.getGetter()
        if (! parentGetter.getReturnType().equals(object.getClass()))
            throw new IllegalStateException("parentfield " + parentField + " is not of type " + object.getClass().getName() + " but of type " + parentGetter.getReturnType().getName())

        Object parentObject = parentGetter.invoke(object)
        if (parentObject == null)
            return
        else
            initialize(parentObject, parentField)

        //setter
        Collection<MetaMethod> filteredMethods = object.metaClass.methods.findAll {it->
            it.name.startsWith("set") &&
                    it.parameterTypes.length == 1 &&
                    it.parameterTypes [0].name.equals(String.class.name)
        }

        //for all setters
        for (MetaMethod setter: filteredMethods) {
            println ("- Initialize setter " + setter.name)

            String getterName = setter.name.replaceFirst("set", "get")

            MetaMethod getter = parentObject.metaClass.methods.find {it->it.name.startsWith(getterName)}
            if (getter.getReturnType().equals(String.class)) {
              String value = getter.invoke(parentObject)
              if (value != null) {
                setter.invoke(object, value)
              }
            }
        }

    }
}
