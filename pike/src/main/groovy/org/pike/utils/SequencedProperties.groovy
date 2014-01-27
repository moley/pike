package org.pike.utils

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class SequencedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    public SequencedProperties() {
    }

    public SequencedProperties(Properties defaults) {
        super(defaults);
    }

    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    public Set<Object> keySet() { return keys; }

    public Enumeration<Object> keys() {
        return Collections.<Object> enumeration(keys);
    }

    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
}