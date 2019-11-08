package org.pike.tasks

import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration
import org.pike.configurators.file.FileConfigurator
import org.pike.configurators.file.XmlConfigurator

class IdeaConfiguration extends CollectingConfiguration {

    public IdeaConfiguration() {
    }

    public IdeaConfiguration(final Logger logger,
                             final File globalConfigPath,
                             final Collection<File> projectConfigPaths) {
        this.projectConfigPaths = projectConfigPaths
        this.globalConfigPath = globalConfigPath
        this.logger = logger
    }

    @Override
    void apply(Configuration configuration, boolean dryRun) {
        super.apply(configuration, dryRun)

        global("options/editor.xml", "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", configuration.showLineNumbers, dryRun, XmlConfigurator.class)
        global("options/ui.lnf.xml", "/application/component[@name='UISettings']/option[@name='SHOW_MEMORY_INDICATOR']", configuration.showMemory, dryRun, XmlConfigurator.class)

    }


    public void global(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {
        if (value == null)
            return

        collectConfiguration('workspace', file, key, value)

        if (!dryRun) {
            if (globalConfigPath == null)
                throw new IllegalStateException("GlobalConfigPath not set")
            File configFile = new File(globalConfigPath, file)

            FileConfigurator fileConfigurator = getFileConfigurator(clazz)
            fileConfigurator.configure(logger, configFile, key, value.toString(), dryRun)
        }

    }

    public void project(String file, String key, Object value, boolean dryRun) {
        if (value == null)
            return

        throw new IllegalStateException("TODO")
    }

}
