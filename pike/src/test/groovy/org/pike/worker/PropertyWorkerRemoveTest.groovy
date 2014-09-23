package org.pike.worker

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import org.pike.tasks.DelegatingTask
import org.pike.test.TestUtils

import static org.junit.Assert.*

/**
 * Tests removing of properties in propertyfile
 */
public class PropertyWorkerRemoveTest {

    File propFile = new File ("tmp/home/user/hallo.properties").absoluteFile

    private Project configureProject () {
        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            rootpath = '${currentPath}/tmp'
            currentHost = 'myhost'
        }
        project.operatingsystems {
            linux {
                homedir = '/home/${user}'
                programdir = "${homedir}/swarm/tools"
            }
        }

        project.hosts {
            myhost {
                operatingsystem = project.operatingsystems.linux
                environment 'testenv'
            }
        }

        project.environments {
            testenv {
                property {
                    file = "${operatingsystem.programdir}/hallo.properties"
                    remove ("property1")
                }
            }
        }
        return project
    }

    @Test
    public void testRemovePropertyFromNonExistingFile () {
        Project project = configureProject()

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        PikeWorker workerTask = TestUtils.getWorker(setPropertyHalloTask)
        println (workerTask.detailInfo)
        workerTask.install()

        assertFalse (propFile.absolutePath + "was created by remove task", propFile.exists())
    }

    @Test
    public void testRemoveNonExistingProperty () {
        Project project = configureProject()

        Properties propSave = new Properties()
        propSave.setProperty("mister", "bean")
        assertTrue (propFile.getParent() + " could not be created", propFile.getParentFile().mkdirs())
        propFile.createNewFile()
        propSave.store(new FileOutputStream(propFile), "")

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        PikeWorker workerTask = TestUtils.getWorker(setPropertyHalloTask)
        println (workerTask.detailInfo)
        workerTask.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        assertNull("property1 exists after removing a non existend property1, that's strange", valueProp1)

        String valueProp2 = prop.get("mister")
        assertNotNull("Value mister was removed", valueProp2)
        assertEquals ("Invalid value set", "bean", valueProp2)

    }

    @Before
    public void before () {
        File tmp = new File ("tmp")
        if (tmp.exists())
            FileUtils.deleteDirectory(tmp)
    }


    @Test
    public void testRemoveExistingProperty () {
        Project project = configureProject()

        Properties propSave = new Properties()
        propSave.setProperty("mister", "bean")
        assertTrue (propFile.getParent() + " could not be created", propFile.getParentFile().mkdirs())
        propFile.createNewFile()
        propSave.store(new FileOutputStream(propFile), "")

        TestUtils.prepareModel(project)


        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        PikeWorker workerTask = TestUtils.getWorker(setPropertyHalloTask)

        println (workerTask.detailInfo)
        workerTask.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        assertNull("Property1 not set at all", valueProp1)

    }



}

