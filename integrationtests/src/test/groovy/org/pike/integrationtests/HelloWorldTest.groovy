package org.pike.integrationtests

import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.ResultHandler
import org.junit.Test


class HelloWorldTest {

    @Test
    public void buildHelloWorld () {
        File rootPath = TestUtils.getRootProject()
        File gradleWrapperProperties = new File (rootPath, 'gradle/wrapper/gradle-wrapper.properties')
        Properties properties = new Properties()
        properties.load(new FileInputStream(gradleWrapperProperties))
        String distributionUrl = properties.get("distributionUrl")
        GradleConnector gradleConnector = GradleConnector.newConnector()
        gradleConnector.forProjectDirectory(new File (rootPath, 'testprojects/helloworld'))
        gradleConnector.useDistribution(URI.create(distributionUrl))

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()

        ProjectConnection projectConnection = gradleConnector.connect()

        //if you use a different than usual build file name:
        BuildLauncher build = projectConnection.newBuild()
        build.forTasks('clean','build')

        try {

            build.setStandardOutput(byteArrayOutputStream)
            build.setStandardError(byteArrayOutputStream)
            build.run(new ResultHandler<Void>() {
                @Override
                void onComplete(Void aVoid) {
                    println "Completed"
                    println byteArrayOutputStream.toString()
                }

                @Override
                void onFailure(GradleConnectionException e) {
                    throw e
                }
            })
        }finally {
            projectConnection.close();
        }

    }

}
