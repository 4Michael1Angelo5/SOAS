package util;

/**
 * Interface defining common queue methods
 * @param <T> the type of data the Queue will hold.
 * @version 1.2
 * @author Chris Chun, Ayush
 */
public interface Queue<T> {
    void enqueue(T val);
    T dequeue();
    T front();
}
