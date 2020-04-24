package org.pike.utils


class ProcessWrapper {


    public ProcessResult execute (String [] commands) {

        ProcessResult result = new ProcessResult()

        Process unmountprocess = commands.execute()
        def unmountout = new StringBuffer()
        def unmounterr = new StringBuffer()
        unmountprocess.consumeProcessOutput( unmountout, unmounterr )

        int returnCodeUnmount = unmountprocess.waitFor()

        result.error = unmounterr
        result.output = unmountout
        result.resultCode = returnCodeUnmount

        return result


    }

}
