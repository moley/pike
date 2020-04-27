package org.pike.eclipse.tasks

import org.pike.configuration.OperatingSystem

public class EclipsePaths {

    public File getWorkingDir(final File installPath) {
        return OperatingSystem.getCurrent().equals(OperatingSystem.MACOS) ? new File(installPath, 'Contents/MacOS') : installPath.absoluteFile

    }

    public File getConfigurationDir(final File installPath) {
      return OperatingSystem.getCurrent().equals(OperatingSystem.MACOS) ? new File(installDir, 'Contents/Eclipse/configuration') : new File (installPath, 'configuration')

    }
}
