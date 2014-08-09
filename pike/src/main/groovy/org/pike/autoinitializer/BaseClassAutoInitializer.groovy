package org.pike.autoinitializer

import groovy.util.logging.Slf4j
import org.codehaus.groovy.reflection.CachedClass

import java.lang.annotation.Annotation

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 24.04.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class BaseClassAutoInitializer {

    private boolean isSupportedType (final CachedClass clazz) {
        if (clazz.name == String.name || clazz.name == Boolean.name)
            return true

    }

    public void initialize (final Object object, final String parentField) {

        MetaBeanProperty parentProperty = object.metaClass.properties.find {it->it.name.equals(parentField)}
        if (parentProperty == null)
            throw new IllegalStateException("parentfield " + parentField + " not found in object " + object)

        MetaMethod parentGetter = parentProperty.getGetter()
        if (! parentGetter.getReturnType().isAssignableFrom(object.getClass()))
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
                    isSupportedType(it.parameterTypes [0])
        }

        //for all setters
        for (MetaMethod setter: filteredMethods) {

            boolean propertyHasNoAutoInitializing = false
            String fieldName = setter.name.substring(3,4).toLowerCase() + setter.name.substring(4)
            MetaProperty property = object.metaClass.properties.find(){it.name == fieldName}

            if (property instanceof  MetaBeanProperty) {
                MetaBeanProperty metaBeanProperty = property
                if (metaBeanProperty.getField() != null) {
                    for (Annotation nextAnnotation : metaBeanProperty.getField().field.declaredAnnotations) {
                        if (nextAnnotation.annotationType().equals(NoAutoInitializing.class)) {
                            propertyHasNoAutoInitializing = true
                            break
                        }
                    }
                }
            }

            if (propertyHasNoAutoInitializing) {
                if (log.debugEnabled)
                    log.debug("Property $fieldName + has the annotation NoAutoInitializing, skip...")
                continue
            }


            if (log.debugEnabled)
                log.debug("Try to initialize setter $setter.name")

            CachedClass clazz = setter.parameterTypes [0]

            String getterName = setter.name.replaceFirst("set", "get")

            MetaMethod getter = parentObject.metaClass.methods.find {it->it.name.startsWith(getterName)}
            if (getter.returnType.name.equals(clazz.name)) {
              Object value = getter.invoke(parentObject)
              if (value != null) {
                  if (log.debugEnabled)
                    log.debug("Setting value $value in object $object getting from parent $parentObject")
                  setter.invoke(object, value)
              }

            }
        }

    }
}
