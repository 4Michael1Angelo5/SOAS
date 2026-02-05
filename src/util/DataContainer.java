package util;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * This is a low level interface defining common behavior all data structures
 * used in the application will adhere to. This interface is meant to be a
 * replica of the Collections interface, but specifically gives tooling needed
 * to meet project requirements of handling, managing, and manipulating Seahawks
 * data for the SOAS application.
 * @author Chris C, Ayush.
 * @param <T> Specifies the type of objects this data container will hold
 */
public sealed interface DataContainer<T> extends Iterable<T>
        permits ArrayStore, SinglyLinkedList {

    // ================== getting ========================

    /**
     * @param theIndex the index of the element you wish to retrieve.
     * @return the element found at theIndex.
     * @throws IndexOutOfBoundsException if index exceeds size() or is
     * less than 0.
     */
    T get(int theIndex) throws IndexOutOfBoundsException;

    /**
     *
     * @return true if the DataContainer is empty and false otherwise.
     */
    boolean isEmpty();

    /**
     * @return the number of elements in the collection.
     */
    int size();

    /**
     *
     * @return true if the DataStructure Supports indexed based access and
     * false otherwise.
     */
    boolean supportsIndexedAccess();

    // ================== adding ========================

    /**
     * Adds an element to the collection/ list.
     * @param val the object to add to the list.
     */
    void add(T val);

    /**
     *
     * @param theIndex adds an element to the specified index
     * @param theVal the data object you wish to add to
     *               the collection at the specified index
     */
    void add(int theIndex, T theVal);

    // ================== removing ========================

    /**
     *
     * @param theVal the value of the data object to be deleted
     * @return the object that removed from the collection
     * @throws NoSuchElementException if the element is not in the Data Collection
     * @throws IllegalArgumentException if the DataContainer does not support
     * indexed based access.
     */
    T remove(T theVal) throws NoSuchElementException, IllegalArgumentException;

    /**
     * @return the removed object from the Data Storage Container.
     * @throws NoSuchElementException if the object is not present
     * in the container or the DataContainer is empty.
     */
    T remove() throws NoSuchElementException;

    /**
     * @return the removed object from the Data Storage Container.
     * @throws IndexOutOfBoundsException if the index is greater than or equal to
     * the current size, or if the index is less than 0.
     * @throws IllegalArgumentException if the DataContainer does not support
     * indexed based access beyond the front or end of the list.
     */
    T removeAt(int theIndex) throws IndexOutOfBoundsException, IllegalArgumentException;

    // ================== updating ========================
    /**
     *
     * @param theIndex the index of the data object you wish to update
     * @param theData the data you wish to update the current list with at
     *                the specified index.
     * @throws IllegalArgumentException if the DataContainer does not support
     * indexed based access beyond the front or rear of the list.
     * @throws  IndexOutOfBoundsException if the index greater than or equal to the current size or is less than 0.
     */
    void set(int theIndex, T theData) throws IllegalArgumentException, IndexOutOfBoundsException;


    // ================== searching ========================

    /**
     * @param thePredicate a predicate used to test elements for a match.
     * @return the index of the object if found and -1 otherwise.
     * @throws IllegalArgumentException if the DataContainer does not support
     * indexed based access beyond the front or end of the list.
     */
    int findBy(Predicate<T> thePredicate) throws IllegalArgumentException;


    /**
     * clears the all the data from the container.
     */
    void clear();

}
