package util;

import counter.OperationCounter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A HashTable implementation that uses chaining to resolve collisions.
 * @param <K> the class of the key object.
 * @param <V> the class of the value object.
 */
public final class HashTable<K,V> implements Dictionary<K,V>, Iterable<Entry<K,V>>, OperationCountable {

    /**
     * HashTable key values
     */
    private ArrayStore<SinglyLinkedList<Entry<K,V>>> myTable;

    /**
     * the object's class of the key needed for resizing if implemented
     */
    private final Class<K> myKeyClass;

    /**
     * the object's class of the value needed for resizing if implemented
     */
    private final Class<V> myValueClass;

    /**
     * HashTable's load factor = size/capcity
     * resize when load factor > 0.5
     */
    private double myLoadFactor;

    /**
     * current number of Entries in the HashTable.
     */
    private int size;

    private final OperationCounter myCounter = new OperationCounter();


    /**
     * Default constructor.
     * Creates a new HashTable with initial capacity of 16.
     * @param theKeyClass the class of the objects key
     * @param theValueClass the class of the objects value.
     */
    public HashTable(Class<K> theKeyClass, Class<V> theValueClass) {

        this(theKeyClass,  theValueClass, 16);
    }

    /**
     * Creates a new HashTable with {@code theInitialCapcity}
     * @param theKeyClass the class of the objects key
     * @param theValueClass the class of the objects value.
     * @param theInitialCapacity the initial capacity of the HashTable
     */
    @SuppressWarnings("unchecked")
    public HashTable(Class<K> theKeyClass, Class<V> theValueClass, int theInitialCapacity) {
        super();
        myTable =
                new ArrayStore<>(
                        (Class<SinglyLinkedList<Entry<K,V>>>) (Class<?>) SinglyLinkedList.class,
                        theInitialCapacity);
        initializeHashTable(myTable, theInitialCapacity);
        myKeyClass = theKeyClass;
        myValueClass = theValueClass;
    }

    // ======================  getters/ setters ===========================

    @Override
    public int size() {
        return size;
    }


    @Override
    public double loadFactor() {
        return myLoadFactor;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void updateLoadLoadFactor() {
        myLoadFactor = (double) size /myTable.size();
    }

    @Override
    public V get(K key) {
        // 1) hash the key and get the index
        int bucketIndex = getKeyIndex(key);
        SinglyLinkedList<Entry<K,V>> bucketList = myTable.get(bucketIndex);

        //2)
        // if it is not empty iterate over all entries in the bucket
        // and return it if present.
        for (Entry<K,V> entry: bucketList) {
            if (Objects.equals(entry.key(), key)) {
                return entry.value();
            }
        }

        // 3) if we didn't find it return null.
        return null;
    }

    // ======================  adding ===========================

    @Override
    public void put(K theKey, V theValue) {
        // 1) create new Entry.
        Entry<K,V> theNewEntry = new Entry<>(theKey, theValue);

        // 2) calculate hash index;
        int hashIndex = getKeyIndex(theKey);

        // 3) add the new Entry to the linked list at the hash index.
        SinglyLinkedList<Entry<K,V>> tableListEntry = myTable.get(hashIndex);

        // a) first check if the entry is already in the list and update it if it
        // is there.
        if (!wasAbleToUpdateListEntry(tableListEntry, theNewEntry) ) {
            // b) otherwise add the entry to the front of the bucket list.
            tableListEntry.addFront(theNewEntry);
            //4) update size only when the new entry is not already present.
            size++;
            //5) update load factor
            updateLoadLoadFactor();

            //6) resize if necessary
            if (myLoadFactor > 0.5) {
                resize();
            }
        }

    }

    // ======================  removing ===========================

    @Override
    public V delete(K key) {

        // 1) check if the key exists in the HashTable
        if (!containsKey(key)) {
            return null;
        }
        // 2) get bucket index of key
        int bucketIndex = getKeyIndex(key);
        SinglyLinkedList<Entry<K,V>> bucketList = myTable.get(bucketIndex);

        //3) remove the key from the bucket list
        Entry<K,V> removed = bucketList.remove(
                (entry)-> Objects.equals(entry.key(), key));
        size--;
        updateLoadLoadFactor();
        return removed.value();
    }

    @Override
    public boolean containsKey(K key) {
        // 1) get bucket index of key
        int bucketIndex = getKeyIndex(key);
        SinglyLinkedList<Entry<K,V>> bucketList = myTable.get(bucketIndex);

        for (Entry<K,V> entry: bucketList) {
            if (Objects.equals(entry.key(), key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Entry<K,V>> iterator() {

        return new Iterator<>() {

            int bucketIndex = 0;

            Iterator<Entry<K, V>> currentIterator = null;

            private void advanceToNext() {
                while ((currentIterator == null || !currentIterator.hasNext())
                        && bucketIndex < myTable.size()) {
                    SinglyLinkedList<Entry<K, V>> nextBucketList = myTable.get(bucketIndex++);
                    currentIterator = nextBucketList.iterator();
                }
            }

            @Override
            public boolean hasNext() {
                advanceToNext();
                return currentIterator != null && currentIterator.hasNext();
            }

            @Override
            public Entry<K, V> next() {
                advanceToNext();
                if (currentIterator == null || !currentIterator.hasNext()) {
                    throw new NoSuchElementException("Empty");
                }
                return currentIterator.next();
            }

        };

    }

    // ====================  util =====================

    public void clear() {
        int curCapacity = myTable.size();
        myTable.clear();
        size = 0;
        initializeHashTable(myTable, curCapacity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<K, V> entry : this) {
            sb.append("{");
            sb.append(entry.toString());
            sb.append("}, ");
        }

        return sb.toString();
    }


    // ====================   private helper methods =========================

    private void initializeHashTable(
            ArrayStore<SinglyLinkedList<Entry<K,V>>> theArrayTable,
            int theCapacity) {
        for (int i = 0; i < theCapacity; i++) {
            theArrayTable.add(i, new SinglyLinkedList<>());
        }
    }

    private int getKeyIndex(K theKey) {
        int theHashResult = Objects.hash(theKey);
        if (theHashResult < 0) {
            // theHashResult & 01111111111111111111111111111111
            // drops the signed bit and returns the positive result.
            // cannot just use Math.abs() because Math.abs(-2^31) will still be negative
            // because of overflow.
            theHashResult = theHashResult & 0x7FFFFFFF;
        }
        // return the hash result mod capacity.
        return theHashResult % myTable.size();
    }

    private boolean wasAbleToUpdateListEntry(
            SinglyLinkedList<Entry<K,V>> tableListEntry,
            Entry<K,V> theNewEntry) {

        for (Entry<K,V>  currentEntry : tableListEntry) {
            if (Objects.equals(theNewEntry.key(),currentEntry.key())) {
                currentEntry.setEntry(theNewEntry.value());
                return true;
            }
        }
        return false;
    }

    private void resize() {
        HashTable<K,V> temp = new HashTable<>(myKeyClass, myValueClass, myTable.size()*2);

        Iterator<Entry<K,V>> iterator = iterator();

        while(iterator.hasNext()) {
            Entry<K,V> entry = iterator.next();
            temp.put(entry.key(), entry.value());
        }

        myTable = temp.myTable;
        myLoadFactor = temp.loadFactor();
        size = temp.size;
    }

    @Override
    public int getSwaps() {
        return myCounter.getCount("swaps");
    }

    @Override
    public int getComparisons() {
        return myCounter.getCount("comparisons");
    }

    @Override
    public void resetCounter() {
        myCounter.resetAll();
    }
}
