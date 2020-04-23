mkdir -p gradle/wrapper

wget --quiet --directory-prefix=gradle/wrapper https://github.com/moley/pike/blob/master/gradle/wrapper/gradle-wrapper.jar
wget --quiet --directory-prefix=gradle/wrapper https://github.com/moley/pike/blob/master/gradle/wrapper/gradle-wrapper.properties
wget --quiet https://github.com/moley/pike/blob/master/gradlew
chmod 777 gradlew
echo """ buildscript {
   repository {
     jcenter()
   }
   dependencies {
      classpath 'org.pike:pike:0.+'
   }
}
""" > build.gradle

clear

echo "Pike is installed in path `pwd`."
echo "Take a look at https://github.com/moley/pike/blob/master/README.md or start your first build with ./gradlew tasks to analyze the available tasks"
echo "Happy coding"
