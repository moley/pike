package org.pike.model.defaults

import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.04.13
 * Time: 01:36
 * To change this template use File | Settings | File Templates.
 */
class Defaults {

    public static String NEWLINE = System.getProperty("line.separator")

    String defaultuser = System.getProperty("user.name")

    /**
     * domain which is appended by default
     */
    String defaultdomain

    String currentHost

    String rootpath = '/'

    /**
     * password for managing pike
     */
    String pikepassword

    /**
     * user for managing pike
     */
    String pikeuser

    /**
     * link to gradle for usage with pike
     */
    String pikegradle  = 'http://services.gradle.org/distributions/gradle-2.0-all.zip'

    /**
     * project
     */
    private Project project

    /**
     * Creates a new instance with the given parameter(s)
     * @param project
     */
    public Defaults(Project project) {
        this.project = project
    }


    public String toString () {
        String objectAsString =  "Defaults $NEWLINE"
        objectAsString += "    * rootpath             : $rootpath $NEWLINE"
        objectAsString += "    * current host         : $currentHost $NEWLINE"
        objectAsString += "    * defaultdomain        : $defaultdomain $NEWLINE"
        objectAsString += "    * pike user            : $pikeuser $NEWLINE"
        objectAsString += "    * pike gradle          : $pikegradle $NEWLINE"
        objectAsString += "    * pike password        : $pikepassword $NEWLINE"
        objectAsString += "    * defaultuser          : $defaultuser $NEWLINE $NEWLINE"

        return objectAsString
    }
}
