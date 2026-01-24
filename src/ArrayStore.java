public class ArrayStore<T>{

    private T[] myData;
    final private Class<T> dataClass;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayStore(Class<T> theClass, int theCapacity){
        if (theCapacity <= 0) {
            throw new IllegalArgumentException("initial capacity must be positive");
        }
        this.dataClass = theClass;
        this.size = 0;
        this.myData = (T[]) java.lang.reflect.Array.newInstance(theClass,theCapacity);
    }

    public void add(T theData) {
        if (size == myData.length){
            resize();
        }
        myData[size++] = theData;
    }

    public T get(int theIndex) {
        if (theIndex >= size || theIndex < 0) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return myData[theIndex];
    }

    public T removeAtIndex(int theIndex) {

        T theItemRemoved = get(theIndex);

        for (int i = theIndex; i < size-1; i++) {
            myData[i] = myData[i+1];
        }
        myData[size-1] = null;
        size--;

        return theItemRemoved;
    }

    public void setData(int theIndex, T theData) {
        myData[theIndex] = theData;
    }

    public int size() {
        return size;
    }


    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newArray = (T[]) java.lang.reflect.Array.newInstance(dataClass, myData.length*2);
        System.arraycopy(myData, 0, newArray, 0, size);
        myData = newArray;
    }

    public void append(ArrayStore<T> theArray) {
        while (myData.length - size < theArray.size) {
            resize();
        }
        int index = 0;
        int newSize = size + theArray.size;
        for (int i = size; i < newSize; i++) {
            setData(i, theArray.get(index++));
        }
        size = newSize;
    }

}
