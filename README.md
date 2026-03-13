##  Seahawks Operations & Analytics System (SOAS)
The SOAS app is a simple CLI stats analysis application that parses Seahawks data from CSV files and provides the user with interesting ways to interact with the data.

## Features
* The app tracks how many operations the Heapify parsing algorithm performs and generates a report.
* Benchmark timer tracks algorithm run time in milliseconds.
* CLI-driven menu to navigate application options and stylized logger for improved UX.
* CSV parsing optimized using a `BufferedReader`.



## Team Information
* **Chris Chun**
* **Ayush**

---

## Role Assignments

| Role | Member(s) | Primary Responsibilities                                                                                                                                     |
| :--- | :--- |:-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Implementer: Core Logic** | Chris | Implemented the `MapManager`, `HashTableBenchMark`, and `HashableManager`                                                                                    |
| **Tester: JUnit Tests** | Ayush | Ayush designed the Junit 5 test suite for the `HashTableTest` class                                                                                          |
| **Analyst: Benchmark + Analysis** | Chris and Ayush | Chris implemented the Results and `OperationCounter`. Ayush analyzed the results, and documented the performance of the hash table operations in the README. |
---

## Analysis Section:

### Collision Strategy:
We chose to use separate chaining using SinglyLinkedList as our collision strategy. 
For this strategy, each bucket holds a linked list of entries. When two keys
hash to the same bucket index, the new entry is prepended to the front of that 
bucket's linked list, and lookup traverses the chain until the matching key is found.

### Hash Function:
We used the **division method**. The hash function computes the bucket index using 
the following steps:

1. Call `Objects.hash(key)` on the player ID
2. Strip the sign bit using `& 0x7FFFFFFF` to guarantee a positive number
3. Take the result mod the table capacity to get the bucket index
```
index = (Objects.hash(playerId) & 0x7FFFFFFF) % capacity
```

Since `Objects.hash()` calls `hashCode()` internally for integers, and `hashCode()` 
returns the integer value itself, this simplifies to:
```
index = playerId % capacity
```

### Benchmark Testing:


---
### 1. How did collision frequency change as size increased?

The collision count stayed zero for all dataset sizes (50, 500, and 5000). 
It means, everytime we add more players to the hash table, the keys still mapped
to different buckets instead of putting it in same spot. Because the player IDs are
in sequential order, they spread out evenly across the table when the hash 
function is calculating the bucket index.

Another reason collisions stayed at zero was because the table resizes when the load 
factor exceeded 0.75. Thats keept the table from becoming too full and it helped maintain
enough buckets for the keys. Since the table kept on expanding when more data was added,
it prevented multiple keys from being placed in the same bucket.

Because of this, the chaining mechanism using the linked list was never really used 
during these tests. Each bucket only had one entry, so the operations didn’t have 
to search through a chain. This represents the best-case behavior for a hash table,
where operations like add and remove run in constant (O(1)) time.

---
### 2. How did load factor affect runtime?

Our table started with a initial capacity of 16 buckets. As we kept inserting players, the 
load factor kept climbing until it hit 0.75, at that point the table doubled its 
size and rehashed everything into the new table. This happened multiple times across 
each dataset.

By the time all 50 players were inserted, the table had grown to a capacity of 128 
and the load factor settled at 0.390625. For 500 players it grew to 1024 with a load 
factor of 0.4882. For 5000 players it grew all the way to 8192 with a load factor of 
0.6103.

Looking at the insert times, it was 0.089ms for 50 players, 0.064ms for 500 players, 
and 0.322ms for 5000 players. The jump at 5000 is because the table had to rehash 
multiple times going from capacity 16 all the way up to 8192. Every rehash moves all 
the existing entries into the new table, which adds up over time.

Search and remove also got a bit slower as the dataset grew. Search went from 0.027ms 
to 0.058ms to 0.159ms, and remove went from 0.066ms to 0.096ms to 0.244ms. But since 
there were zero collisions the whole time, every search and remove just went directly 
to the right bucket in one step. The slowdown was from the table being bigger, not 
from having to search through the chains.

This is worth noting, that the load factor showed 0.0 for all remove rows in our 
results. That is because we capture the load factor after all the players have been 
removed, so the table is empty at that point.

---
### 3. Did results match expected O(1) behavior?

Yes, the results did match what we were expecting. Looking at the numbers, when we went from 
50 players to 5000 players, that was a 100x increase in data. But the times did not 
go up 100x.

