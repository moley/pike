package org.pike.vagrant

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.cache.CacheManager
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.worker.DownloadWorker

/**
 * Created by OleyMa on 05.08.14.
 */
class CreateVmTask extends DefaultTask {

    Host host

    CacheManager cacheManager = new CacheManager()

    @TaskAction
    public void prepare () {
        Operatingsystem os = host.operatingsystem
        File hostDir = VagrantUtil.getWorkingDir(project, host)

        Vagrant vagrant = os.vagrant
        String vagrantBox = vagrant.boxUrl
        if (vagrantBox == null)
            throw new IllegalStateException("No vagrant box defined for host $host.name, skip preparing vm")

        DownloadWorker worker = new DownloadWorker()
        worker.from = vagrantBox
        worker.toPath = hostDir
        worker.operatingsystem = os
        worker.project = project
        worker.install()

        File boxFile = worker.downloadedFile
        File renamedFile = new File (boxFile.parentFile, "vm.box")
        boxFile.renameTo(renamedFile)

        /**project.exec {
            workingDir hostDir.absolutePath
            commandLine 'vagrant', 'box', 'remove', host.name
        }  **/

        project.exec {
            workingDir hostDir.absolutePath
            commandLine 'vagrant', 'box', 'add', host.name, renamedFile.absolutePath
        }


        //Create vagrant file with own ip
        File vagrantFile = new File (hostDir, 'Vagrantfile')
        vagrantFile.text = """# Vagrantfile created by vagrant plugin of pike
VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "$host.name"
  config.vm.network :private_network, ip: "$host.ip"
end
"""


    }
}
