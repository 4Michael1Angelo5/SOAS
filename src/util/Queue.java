package util;

public interface Queue<T> {
    void enqueue(T val);
    T dequeue();
    T front();
}
