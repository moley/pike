package org.pike.tasks

import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration

class EclipseConfiguration extends CollectingConfiguration {

    public final static String FILE_RESOURCES_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.core.resources.prefs'
    public final static String FILE_UI_EDITORS_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.editors.prefs'
    public final static String FILE_UI_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.prefs'
    public final static String KEY_ENCODING = 'encoding'
    public final static String KEY_SPACES_FOR_TABS = 'spacesForTabs'
    public final static String KEY_TAB_WIDTH = 'tabWidth'
    public final static String KEY_SHOW_MEMORY = 'SHOW_MEMORY_MONITOR'



    /**
     * constructor
     * @param logger the gradle logger, can be <code>null</code>
     * @param workspaceConfigPath configuration path for workspace configurations
     * @param projectConfigPaths list of paths for project configurations
     */
    public EclipseConfiguration(final Logger logger,
                                final File workspaceConfigPath,
                                final Collection<File> projectConfigPaths) {
        this.workspaceConfigPath = workspaceConfigPath
        this.projectConfigPaths = projectConfigPaths
        this.logger = logger
    }

    /**
     * default constructor. Should only be used when calling check
     */
    public EclipseConfiguration() {
    }

    /**
     * applies the configurations
     *
     * @param configuration configuration to apply
     * @param dryRun true: don't write anything, only check overlapping configurations
     */
    public void apply(Configuration configuration, final boolean dryRun) {
        super.apply(configuration, dryRun)

        workspace(FILE_RESOURCES_PREFS, KEY_ENCODING, configuration.encoding, dryRun)
        workspace(FILE_UI_EDITORS_PREFS, KEY_SPACES_FOR_TABS, configuration.spacesForTabs, dryRun)
        workspace(FILE_UI_EDITORS_PREFS, KEY_TAB_WIDTH, configuration.tabWidth, dryRun)
        workspace(FILE_UI_PREFS, KEY_SHOW_MEMORY, configuration.showMemory, dryRun)

        //TODO Formatter
        //TODO compare ignore whitespace
        //TODO Disable automatic XML validation
        //TODO Sonarqube / SonarLint
        //TODO Proxy
        //TODO linenumbers
    }


    private void workspace(String file, String key, Object value, boolean dryRun) {

        if (value == null)
            return

        collectConfiguration('workspace', file, key, value)

        if (!dryRun) {
            if (workspaceConfigPath == null)
                throw new IllegalStateException("Workspace ConfigPath not set")
            Properties properties = new Properties()
            File configFile = new File(workspaceConfigPath, file)

            if (configFile.exists())
                properties.load(new FileInputStream(configFile))

            properties.setProperty(key, value.toString())
            if (logger)
                logger.lifecycle("Set " + key + " = " + value + " (" + configFile.absolutePath + ")")


            properties.store(new FileOutputStream(configFile), 'Saved by pike at ' + new Date())
        }

    }

    public void project(String file, String key, String value, boolean dryRun) {
        if (value == null)
            return

        if (!dryRun) {
            if (projectConfigPaths == null )
                throw new IllegalStateException("Project Config Paths not set")
            for (File nextProjectConf : projectConfigPaths) {
                File configFile = new File(nextProjectConf, file)


                if (configFile.exists())
                    properties.load(new FileInputStream(configFile))

                properties.setProperty(key, value)
                if (logger)
                    logger.lifecycle("Set " + key + " = " + value + " (" + configFile.absolutePath + ")")

                properties.store(new FileOutputStream(configFile), 'Saved by pike at ' + new Date())
            }
        }

    }
}
