package org.pike.utils

import org.gradle.api.Project


class PikeProperties {

    Properties properties = new Properties()

    File propertyFile

    Project project

    public PikeProperties (Project project) {
        this.project = project

        propertyFile = getPropertiesFile(project.projectDir)
        if (propertyFile.exists())
          properties.load(new FileReader(propertyFile))
    }

    public static File getPropertiesFile (File projectDir) {
      return new File (projectDir, 'build/pike.properties')
    }

    public String getProperty (final String key) {
        return properties.getProperty(key)
    }

    public File getFileProperty (final String key) {
        String prop = getProperty(key)
        return prop != null ? new File (prop) : null
    }

    public void setProperty (final String key, final String value) {
        properties.setProperty(key, value)
        propertyFile.parentFile.mkdirs()

        properties.store(new FileWriter(propertyFile), "Configured by pike")

    }
}
