pike
====

Gradle based provisioning tool like chef/puppet

./gradlew tasks - shows all tasks you can use 
./gradlew prepareInstallers - prepares all installers for all configured operatingsystems
./gradlew hosts - shows all configured hosts
./gradlew installPike --host=foo - installs pike on this host
./gradlew configure --host=foo starts configuration on this host
