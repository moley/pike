package org.pike.resolver

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 22.04.13
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
class ResolveException extends RuntimeException{

    private Collection<ResolveItem> nonresolvableItems = new ArrayList<ResolveItem>()

    public ResolveException (final String message, final Collection<ResolveItem> nonresolvableItems) {
        super (message)
        this.nonresolvableItems = nonresolvableItems
    }

    public Collection<ResolveItem> getNonResolableItems () {
        return nonresolvableItems
    }

}
