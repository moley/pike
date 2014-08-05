package org.pike.integration

import org.gradle.tooling.ProjectConnection
import org.junit.Test
import org.pike.test.TestUtils

/**
 * Created by OleyMa on 31.07.14.
 */
class StartRemoteBuildTaskTest {

    @Test
    public void startRemoteBuild () {

        ProjectConnection connection = TestUtils.gradleConnector.forProjectDirectory(new File ('testprojects/testprojectSimple')).connect()
        connection.newBuild().forTasks('configureRemote').run()


    }
}
