package util;

public class HashTable<K,V> implements MapTable<K,V> {

    /**
     * HashTable key values
     */
    ArrayStore<SinglyLinkedList<Entry<K,V>>> myTable;

    /**
     * the object's class of the key needed for resizing if implemented
     */
    Class<K> myKeyClass;

    /**
     * the object's class of the value needed for resizing if implemented
     */
    Class<V> myValueClass;

    double myLoadFactor;


    @SuppressWarnings("unchecked")
    public HashTable(Class<K> theKeyClass, Class<V> theValueClass) {
        super();
        SinglyLinkedList<V> chain = new SinglyLinkedList<>();
        myTable = new ArrayStore<>((Class<SinglyLinkedList<Entry<K,V>>>) (Class<?>) SinglyLinkedList.class);
    }

    @Override
    public void put(K theKey, V value) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public int size() {
        return myTable.size();
    }

    @Override
    public double loadFactor() {
        return myLoadFactor;
    }

    private static class Entry<K,V> {
        K key;
        V value;
        private Entry(K key, V value) {
            super();
            this.key = key;
            this.value = value;
        }
        private K key() {
            return key;
        }
        private V value() {
            return value;
        }
    }
}