For search, it went from 0.027ms to 0.158ms. That is only about a 6x increase for 
100x more data. For remove, it went from 0.057ms to 0.241ms, which is about a 4x 
increase. If the hash table was slow like a simple array scan, those numbers would 
have gone up way more.

Insert was bit different because it jumped from 0.089ms to 0.295ms. The jump at 5000 
was mostly due to the table resizing when the load factor approached 0.5. That 
resize moves all the entries into a bigger table, which adds extra work. Without that, the 
insert would have stayed more consistent across all three sizes.

---
### 4. At what load factor did performance degrade?

We started to see performance slow down as the load factor got above 0.39 and 
pushed toward 0.61 at size 5000. The biggest jump was at size 5000 where insert 
went from 0.064ms to 0.322ms.

The slowdown was not because buckets were getting crowded with multiple players. 
Collisions stayed at zero the whole time. The real cause was the rehashing. Every 
time the load factor crossed 0.75, we had to double the table size and move all the 
existing entries into the new table. For 5000 players, that happened multiple times 
as the table grew from 16 all the way to 8192. All that moving adds up.

Search and remove were not hit as hard because they do not cause any resizing. 
Search went from 0.027ms to 0.159ms and remove went from 0.066ms to 0.244ms across 
all three sizes. The increase there is just from the table being bigger in general, 
not from any real degradation in lookup speed.

---
### 5. What tradeoffs exist between chaining and probing?

Discuss the advantages and disadvantages of different collision resolution strategies:
- **Chaining**
  - Pros: 
      - It handle a lot of collisions without breaking the table.
      - The table can store more elements than the number of buckets because each bucket can hold a linked list.
      - Insertions and deletions are easier since elements can just be added or removed from the list.
  - Cons:
      - It uses extra memory for the linked list nodes and pointers.
      - Searching can be slower, if a bucket ends up with a long chain.
      - It can have bad cache performance because the linked list nodes are not stored next to each other in memory.
  
- **Probing (Open Addressing)**
  - Pros:
      - Everything is stored directly in the table, so it uses less memory.
      - It often has better cache performance because the data is stored in one continuous array.
      - Searches are very fast when the load factor is low.
  - Cons:
      - Performance can drop quickly if the table gets too full.
      - Collisions can create clustering that slow down searching.
      - Deleting elements is harder and sometimes requires tombstone markers.

We went with chaining because we already had a `SinglyLinkedList` built from PA2, 
so it made sense to reuse it here. The deletions was easier since we just remove 
the node from the list and go to the next. We also didn't need to leave any markers
behind like probing requires. Because the system needed to constantly add, search,
and remove players during a live game, chaining was the easiest and most efficient choice for us.

---
### 6. Compare performance with PA1 (array) and PA2 (linked list) by filling the following table:

| Structure | Lookup Time | Memory (space + overhead) | Best Use Case |
|----------|-------------|-------------|-------------|
| Array (PA1) | O(n) ~0.2ms at 5000 |  Low, all stored together in memory | Small datasets, index-based access |
| Linked List (PA2) ~189.3ms at 5000| O(n) | Higher, each node has a pointer | Frequent insert and deletions |
| Hash Table (PA5) | O(1) ~0.159ms at 5000 | Higher, buckets and linked lists | Fast lookup by key, large datasets |

In PA1 and PA2, finding a player by ID would check each entry one by one until we found the right one.
For 5000 players, that can take up to 5000 checks in the worst case, and that's really slow. With the hash 
table in PA5, we take the player ID and use the hash function to go straight to the correct bucket. In
most cases, this will only takes one step, so the lookup time is about O(1) on average. That is why the
hash table works best here. The Seahawks analytics team would need player stats quickly during a game, 
and the hash table allows us to find players much faster than using an array or a linked list.

---

# Reflection and Team Process

The design choices the team implemented in PA3, using a dependency injection strategy into the
`DataManager` proved to be worth its weight in gold. This decoupled the `DataManager` from its
Data Structure storage strategy, allowing it to be `DataContainer` agnostic and flexible. This
meant the team did not need to implement two different drill managers, one backed by a queue and
one backed by a priority queue, to conduct benchmark testing. We simply were able to instantiate
two different instances of the same manager, one supplied with a queue and one supplied with a
priority queue. This saved the team time and expedited iteration and collaboration, freeing up
the team to tackle more interesting statistical analysis.

