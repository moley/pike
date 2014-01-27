package org.pike.os

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 13.09.13
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOsProvider implements IOperatingsystemProvider {

    @Override
    String addPath(String path, String addPath) {
        return path + fileSeparator + addPath
    }
}
