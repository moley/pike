package org.pike.model

import org.gradle.api.Project
import org.pike.model.operatingsystem.Operatingsystem

/**
 * dsl class to define autoinstaller functionality
 */
class Autoinstall {

    /**
     * project
     */
    private Project project

    /**
     * list of operatingsystems to create installer for
     */
    Set<Operatingsystem> os = new HashSet<Operatingsystem>()

    /**
     * Constructor
     * @param project  used project
     */
    public Autoinstall (Project project) {
        this.project = project
    }

    /**
     * operatingsystem to generate installer for
     * @param os
     */
    public void os (final Operatingsystem os) {
        this.os.add(os)
    }


}