The team used the extra time to pursue interdisciplinary collaboration. Chris and Ayush
collaborated with Chris's Statistics professor, Dr. Kmail, to draw more insights into how to
make sense of the simulation results. Chris and Ayush felt that the average wait time was not
statistically interesting. Dr. Kmail showed us that a more statistically significant metric
would be to compare the changes in the average wait times using a Z-score, which tracks how
many standard deviations an individual drill was from the mean wait time. This allowed us to
standardize the data so that when we compared changes in wait time, differences were normalized.
For example, if a drill waits 40 minutes, it is hard to tell by itself if that is statistically
significant. Transforming the changes to a Z-score shows directly how significant the change
was. While we did not observe any extreme outliers of z-scores above 3, we did notice
significant volatile changes in the wait time of z-scores for the top and bottom 1%.

Using the Z-score revealed that our data showed signs of a typical normal distribution. For
every drill that experienced an extreme negative change in wait time, there was a corresponding
positive change in wait time. These results showed us that although it does not change the total
time to process all the drills, it redistributes fairness, causing huge swings in wait time, so
For every drill that is rewarded, there is a corresponding drill that was punished.

Another great aspect of the team's architectural choices was to design the `DrillManager` and
the `PriorityQueue` to accept a comparator to reorder the heap. The benefit of this was that
the team could compare and contrast different sorting strategies to see how average wait time
and z-scores were affected.

While designing a `PriorityQueue` that supports a custom comparator was straightforward,
integrating it into the `DrillManager` presented structural challenges. Because the
`DrillManager` is decoupled from its `DataContainer`; the specific implementation type is not
known at compile time. We implemented type-safety 'guard rails' to verify the container type
before exposing APIs that would otherwise risk runtime errors.

Furthermore, since the abstract `DataManager` owns the `DataLoader`, we had to ensure the
`Comparator` remained synchronized across both. Without updating the `PriorityQueue` supplier
within the `DataLoader`, subsequent CSV imports would have reverted the `DrillManager` to its
initial default sorting logic rather than respecting the newly assigned comparator.

With the machinery in place, the team eagerly tested the flexibility of their new design.
Perhaps one of the most fascinating discoveries was that while most sorting strategies resulted
in longer average wait time, there was one sorting strategy that absolutely dominated in terms
of throughput efficiency — sorting by shortest duration first. By scheduling drills by lowest
duration, the team observed for sample sizes of 5000, the average wait time for the Queue was
34,797.50 minutes, but the average wait time for the `PriorityQueue` was 28,787.27 minutes.
Meaning by scheduling drills by shortest duration, the priority queue saved roughly 6,010
minutes — or 4 days, 4 hours, and 10 minutes in average wait time! These results are
fascinating because while the total time to process all the drills remained unchanged, the
average wait time was vastly improved by scheduling shortest-job-first.


---

## Project Structure
The project is organized with separate source and test roots to maintain clean code separation:

