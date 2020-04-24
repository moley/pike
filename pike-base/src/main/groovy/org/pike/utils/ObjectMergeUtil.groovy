package org.pike.utils

/**
 * merges two objects of a type, which must have
 * a default constructor
 *
 * @param <T>  type, which objects are of
 */
class ObjectMergeUtil<T> {

    /**
     * merges fields of an specific object into fields of an generic object
     *
     * @param genericObject     fields of this object are merged by default
     * @param specificObject    fields of this object overwrite the generic ones
     * @param clazz type to be used for creating merged object
     * @return merged object
     */
    public T merge (T genericObject, T specificObject) {
        if (genericObject == null && specificObject == null)
            return null

        Class<T> genericClazz = genericObject.getClass()
        Class<T> specificClazz = specificObject.getClass()

        Class<T> clazz = genericObject != null ? genericClazz: specificClazz
        if (genericObject != null && specificObject != null && ! genericClazz.simpleName.equals(specificClazz.simpleName))
            throw new IllegalArgumentException("You did not use the same class for both generic and specific object")

        T mergedConfiguration = clazz.getDeclaredConstructor().newInstance()
        mergedConfiguration.properties.each { prop, val ->
            if(prop in ["metaClass","class"]) return

            if (genericObject != null) {
                Object valueFromGlobalConfiguration = genericObject.getProperty(prop)
                if (valueFromGlobalConfiguration != null)
                    mergedConfiguration.setProperty(prop, valueFromGlobalConfiguration)
            }

            if (specificObject != null) {
                Object valueFromSpecificConfiguration = specificObject.getProperty(prop)
                if (valueFromSpecificConfiguration != null)
                    mergedConfiguration.setProperty(prop, valueFromSpecificConfiguration)
            }
        }

        return mergedConfiguration

    }
}
