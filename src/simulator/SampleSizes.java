package simulator;


/**
 * An enum class to allow users to specify which data set size
 * they would like to use e.g. 50, 500, 5000 - drills, players, etc.
 * @author Chris Chun, Ayush.
 * @version 1.1
 */
public enum SampleSizes {
    // 1. Define the constants with their associated integers
    SMALL(50),
    MEDIUM(500),
    LARGE(5000);

    // 2. This variable stores the 50, 500, or 5000
    private final int size;

    // 3. The constructor that maps the number to the name
    SampleSizes(int size) {
        this.size = size;
    }

    // 4. A getter so you can use the number in your logic
    public int getSize() {
        return this.size;
    }
}