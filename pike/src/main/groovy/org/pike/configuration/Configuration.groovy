package org.pike.configuration

import org.gradle.api.Project

class Configuration {

    String encoding

    Formatter formatter

    Boolean showMemory

    Boolean showLineNumbers

    String basepath

    Project project

    Boolean compareDialogWhitespaces

    Boolean disableAutomaticXmlValidation

    String sonarqubeUrl

    /**
     * sets the encoding
     * (eclipse - idea)
     * @param encoding new encoding
     */
    void encoding (String encoding) {
        this.encoding = encoding
    }

    /**
     * sets the base path
     * @param basepath
     */
    void basepath (String basepath) {
        this.basepath = basepath
    }


    /**
     * configures a show memory indicator
     * (eclipse - idea)
     * @param showMemory flag if indicator should be shown
     */
    void showMemory (final boolean showMemory) {
        this.showMemory = showMemory
    }

    /**
     * configures if line numbers should be shown in editor
     * (eclipse - idea)
     *
     * @param showLineNumbers flag if line numbers should be shown
     */
    void showLineNumbers (final boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers
    }

    /**
     * formatter configurations
     * (eclipse - idea)
     *
     * @param closure closure
     */
    void formatter (Closure closure) {
        formatter = new Formatter()
        formatter.project = project
        project.configure(formatter, closure)
    }

    /**
     * configures if whitespaces should be shown in compar dialog
     * (eclipse)
     *
     * @param compareDialogWhitespaces
     */
    void compareDialogWhitespaces (final boolean compareDialogWhitespaces) {
        this.compareDialogWhitespaces = compareDialogWhitespaces
    }

    /**
     * disables the automatic xml validation for performance reasons
     * (eclipse)
     *
     * @param automaticXmlValidation
     */
    void disableAutomaticXmlValidation () {
        this.disableAutomaticXmlValidation = Boolean.TRUE
    }

    /**
     * configures the sonarqube url for the
     * (eclipse)
     * @param url base url of sonarqube server
     */
    void sonarqubeUrl (final String url) {
        this.sonarqubeUrl = url
    }

}
