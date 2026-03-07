package util;

/**
 * Interface for HashTable
 * @param <K> the key.
 * @param <V> the value.
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public interface MapTable<K,V> {

    void put(K theKey,V value);

    /**
     *
     * @param key the key
     * @return the value associated with the key in the HashTable.
     */
    V get(K key);

    /**
     *
     * @param key the key of the object to remove.
     * @return the value of the key for the removed obj if found,
     * or null if the object is not found in the HashTable.
     */
    V delete(K key);

    /**
     *
     * @param key the key of the object in the HashTable.
     * @return true if found and false otherwise.
     */
    boolean containsKey(K key);

    /**
     *
     * @return the number of key value pairs in the HashTable.
     */
    int size();

    /**
     *
     * @return the load factor of the HashTable.
     */
    double loadFactor();

    boolean isEmpty();
}
