package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.options.Option

class ForcableTask extends DefaultTask {

    boolean force

    @Option(option = "force", description = "Makes a clean configuration (e.g. pull on existing git clones)")
    public void setForce(boolean enabled) {
        this.force = enabled
    }
}
