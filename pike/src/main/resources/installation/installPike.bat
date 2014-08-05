echo Installing pike in path %~pd0 


unzip.exe -o tmp/*.zip


dir /b gradle* >gradleversion.txt

set /p CURRENTGRADLE=<gradleversion.txt

del gradleversion.txt


dir /b jdk* >jdkversion.txt

set /p CURRENTJDK=<jdkversion.txt

del jdkversion.txt


echo Current gradle version %CURRENTGRADLE%

echo Current jdk version %CURRENTJDK%


rename %CURRENTGRADLE% gradle

rename %CURRENTJDK% jdk

