package org.pike.eclipse.utils

class CreateUpdatesiteMetadata {

    private void createMetadataFile (final File file, final String name, final String type, long timestamp) {
        int numberOfFiles = file.parentFile.listFiles().length
        String prefix = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<repository name="Mirror $name" type="${type}" version="1.0.0">
  <properties size="1">
    <property name="p2.timestamp" value="${timestamp}"/>
  </properties>
  <children size="${numberOfFiles}">
"""
        String postfix = """  </children>
</repository>
"""

        String text = prefix
        for (File next: file.parentFile.listFiles()) {
            if (next.isDirectory()) {
                text +="""      <child location="${next.name}"/>\n"""
            }
        }

        text+= postfix
        file.createNewFile()
        file.text = text
    }

    public void createMetadata (final File folder, final String name) {
        long time = System.currentTimeMillis()
        createMetadataFile(new File (folder, 'compositeArtifacts.xml'), name,  'org.eclipse.equinox.internal.p2.artifact.repository.CompositeArtifactRepository', time)
        createMetadataFile(new File (folder, 'compositeContent.xml'), name, 'org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository', time)

    }
}
