package org.pike.autoinstall

import com.sshtools.j2ssh.transport.HostKeyVerification
import com.sshtools.j2ssh.transport.TransportProtocolException
import com.sshtools.j2ssh.transport.publickey.SshPublicKey


/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 29.04.13
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class AlwaysTrueHostKeyVerification implements HostKeyVerification{
    @Override
    boolean verifyHost(String s, SshPublicKey sshPublicKey) throws TransportProtocolException {
        return true
    }
}
