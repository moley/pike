package org.pike.eclipse.tasks

import org.pike.configuration.OperatingSystem

public class EclipsePaths {

    public File getWorkingDir(final File installPath) {
        return OperatingSystem.getCurrent().equals(OperatingSystem.MACOS) ? new File(installPath, 'Contents/MacOS') : new File(installPath, 'eclipse')

    }

    public File getConfigurationDir(final File installPath) {
      return OperatingSystem.getCurrent().equals(OperatingSystem.MACOS) ? new File(installDir, 'Contents/Eclipse/configuration') : new File (installPath, 'eclipse/configuration')

    }
}
