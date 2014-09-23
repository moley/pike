package org.pike.worker

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.tasks.DelegatingTask

/**
 * Tests adding of properties in propertyfile
 */
class PropertyWorkerAddTest {

    String user = System.getProperty("user.name")
    File propFile = new File ("tmp/home/${user}/swarm/tools/hallo.properties").absoluteFile

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
                    add ("property1", "value1")
                }
            }
        }
        return project
    }
    @Test
    public void testNew () {
        Project project = configureProject()

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        PikeWorker worker = TestUtils.getWorker(setPropertyHalloTask)
        println (worker.detailInfo)
        worker.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        assertNotNull("Value not set at all", valueProp1)
        assertEquals ("Invalid value set", "value1", valueProp1)

    }



    @Before
    public void before () {
        File tmp = new File ("tmp")
        if (tmp.exists())
            FileUtils.deleteDirectory(tmp)
    }


    @Test
    public void testUpdate () {
        Project project = configureProject()

        Properties propSave = new Properties()
        propSave.setProperty('mister', 'bean')
        assertTrue (propFile.getParent() + " could not be created", propFile.getParentFile().mkdirs())
        propFile.createNewFile()
        propSave.store(new FileOutputStream(propFile), "")

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ('installTestenv')
        PikeWorker worker = TestUtils.getWorker(setPropertyHalloTask)
        println (worker.detailInfo)
        worker.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        assertNotNull("Property1 not set at all", valueProp1)
        assertEquals ("Invalid value set at property1", "value1", valueProp1)

        String valueProp2 = prop.get("mister")
        assertNotNull("Mister not merged", valueProp2)
        assertEquals ("Invalid value set at mister", "bean", valueProp2)

    }



}
