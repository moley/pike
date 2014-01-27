package org.pike.common

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 16.04.13
 * Time: 08:16
 * To change this template use File | Settings | File Templates.
 */
class NamedElement {

    public static String NEWLINE = System.getProperty("line.separator")


    private String name


    public NamedElement (String name) {
        setName(name)
    }

    private void setName (final String name) {
        this.name = name
    }

    public String getName () {
        return name
    }
}
