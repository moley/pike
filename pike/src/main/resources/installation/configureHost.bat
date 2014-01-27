echo Starting pike in path %~pd0


SET GRADLE_HOME=%~pd0%\gradle

SET JAVA_HOME=%~pd0%\jdk

SET PATH=%GRADLE_HOME%\bin:%JAVA_HOME%\bin:%PATH%


echo GRADLE_HOME set to %GRADLE_HOME% and added to PATH

echo JAVA_HOME set to %JAVA_HOME% and added to PATH

echo Startparameter = %1


echo Call gradle

%GRADLE_HOME%\bin\gradle -b bootstrapbuild.gradle %1 --stacktrace