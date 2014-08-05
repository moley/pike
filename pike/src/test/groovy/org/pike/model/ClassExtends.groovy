package org.pike.model

import org.pike.autoinitializer.NoAutoInitializing

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 24.04.13
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
class ClassExtends {

    String name
    String variable1
    String variable2

    @NoAutoInitializing
    Boolean createInstaller

    Boolean autoInitializedParameter

    ClassExtends parent
}
