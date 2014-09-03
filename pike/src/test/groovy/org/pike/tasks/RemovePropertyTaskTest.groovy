package org.pike.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.04.13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
public class RemovePropertyTaskTest {

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
        println (setPropertyHalloTask.getDetailInfo())
        setPropertyHalloTask.install()

        Assert.assertFalse (propFile.absolutePath + "was created by remove task", propFile.exists())


    }

    @Test
    public void testRemoveNonExistingProperty () {
        Project project = configureProject()

        Properties propSave = new Properties()
        propSave.setProperty("mister", "bean")
        Assert.assertTrue (propFile.getParent() + " could not be created", propFile.getParentFile().mkdirs())
        propFile.createNewFile()
        propSave.store(new FileOutputStream(propFile), "")

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        println (setPropertyHalloTask.getDetailInfo())
        setPropertyHalloTask.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        Assert.assertNull("property1 exists after removing a non existend property1, that's strange", valueProp1)

        String valueProp2 = prop.get("mister")
        Assert.assertNotNull("Value mister was removed", valueProp2)
        Assert.assertEquals ("Invalid value set", "bean", valueProp2)

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
        Assert.assertTrue (propFile.getParent() + " could not be created", propFile.getParentFile().mkdirs())
        propFile.createNewFile()
        propSave.store(new FileOutputStream(propFile), "")

        TestUtils.prepareModel(project)


        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        println (setPropertyHalloTask.getDetailInfo())
        setPropertyHalloTask.install()

        Properties prop = new Properties()
        prop.load(new FileInputStream(propFile))
        String valueProp1 = prop.get("property1")
        Assert.assertNull("Property1 not set at all", valueProp1)

    }



}

