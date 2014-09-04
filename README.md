pike
====

Gradle based provisioning tool like chef/puppet

General tasks:

./gradlew tasks - shows all tasks you can use 
./gradlew hosts - shows all configured hosts

To use pike with autoinstall:

./gradlew prepareInstallers - prepares all installers for all configured operatingsystems
./gradlew installPike --host=foo - installs pike on this host
./gradlew configure --host=foo starts configuration on this host

To use pike with vagrant: 

./gradlew prepareInstallers - prepares all installers for all configured operatingsystems
./gradlew createVms - creates vms that are configured
./gradlew startVms - starts vms that are configured
./gradlew installPikeVm - installs pike on vm
./gradlew configureVm - starts configuration on vm
