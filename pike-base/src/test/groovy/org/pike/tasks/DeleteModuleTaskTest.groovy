package org.pike.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin


class DeleteModuleTaskTest {

    @Test
    public void deleteExistingModule() {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
            }
        }


        DeleteModuleTask deleteModuleTask = project.tasks.deletePike
        File clonepath = deleteModuleTask.clonePath
        clonepath.mkdirs()
        Assert.assertTrue (new File (clonepath, "README.md").createNewFile())

        deleteModuleTask.deleteGitModule()

        Assert.assertFalse ("ClonePath exists after deleting", clonepath.exists() )
    }


    @Test
    public void deleteNonExistingModule() {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
            }
        }
        DeleteModuleTask deleteModuleTask = project.tasks.deletePike
        deleteModuleTask.deleteGitModule()
    }
}
