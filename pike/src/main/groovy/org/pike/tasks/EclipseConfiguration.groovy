package org.pike.tasks

import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration
import org.pike.configurators.file.FileConfigurator
import org.pike.configurators.file.PropertiesConfigurator

class EclipseConfiguration extends CollectingConfiguration {

    public final static String FILE_RESOURCES_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.core.resources.prefs'
    public final static String FILE_UI_EDITORS_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.editors.prefs'
    public final static String FILE_UI_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.prefs'
    public final static String KEY_ENCODING = 'encoding'
    public final static String KEY_SPACES_FOR_TABS = 'spacesForTabs'
    public final static String KEY_TAB_WIDTH = 'tabWidth'
    public final static String KEY_SHOW_MEMORY = 'SHOW_MEMORY_MONITOR'


    /**
     * {@inheritDoc}
     */
    public void apply(final Logger logger,
                      final File globalConfigPath,
                      final File workspaceConfigPath,
                      final Collection<File> projectConfigPaths,
                      Configuration configuration,
                      final boolean dryRun) {
        super.apply(logger, globalConfigPath, workspaceConfigPath, projectConfigPaths, configuration, dryRun)

        workspace(FILE_RESOURCES_PREFS, KEY_ENCODING, configuration.encoding, dryRun, PropertiesConfigurator.class)
        workspace(FILE_UI_EDITORS_PREFS, KEY_SPACES_FOR_TABS, configuration.spacesForTabs, dryRun, PropertiesConfigurator.class)
        workspace(FILE_UI_EDITORS_PREFS, KEY_TAB_WIDTH, configuration.tabWidth, dryRun, PropertiesConfigurator.class)
        workspace(FILE_UI_PREFS, KEY_SHOW_MEMORY, configuration.showMemory, dryRun, PropertiesConfigurator.class)

        //TODO Formatter
        //TODO compare ignore whitespace
        //TODO Disable automatic XML validation
        //TODO Sonarqube / SonarLint
        //TODO Proxy
        //TODO linenumbers
    }


    private void workspace(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {

        if (value == null)
            return

        collectConfiguration('workspace', file, key, value)

        if (!dryRun) {
            if (workspaceConfigPath == null)
                throw new IllegalStateException("Workspace ConfigPath not set")

            File configFile = new File(workspaceConfigPath, file)
            FileConfigurator configurator = getFileConfigurator(clazz)
            configurator.configure(logger, configFile, key, value.toString(), dryRun)
        }

    }

    public void project(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {
        if (value == null)
            return

        if (!dryRun) {
            if (projectConfigPaths == null )
                throw new IllegalStateException("Project Config Paths not set")
            for (File nextProjectConf : projectConfigPaths) {
                File configFile = new File(nextProjectConf, file)

                FileConfigurator configurator = getFileConfigurator(clazz)
                configurator.configure(logger, configFile, key, value.toString(), dryRun)
            }
        }

    }
}
