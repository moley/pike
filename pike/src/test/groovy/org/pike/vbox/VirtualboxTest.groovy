package org.pike.vbox



/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
class VirtualboxTest {

/**



    private IMachine findOrCreateMachine (final IVirtualBox virtualbox, final String name, final String original) {

        IMachine machine = null
        try {
          machine = virtualbox.findMachine(name)
        } catch (VBoxException) {

            throw new RuntimeException(e)

            //IMachine originalMachine = virtualbox.findMachine(original)

 //println ("MediumAttachments: " + originalMachine.getMediumAttachments())
 //          machine = virtualbox.createMachine(null, name, originalMachine.groups, originalMachine.getOSTypeId(), "")
 //          originalMachine.cloneTo(machine, CloneMode.AllStates, new ArrayList<CloneOptions> () )
 //          virtualbox.registerMachine(machine)

        }

        return machine
    }


    @Test@Ignore
    public void test () throws Exception {



        VirtualBoxManager mgr = VirtualBoxManager.createInstance(null)


        String url = "http://localhost:18083"
        String user = "root"
        String passwd = "Momopomo351977"
        println ("Connecting")
        mgr.connect(url, user, passwd)
        println ("Connected")


        //IMachine machine = findOrCreateMachine(mgr.getVBox(), "hansdampfgug", "opensuse-11.2-x86")
        IMachine machine = findOrCreateMachine(mgr.getVBox(), "opensuse-11.2-x86", "opensuse-11.2-x86")
        println ("Found machine " + machine.name)

        ISession session = mgr.getSessionObject()



        mgr.startVm("opensuse-11.2-x86", null, 7000) //"headless", 7000)

        machine.getNetworkAdapter(0).StorageControllerByName()






        IDHCPServer server = mgr.getVBox().getDHCPServers().get(0)
        //mgr.getVBox().getDHCPServers().get(0).setConfiguration(server.IPAddress, server.networkMask, "192.168.56.108", "192.168.2.254")

        println ("Internal networks: " + mgr.getVBox().getInternalNetworks())
        println (mgr.getVBox().getDHCPServers().get(0).getIPAddress())
        println (mgr.getVBox().getDHCPServers().get(0).getLowerIP())
        println (mgr.getVBox().getDHCPServers().get(0).getUpperIP())
        println (mgr.getVBox().getDHCPServers().get(0).getNetworkMask())
        println (mgr.getVBox().getDHCPServers().get(0).getNetworkName())
        println ("IP: " + server.getUpperIP())






        println ("ID" +  machine.getId())

        while (true) {
          println("IP: "  + machine.getGuestPropertyValue("/VirtualBox/GuestInfo/Net/0/V4/IP"))
          println ("Bridged interface      : " + machine.getNetworkAdapter(0).getBridgedInterface())
          println ("HostOnly interface     : " + machine.getNetworkAdapter(0).getHostOnlyInterface())
          println ("HostIP(NAT)            : " + machine.getNetworkAdapter(0).getNATEngine().getHostIP())
          println ("vbox...internal: " + mgr.getVBox().internalNetworks)
          println ("" + mgr.getVBox().getDHCPServers().size() )



          Thread.sleep(1000)
        }
                  **/





  //         println ("Open machine")
        //mgr.getVBox().openMachine(null)
        //          println ("Machine opened")


        //ISession session = mgr.getSessionObject()



        //machine.launchVMProcess(session, "headless", "")



        //while (true) {

        //   String machineState = machine.getState().name()
        //   String sessionState = machine.getState().name()

        //    println ("States: MachineState = $machineState / SessionState = $sessionState)")
        //   Thread.sleep(1000)
        //  }

        //  machine.lockMachine(session,  LockType.Shared)
        //try {
        //       IConsole console = session.getConsole()
        //    IGuest guest = console.getGuest()

        //      IGuestSession guestSession = guest.createSession("bob","password", "", "")
        //     guestSession.processCreate("/usr/bin/firefox", null, null, null, 0L)



        // } finally {
        //    session.unlockMachine()
        // }






        //machine.unregister(CleanupMode.Full)  **/




        //}
}
