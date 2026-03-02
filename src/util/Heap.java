package util;

public interface Heap<T> {
    void insert(T item);
    T peek();
    T extract();
}
