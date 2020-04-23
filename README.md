[![Build Status](https://travis-ci.org/moley/pike.svg?branch=master)](https://travis-ci.org/moley/pike)
[![CodeCoverage](https://codecov.io/gh/moley/pike/branch/master/graph/badge.svg)](https://codecov.io/gh/moley/pike)


PIKE
====

Pike is a provisioning tool like chef/puppet based on gradle technology but has it's focus not on 
server provisioning but on provisioning your development machine. This means having the same 
development environment for all users of a project. As well you can define user specific 
configurations which override this common configurations were useful. 

## Installation

Pike is installed by running the following command in your terminal. 

```
sh -c "$(wget -O- https://github.com/moley/pike/blob/master/bootstrap/install.sh)"
```


## First build 

Starting pike the first time is very easy. It is all about creating a gradle buildfile which 
contains the configurations. A simple buildfile could be like: 
```
buildscript {
    repositories {  mavenCentral() }
    dependencies { classpath 'org.pike:pike:0.9' }
}

apply {
    plugin 'pike'
}

pike {
    git {
        gitmodule ('pike', 'https://github.com/moley/pike.git') {
            configuration {
                encoding 'UTF-8' //would lead to error in eclipse because workspace wide configuration  
            }
        }
        gitmodule ('leguan', 'https://github.com/moley/leguan.git')
    }

    idea {
        version '2019.2.3'
        globalConfFolder 'IdeaIC2019.2'
        plugin 'https://plugins.jetbrains.com/files/6546/71101/EclipseFormatter.zip?updateId=71101&pluginId=6546&family=INTELLIJ'
        xmx '2G'
    }

    eclipse {
        repo 'https://download.eclipse.org/releases/2019-09/'
        feature 'org.eclipse.egit'
        feature 'org.eclipse.buildship'
        xmx '4G'
    }

    configuration {
        encoding 'ISO-8859-15'
    }
}
```
This example configures that your project contains two submodules (pike and leguan) and
defines to be able to use **Eclipse** as well as **IntelliJ**. Tasks are created to 
* checkout the project (clone, cloneLeguan, clonePike)
* execute an initial build (buildLeguan, buildPike)
* delete the checked out modules (delete, deleteLeguan, deletePike)
* install, configure and start Eclipse (installEclipse, configureEclipse, startEclipse)
* install, configure and start IntelliJ (installIdea, configureIdea, startIdea)

## Configurations

You have different locations to configure your tools. If you want global configurations to be enabled 
for all users you add them in the buildfile globally (pike.configuration)
or per module (pike.git.[MODULENAME].configuration). 
If you want to override this configurations you can add gradle properties at your project (*gradle.properties*) 
or at a global location (*~/.gradle/gradle.properties*). As soon as these properties are prefixed with **pike.** they 
are tried to be taken in account: 
* pike.encoding : defines a global encoding
* pike.leguan.encoding : defines an encoding for the module leguan

Be aware that not all configurations are sensible at all locations, due to the IDEs save the configurations on different 
places. For example: If you define different encodings in your modules the configuration of your eclipse IDE will fail 
with an exception because Eclipse saves the encoding once per workspace and must be unique across your project.

| **Configuration**                  | **Eclipse**  | **IntelliJ**   |  Description                       |
|------------------------------------|--------------|----------------|------------------------------------|
| encoding ('UTF-8')                 |      x       |     x          | Encoding                           |
| showMemory (true)                  |      x       |     x          | Show memory indicator              |
| showLineNumbers (true)             |      x       |     x          | Show line numbers                  |
| compareDialogWhitespaces(true)     |      x       |     -          | Show whitespaces in compar dialog  |
| disableAutomaticXmlValidation()    |      x       |     -          | Disables xml validation on the fly |
| sonarqubeUrl ('https://sonar.org') |      x       |     -          | Configure sonarqube url            |

| **Formatter    **       | **Eclipse**  | **IntelliJ**            |   Description                     |
|-------------------------|--------------|-------------------------|-----------------------------------|
| name ('Some name')      |      x       |     x                   | The name of the formatter         |
| spacesForTabs (true)    |      x       |     x                   | true: Spaces, false: Tabs         |
| tabWidth (2)            |      x       |     x                   | Tab Width                         |
| indent (2)              |      x       |     x                   | Indention level                   |
| lineSplit (80)          |      x       |     x                   | Position of hard wrap line (preview margin) |




