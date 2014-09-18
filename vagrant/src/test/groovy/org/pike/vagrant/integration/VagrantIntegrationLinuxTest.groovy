package org.pike.vagrant.integration

import org.pike.test.TestUtils

/**
 * Tests a complete walkthrough with the linux vm testhost in vagrant testproject
 */
public class VagrantIntegrationLinuxTest extends VagrantRoundtripTest {


    @Override
    protected File getProject() {
        return TestUtils.getTestproject('vagrant')
    }

    @Override
    protected boolean withLocal() {
        return true
    }

    @Override
    protected String getHost() {
        return "testhost"
    }

    @Override
    protected String getOs() {
        return "linux"
    }
}

