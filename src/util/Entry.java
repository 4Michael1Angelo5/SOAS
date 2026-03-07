package util;

/**
 * A kay value paired object.
 * @param <K> the key
 * @param <V> the value
 * @author Chris Chun, Ayush.
 * @version 1.1
 */
public class Entry<K,V>{

    private final K key;
    private V value;
    public Entry(K key, V value) {
        super();
        this.key = key;
        this.value = value;
    }
    public K key() {
        return key;
    }
    public V value() {
        return value;
    }
    void setEntry(V theValue) {
        value = theValue;
    }

    @Override
    public String toString() {
        return key + " -> " + value;
    }

}
