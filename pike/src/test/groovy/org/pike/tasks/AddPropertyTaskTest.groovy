package org.pike.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.worker.PikeWorker

/**
 * tests for adding property to a propertyfile
 */
class AddPropertyTaskTest {

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
        worker.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        Assert.assertNotNull("Value not set at all", valueProp1)
        Assert.assertEquals ("Invalid value set", "value1", valueProp1)

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
        propSave.setProperty("mister", "bean")
        Assert.assertTrue (propFile.getParent() + " could not be created", propFile.getParentFile().mkdirs())
        propFile.createNewFile()
        propSave.store(new FileOutputStream(propFile), "")

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        PikeWorker worker = TestUtils.getWorker(setPropertyHalloTask)
        worker.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        Assert.assertNotNull("Property1 not set at all", valueProp1)
        Assert.assertEquals ("Invalid value set at property1", "value1", valueProp1)

        String valueProp2 = prop.get("mister")
        Assert.assertNotNull("Mister not merged", valueProp2)
        Assert.assertEquals ("Invalid value set at mister", "bean", valueProp2)

    }



}
