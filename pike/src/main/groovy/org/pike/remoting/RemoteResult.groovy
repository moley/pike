package org.pike.remoting

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
class RemoteResult {

    String host

    String output

    public RemoteResult (final String host, final String output) {
        this.host = host
        this.output = output
    }

    public boolean isOk () {
        return ! output.contains("BUILD FAILED")
    }
}