* **src/**: Contains production source code.
    * benchmark/
        * `Benchmark` interface for defining `BenchmarkRunner` contract
        * `BenchmarkRunner`: responsible for benchmark speed testing
    * counter/
        * `Counter` interface for defining `OperationCounter` contract
        * `OperationCounter`: responsible for counting algorithm operations, i.e., swaps, assignments, comparisons
    * loader/
        * `Loader` interface for defining `DataLoader` contract
        * `DataLoader.java`: Core logic for file reading and generic data parsing.
    * manager/
      * `DataManager` abstract class defining common behavior to all future managers, i.e., DrillsManager, Transaction 
            Manager/ TransactionFeed, etc.
      * `MapManager`: abstract parent class defining common behavior to all future managers using HashTable-based storage, i.e.,
            PlayerManager, etc.
      * `RosterManager`: concrete child class of `DataManager` that brings specific functionality needed to manage the Seahawks roster.
      * `TransactionFeed`: concrete child class of `DataManager` that brings specific functionality needed to manage the Seahawks transactions.
      * `UndoManager`:  concrete child class of `DataManager` that brings specific functionality needed to undo actions from the other managers.
      * `FanTicketQueue`: concrete child class of the `DataManager` that brings specific functionality needed to manage the Seahawks fan ticket line.
      * `DrillManager`: concrete child class of the `DataManager` that brings specific functionality needed to manage Seahawks Drills.
      * `PlayerManager`: concrete child class of `MapManager` that brings specific functionality needed to manage Seahawks players.
      * `HashableManager`: interface defining contract for all managers using HashTable-based storage.
      * `Manager`: interface defining contract for all managers using DataContainer-based storage.
    * results/
        * `ExperimentResults.java`: A simple Record class used to report a specific result from a benchmark test. 
        * `Results.java`: Abstract parent class defining all common behavior to concrete Benchmark Results classes: `FanTicketResults`, `RosterResults`, `TransactionResults`, `UndoResults`. It automates experiments across 50, 500, and 5000 records,
        * `FanTicketResults`, `RosterResults`, `TransactionResults`, `UndoResults`: Concrete child classes of the `Results` class that handle specific testing needed for managing their respective `DataTypes`.
        * `Experiment.java`: Interface defining contract for all Results classes.
        * calculating the average execution time (ms) for Add, Remove, and Search operations.
        * `HashTableBenchMark`: Abstract parent class defining common behavior for all HashTable-based benchmark experiments, automating setup, execution, and results display across PlayerResults and future hash-based benchmarks.
        * `PlayerResults`: Concrete child class of HashTableBenchMark that handles specific benchmark testing for PlayerManager using HashTable across 50, 500, and 5000 player datasets.
    * types/
        * `DataType`: Sealed interface that ensures all data managed by the system has a consistent identity.
        * `Player.java`, `Drill.java`, `Transaction.java`, `Action.java`, `FanRequest.java`: Data models.
        * `PlayerEnhanced.java`: Record class for storing enhanced Seahawk player data, including player ID, name, position, yards, touchdowns, and injury status.
        * `Position.java`: Enum class that lists the supported player positions, such as `QB`, `WR`, `RB`, and `CB`.
        * `UndoRecord.java`: Record class that links an `Action` to its corresponding inverse operation/previous state. 
        * `ActionType.java`: An Enum class that lists all of the supported  `Action` types, e.g., `ADD_PLAYER`, `REMOVE_PLAYER.`
    * simulator/
      * `UndoSimulator.java`: A Java class that simulates processing 5000 requests in the `TransactionFeed` and `RosterManager`, then undoing those actions with the `UndoManager`.
      * `DrillSimulator`: Simulates drill scheduling using a FIFO order queue and a Priority Queue using the `DrillManager`. Compares wait time metrics and reports results.
      * `DrillStats.java`: Mutable container for storing statistics associated with a `Drill` as it moves through a scheduling simulation.
      * `DrillReport.java`: An immutable Java record class that stores the drill's wait time and order processed.
      * `SampleSizes.java`: An Enum class that stores sample size options to allow users to easily choose which CSV dataset to run simulations on. 
    * util/
      * `SinglyLinkedList.java`: A generic, low-level utility class that manages a raw Singly Linked List (`SinglyLinkedList<T>`). 
      * `ArrayStore.java`: A generic, low-level utility class that manages a raw array (`T[]`). 
      It handles **dynamic resizing** (doubling capacity) via `System.arraycopy` and ensures **contiguous memory** by shifting elements during `removeAtIndex` operations.
      * `ArrayStack.java`: An array-based implementation of a stack.
      * `LinkedQueue.java`: A singly linked list implementation of a Queue.
      * `BinaryHeapPQ.java`: A Binary Heap-based implementation of a Priority Queue.
      * `HashTable.java`: A hash table implementation that stores key–value pairs using chaining with singly linked lists to handle collisions.
    * `Main` - CLI-driven menu interface for interacting with the SOAS application.
* **test/**: Contains unit tests and test resources.
    * `LoaderTest.java`: JUnit 5 test cases.
    * `badFormatPlayers.csv`: Resource for testing error handling.
* **data/**: Contains the primary CSV datasets (players, drills, and transactions).
* **resources/**: Contains a `results.txt` file displaying the results of the benchmark testing of the `TransactionFeed`. 

---

## Getting Started
### Prerequisites
* **Java SDK 25** (or your current version)
* **JUnit 5.12+**
* **IntelliJ IDEA**

## Tools Used
* Gemini, ChatGPT
    - Syntax clarification, Javadocs, architecture brainstorming, readme.md template, and to generate a graph of our benchmark results,



### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/4Michael1Angelo5/SOAS.git](https://github.com/4Michael1Angelo5/SOAS.git)
