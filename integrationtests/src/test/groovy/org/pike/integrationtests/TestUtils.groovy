package org.pike.integrationtests


class TestUtils {

    static File getRootProject() {
        File currentDir = new File("").getAbsoluteFile()
        while (currentDir.getParentFile() != null) {
            File settingsGradle = new File(currentDir, "settings.gradle")
            if (settingsGradle.exists()) {
                return settingsGradle.getParentFile()
            }

            if (currentDir.getParentFile() != null)
                currentDir = currentDir.getParentFile()

        }

        throw new IllegalStateException("RootProject not found from " + new File("").getAbsolutePath())
    }


}
