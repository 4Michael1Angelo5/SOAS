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

We chose to use separate chaining using SinglyLinkedList as our collision strategy. 
For this strategy, each bucket holds a linked list of entries. When two keys
hash to the same bucket index, the new entry is prepended to the front of that 
bucket's linked list, and lookup traverses the chain until the matching key is found.

### 1. How did collision frequency change as size increased?

The collision count stayed zero for all dataset sizes (50, 500, and 5000). 
It means, when more players are added to the hash table, the keys still mapped
to different buckets instead of putting it in same spot. Because the player IDs are
in sequential order, they spread out evenly across the table when the hash 
function is calculating the bucket index.

Another reason collisions stayed at zero was because the table resizes when the load 
factor gets too high. Thats keept the table from becoming too full and it helped maintain
enough buckets for the keys. Since the table kept on expanding when more data was added,
it prevented multiple keys from being placed in the same bucket.

Because of this, the chaining mechanism using the linked list was never really used during these tests.
Each bucket only had one entry, so the operations didn’t have to search through a chain. This represents
the best-case behavior for a hash table, where operations like add and remove run in constant (O(1)) time.

---

### 2. How did load factor affect runtime?

When the dataset size increased from 50, 500, and 5000, the load factor increased from about 0.0100 
to 0.1000 to 0.4999. The load factor is showing us how full the table is. As we added more players,
the table started filling up more.

Looking at the insert times, it went from 0.089ms at size 50, then 0.063ms at size 500, and then 
0.295ms at size 5000. The drop in 50 to 500 is not really important. That can just happen
from normal change across the 30 benchmark runs. The big increase at size 5000 is because the 
load factor gets close to 0.5, which is the point where the table decides it is getting too full 
and needs to resize. When the table resizes, it doubles in size and moves all the existing entries
into the new table. And that extra work makes the operation slower.

Search and remove shows the same pattern. The search times went from 0.027ms to 0.070ms to 0.158ms, 
and remove times went from 0.057ms to 0.100ms to 0.241ms as the dataset got bigger. The operations
get a little slower as more data is stored, but the increase is still small. Since the collision count
stayed 0, each operation was able to go straight to the correct bucket without needing to go through a chain.

Overall, the load factor stayed low enough that it did not cause big slowdowns. The table resized when needed
and stayed fairly spread out. Because of that, the operations still behaved close to constant time on average
even when the dataset got larger.

---

### 3. Did results match expected O(1) behavior?

Yes, the results did matched what we were expecting. Looking at the numbers, when we went from 
50 players to 5000 players, that was a 100x increase in data. But the times did not 
go up 100x.

For search, it went from 0.027ms to 0.158ms. That is only about a 6x increase for 
100x more data. For remove, it went from 0.057ms to 0.241ms, which is about a 4x 
increase. If the hash table was slow like a simple array scan, those numbers would 
have gone up way more.

Insert was bit different because it jump from 0.089ms to 0.295ms. The jump at 5000 
was mostly do to the table having to resize when the load factor got close to 0.5. That 
resize moves all the entries into a bigger table which adds extra works. Without that, 
insert would have stayed more consistent across all three sizes.

---

### 4. At what load factor did performance degrade?

Analysis...

---

### 5. What tradeoffs exist between chaining and probing?

Discuss the advantages and disadvantages of different collision resolution strategies.

- **Chaining**
  - Pros:
  - Cons:

- **Probing (Open Addressing)**
  - Pros:
  - Cons:

Explain why the chosen strategy works well for this implementation.

---

### 6. Compare performance with PA1 (array) and PA2 (linked list) by filling the following table:

| Structure | Lookup Time | Memory (space + overhead) | Best Use Case |
|----------|-------------|-------------|-------------|
| Array (PA1) | | | |
| Linked List (PA2) | | | |
| Hash Table (PA5) | | | |

Analysis...

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
      * `BinaryHeapPQ.java`: A Binary Heap based implemenation of a Priority Queue.
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
