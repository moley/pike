[![Build Status](https://travis-ci.org/moley/pike.svg?branch=master)](https://travis-ci.org/moley/pike)
[![CodeCoverage](https://codecov.io/gh/moley/pike/branch/master/graph/badge.svg)](https://codecov.io/gh/moley/pike)


PIKE
====

Pike is a provisioning tool like chef/puppet based on gradle technology but has it's focus not on 
server provisioning but on provisioning your development machine. This means having the same 
development environment for all users of a project. As well you can define user specific 
configurations which override this common configurations were useful. 

# Installation

Pike is installed by running the following command in your terminal. 

```
sh -c "$(wget -O- https://raw.githubusercontent.com/moley/pike/master/bootstrap/install.sh)"
```

# First build 

After you have installed pike with the upper command you can start it by simply calling 
```
./gradlew tasks
```
Afterwards you see all the tasks which can be called in this installation.


# Use cases

# Creating an equal development environment for every user

You can use **pike** to configure your development environment in a equal manner for any developer. 
You can find an example buildfile in testproject [development](testprojects/development/build.gradle)

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
 

# Mirror eclipse versions on a company wide mirror server
If you want to setup a mirror for a certain eclipse version you can setup your build like 
shown in testproject [eclipseAdmin](testprojects/eclipseAdmin/build.gradle)

 






