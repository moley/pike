package org.pike.common

import org.gradle.api.internal.DynamicObject
import org.gradle.api.internal.DynamicObjectAware
import org.gradle.api.internal.ExtensibleDynamicObject
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.internal.reflect.Instantiator

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 16.04.13
 * Time: 08:16
 * To change this template use File | Settings | File Templates.
 */
class NamedElement implements ExtensionAware, DynamicObjectAware  {

    public static String NEWLINE = System.getProperty("line.separator")

    ExtensibleDynamicObject extensibleDynamicObject

    private String name

    public NamedElement (String name, Instantiator instantiator = null) {
        setName(name)
        this.extensibleDynamicObject =  new ExtensibleDynamicObject(this, instantiator)
    }

    private void setName (final String name) {
        this.name = name
    }

    public String getName () {
        return name
    }

    public Convention getConvention() {
        return extensibleDynamicObject.getConvention();
    }

    public ExtensionContainer getExtensions() {
        return getConvention();
    }

    DynamicObject getAsDynamicObject(){
        return extensibleDynamicObject
    }
}
