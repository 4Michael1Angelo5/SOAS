package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SinglyLinkedList<T> implements Iterable<T>{

    /**
     * No Such Element Exception error.
     */
    private static final String NO_SUCH_ELEMENT_ERR = "Cannot remove from empty list";

    /**
     * Illegal Argument Exception error message.
     */
    private static final String ILLEGAL_ARG_ERR = "Index must be greater than or equal to 0 but less then ";
    /**
     * Head of the Singly Linked List
     */
    private Node<T> head;

    /**
     * Tail of the Singly Linked List
     */
    private Node<T> tail;

    /**
     * How many nodes are in the list.
     */
    private int size;

    // constructor
    public SinglyLinkedList() {
        head = tail = null;
    }

    /**
     * Gets the number of nodes currently in the list.
     * @return the number of nodes in the list.
     */
    public int size() {
        return this.size;
    }

    /**
     *
     * @param theIndex the index of the node in the Singly Linked List
     * @return the value stored in the node at position 'theIndex'.
     */
    public T get(int theIndex) {

        if (theIndex > size -1 || theIndex < 0) {
            throw new NoSuchElementException(NO_SUCH_ELEMENT_ERR + size);
        }

        Node<T> walker = head;
        int steps = 0;
        while (steps < theIndex) {
            walker = walker.next;
            steps++;
        }

        return walker.val;
    }

    /**
     * Add a new Node to the list at the front.
     * @param item the item you want to add to the list.
     */
    public void addFront(T item) {
        Node<T> newNode = new Node<>(item);

        if (size == 0) {
            head = tail = newNode;
        }else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    /**
     * Appends a Node to the end of the list.
     * @param theVal the value of the node you wish to add.
     */
    public void addRear(T theVal) {
        Node<T> newNode = new Node<>(theVal);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     *
     * @param theIndex the index of node after insertion.
     * @param theVal the value of the node you want to add into the list.
     */
    public void addAtIndex(int theIndex, T theVal) {

        Node<T> newNode = new Node<>(theVal);

        // 1) Inserting in an invalid place
        if (theIndex > size || theIndex < 0) {
            throw new IllegalArgumentException(ILLEGAL_ARG_ERR + size);
        } else
        // 2) Inserting at the end
        if (theIndex == size) {
            this.addRear(theVal);
            return;
        } else
        // 3) inserting in the front
        if (theIndex == 0) {
            this.addFront(theVal);
            return;
        } else {
            // 4) Inserting somewhere in between head and tail

            Node<T> walker = head;
            int steps = 0;
            while( steps < theIndex - 1) {
                walker = walker.next;
                steps++;
            }
            // now prev sits on the node right before the one where we need to place
            // the new node

            // 5) addAtIndex the node and update pointers
            newNode.next = walker.next;
            walker.next = newNode;
        }

        // 6) update the size
        size++;
    }

    /**
     * Removes a node from the front of the list.
     * @return the value of removed node.
     */
    public T remove() {
        if (head == null) {
            throw new NoSuchElementException(NO_SUCH_ELEMENT_ERR);
        }
        T theNodeValueRemoved = head.val;
        head = head.next;
        size--;

        if (size == 0){
            tail = null;
        }
        return theNodeValueRemoved;
    }

    public T remove(int theIndex){

        T theRemovedVal;

        // 1) handle exceptions
        if (head == null) {
            throw new NoSuchElementException(NO_SUCH_ELEMENT_ERR);
        }
        if (theIndex > size - 1 || theIndex < 0) {
            throw new IllegalArgumentException(
                    ILLEGAL_ARG_ERR + size
            );
        }

        // 2) removal from front
        if (theIndex == 0) {
            return  this.remove();
        }

        // 3) remove somewhere between head and tail
        else {
            Node<T> walker = head;
            int curIndex = 0;

            while (curIndex < theIndex - 1) {
                walker = walker.next;
                curIndex++;
            }

            // now walker is sitting right before the node we want to remove
            theRemovedVal = walker.next.val;

            // 4) update tail if index = size - 1
            if (walker.next == tail) {
                tail = walker;
            }

            // 6) remove the node
            walker.next = walker.next.next;
        }

        // 7) decrement
        size--;

        return theRemovedVal;
    }

    /**
     * Removes the first matching item from the linked list or
     * throws an exception if the item is not present. This method
     * uses the object's `.equals()` method to compare for equality to
     * determine equality.
     * @param item the item you wish to remove the list.
     * @return the item removed.
     */
    public T remove(T item) {

        T itemRemoved;

        // 1) check if list is empty
        if (size == 0) {
            throw new NoSuchElementException(NO_SUCH_ELEMENT_ERR);
        }

        // 2) check if we are removing the head
        Node<T> walker = head;
        if (Objects.equals(item,walker.val)) {
            return this.remove();
        }

        // 3) try to find the item.
        while (walker.next != null) {

            // use Object.equals to guard against null pointer
            if (Objects.equals(item, walker.next.val)){

                // update tail if we are removing it.
                if (walker.next == tail) {
                    tail = walker;
                }

                itemRemoved = walker.next.val;
                walker.next = walker.next.next;

                // decrement size
                size--;
                return itemRemoved;
            }

            walker = walker.next;
        }

        throw new NoSuchElementException("Can't find item");
    }

    /**
     * Searches a linked list for the item and if it is present
     * returns it's index position or returns -1 otherwise.
     * @param theItem the item to search for in the linked list.
     * @return the index of the item if present or -1 otherwise.
     */
    public int indexOf(T theItem) {
        Node<T> walker = head;
        int idx = 0;
        while(walker != null) {
            if (Objects.equals(walker.val, theItem)) {
                return idx;
            }
            idx++;
            walker = walker.next;
        }
        return -1;
    }

    /**
     * reset the list to empty.
     */
    public void reset() {
        head = tail = null;
        size = 0;
    }



    /**
     * Converts a linked list into an array list.
     * @param theClass the class of objects the array will contain.
     * @return An array list version of the linked list.
     */
    public ArrayStore<T> toArrayStore(Class<T> theClass){

        ArrayStore<T> array = new ArrayStore<>(theClass, 16 );

        Node<T> walker = head;

        while(walker != null) {
            array.add(walker.val);
            walker = walker.next;
        }

        return array;
    }

    /**
     *
     * @return A String representation of the LinkedList
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> walker = head;
        while (walker != null) {
            sb.append(walker);
            sb.append(" -> ");
            walker = walker.next;
        }
        sb.append("null");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T value = current.val;
                current = current.next;
                return value;
            }
        };
    }

    /**
     * Node class for singly linked list.
     * @param <T> the type of data the node will store.
     */
    private static class Node <T> {
        public T val;
        public Node<T> next;
        public Node(T val){
            super();
            this.val = val;
            this.next = null;
        }

        public Node() {
            super();
            this.val = null;
            this.next = null;
        }

        @Override
        public String toString(){
            return this.val.toString();
        }

    }

public static void main(String[] args) {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.addRear(0);
    list.addRear(1);
    list.addRear(2);
    list.addRear(3);
    list.addRear(4);
    System.out.println(list);
    System.out.println("expected size: 5, Actual size:" + list.size);
    list.addAtIndex(2, 100);
    System.out.println("expected: 0 -> 1 -> 100 -> 2 -> 3 -> 4 -> null");
    System.out.println(("actual : " + list));
    list.remove(2);
    System.out.println("expected: 0 -> 1 -> 2 -> 3 -> 4 -> null");
    System.out.println(("actual : " + list));

    SinglyLinkedList<String> list2 = new SinglyLinkedList<>();
    list2.addRear("A");
    list2.addRear("B");
    list2.addRear("C");
    list2.addRear("D");
    list2.addAtIndex(4, "E");

    System.out.println("Expected: " + 5);
    System.out.println("Actual: " + list2.size);

    System.out.println(list2);
}
}

