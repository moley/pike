pike
====

Pike is a provisioning tool like chef/puppet based on gradle technology. You can define multiple 
hosts to be provisioned in different ways. Also pike is implemented to be simply bootstrapped, 
so you only need an ssh access to the hosts that should be configured. It works with vagrant as well 
to easily test your provisioning scripts. 


## First steps 

   TODO

## General tasks

To show all available tasks call the gradle task 

   ./gradlew tasks - shows all tasks you can use 


To show what hosts are configured in your provisioning scripts you can call 

    ./gradlew hosts - shows all configured hosts


## Autoinstallation

You can automatically create installers for your defined hosts by calling

    ./gradlew prepareInstallers 

If you want to install pike you call

    ./gradlew installPike --host=foo 

To start provisioning of one or all environments defined on any configured host you call 

    ./gradlew provision --host=foo --env=bar 


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
