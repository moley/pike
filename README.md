pike
====

Pike is a provisioning tool like chef/puppet based on gradle technology. You can define the current or multiple 
remote hosts to be provisioned in different ways. 

Also pike is implemented to be simply bootstrapped, 
so you only need an ssh access to any remotehosts that should be configured. If you want to configure localhost you need no more additional tooling. 

Pike works with vagrant as well to easily test your provisioning scripts. 


## First steps 

   TODO

## General tasks

To show all available tasks call the gradle task 

    ./gradlew tasks 


To show what hosts are configured in your provisioning scripts you can call 

    ./gradlew hosts 


## Autoinstallation

### Create installers
With the autoinstall feature it is possible to create installers per operatingsystem you want to configure.

You can automatically create installers for your defined hosts by calling

    ./gradlew prepareInstallers 
    
This results in an installer per operatingsystem you have defined to be created. This installer contains a defined jre, the gradle wrapper itself and a platform specific script startscript.

### Installing installers on remote hosts

If you want to install pike via these installers you call

    ./gradlew installPike [--host=foo] 
    
### Starting provisioning on remote hosts

To start provisioning of one or all environments defined on any configured host you call 

    ./gradlew provision [--host=foo] [--env=bar] 
    
    
Anytime you call a provisioning pike copies changed plans to the remote host, the gradle wrapper (which defines what gradle version is used) all the changed libraries and dependencies and, if exists, an file gradle.properties (file to configure your build).  


## Vagrant

As using with autoinstallation plugin you have to create your installers first. 

Then you can create your vms with

    ./gradlew createVms 

To start your vms you simply call

    ./gradlew startVms 

If you want to install pike on a vm you call

    ./gradlew installPikeVm --host=foo 

To start provisioning of one or all environments defined on any configured vm you call 

    ./gradlew provisionVm --host=foo --env=bar 
