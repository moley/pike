package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.pike.model.host.Host
import org.pike.remoting.SshRemoting

/**
 * Created by OleyMa on 07.08.14.
 */
@Slf4j
class VagrantRemoting extends SshRemoting {

    @Override
    public String getUser (Host host, Project project) {
        return 'vagrant'
    }

    @Override
    public String getPassword (final Host host, final Project project) {
        return 'vagrant'
    }
}
