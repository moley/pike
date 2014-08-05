package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by OleyMa on 01.08.14.
 */
class CreateBootstrapScript extends DefaultTask {

    File toPath

    @TaskAction
    public void create () {

        String text = """
/**
 * bootstrap build path, this includes pike.gradle by default
 */

buildscript {
    dependencies {
        classpath fileTree (dir: 'libs', includes: ['*.jar'])
    }
}

apply from: "pike.gradle"
"""

        File buildGradle = new File (toPath, 'build.gradle')
        buildGradle.parentFile.mkdirs()
        buildGradle.text = text


    }
}
