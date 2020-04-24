package org.pike.integrationtests

import org.gradle.tooling.*
import org.junit.Test

class MirrorEclipseTest {

    @Test
    public void buildDevelopment () {
        File rootPath = TestUtils.getRootProject()
        File gradleWrapperProperties = new File (rootPath, 'gradle/wrapper/gradle-wrapper.properties')
        Properties properties = new Properties()
        properties.load(new FileInputStream(gradleWrapperProperties))
        String distributionUrl = properties.get("distributionUrl")
        GradleConnector gradleConnector = GradleConnector.newConnector()
        gradleConnector.forProjectDirectory(new File (rootPath, 'testprojects/eclipseAdmin'))
        gradleConnector.useDistribution(URI.create(distributionUrl))

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()

        ProjectConnection projectConnection = gradleConnector.connect()

        //if you use a different than usual build file name:
        BuildLauncher build = projectConnection.newBuild()
        build.forTasks('clean', 'mirrorEclipse')
        build.addArguments('-s')

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
                    throw new IllegalStateException("Build was not successful\nOutput:" + byteArrayOutputStream.toString(), e)
                }
            })
        }finally {
            projectConnection.close();
        }

    }

}
