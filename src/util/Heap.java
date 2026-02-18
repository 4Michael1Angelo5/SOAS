package util;

public interface Heap<T> {
    void insert(T item);
    T peek();
    void heapifyUp();
    void heapifyDown();
    T extract();
}
