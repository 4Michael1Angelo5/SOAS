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
`Objects.hash()` is a built in Java utility method. When you pass an integer key to 
it, it internally calls `hashCode()` on that integer. For integer types in Java, 
`hashCode()` just returns the integer value itself. So passing in player ID 1001 
gives us back 1001, player ID 1002 gives us 1002, and so on. We can then take that 
value mod the table capacity to get the bucket index.

### Why we used `& 0x7FFFFFFF` instead of `Math.abs()`

At first it seems like `Math.abs()` would be the simple way to make sure the hash 
result is always positive. But there is an edge case where it completely breaks.
Java uses Two's Complement to represent integers. Because of this, the range of a 
32-bit signed integer is not symmetrical:

- Maximum positive value: 2,147,483,647 (2^31 - 1)
- Minimum negative value: -2,147,483,648 (-2^31)

The negative side can hold one more value than the positive side can. This will cause 
a problem when we try to use `Math.abs()` on the most negative number possible.
When you call `Math.abs(-2,147,483,648)`, Java tries to flip it to positive 
2,147,483,648. But that number is too big to fit in an int. The value wraps 
all the way around and comes back out as -2,147,483,648 again. So 
`Math.abs(Integer.MIN_VALUE)` returns a negative number, which would give us an 
invalid bucket index.

The bitmask `& 0x7FFFFFFF` fixes this completely. Instead of doing any arithmetic, 
it just forces the sign bit to 0. No addition, no overflow, and no wrapping. The result 
is always a valid positive number no matter what.

`0x7FFFFFFF` in binary looks like this:
```
0 1111111 11111111 11111111 11111111
```
Every bit is 1 except the leftmost sign bit which is 0. When we AND any number 
with this, all the original bits stay the same but the sign bit gets forced to 0, 
guaranteeing a positive result every time.

### Benchmark Testing:

We ran our benchmarks using three dataset sizes: 50, 500, and 5000 players. For 
each size we measured three operations: Insert, Search, and Remove. Every operation 
was run 30 times and we took the average time to reduce noise in the results.
The table starts with an initial capacity of 16 buckets. As we insert more players, 
the load factor is tracked after every insert. Once the load factor exceeds 0.75, the 
table doubles its capacity and rehashes all existing entries into the new bigger table. 
This means by the time all players are inserted, the table has already resized several 
times. For 5000 players the table grew from 16 all the way to 8192 buckets.

We measured two metrics alongside the average time:
- **Load Factor** — captured after the last trial of each operation. This tells us 
  how full the table was when the operation finished. Remove always shows 0.0 because 
  the load factor is captured after all players have been removed and the table is empty.

- **Collisions** — tracked inside `put()`. A collision is counted when a new key maps 
  to a bucket that is already occupied by a different key. This gives us an accurate 
  count of how many times two different players competed for the same bucket.

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

For PA5, tensions emerged between the design direction of the SOASS application and the integration
of the sprint requirements. Despite the team's best efforts to streamline the course project by
abstracting the addition of new Data Structure integrations for the benchmark testing into the
`DataContainer` interface, PA5 required the implementation of a `HashMap`, which did not fit neatly
into this abstraction. Namely, the `add(T val)` method guaranteed by the `DataContainer` interface
does not align with `add(K key, V val)`. This is because the `HashTable` requires key-value pairs
for insertion. Similarly, `remove(T val)` does not align with `remove(K key)`. The group realized
that if we were to enforce the implementation of the `DataContainer` interface onto the `HashTable`,
it would effectively reduce the `PlayerManager`'s usage of the `HashTable` to a `HashSet`, thus not
fulfilling the sprint requirement of O(1) lookups by Player ID.
 
To solve this, the group decided the best path forward was not to enforce the implementation of the
`DataContainer` interface onto the `HashTable`. The consequence of this decision was that code
duplication appeared in the abstract `MapManager` class. It is essentially a duplicate of the
abstract `DataManager` class but designed only to work with data structures that implement a
`HashTable`/`Dictionary` interface. The group realized there was a path forward to eliminate the
code duplication but it would require a major refactor of over 3k lines of code. Considering that
this was the final sprint for the quarter-long project, the group determined the payout from a
refactor was too little to be worth it. Rewriting 3k lines of code to eliminate 500 lines of code
duplication just doesn't make sense.
 
However, if the team decides to continue this project in the future, a refactor would ultimately be
the right choice, but considering the time constraints, the group chose the path of pragmatic
delivery. The `DataContainer` abstraction was great and streamlined the group's ability to meet
sprint requirements, but by PA5, it was clear it was becoming too monolithic and starting to leak.
What the group learned was that sometimes the current abstraction does not align with business
logic, and it is better to depart from it when this becomes apparent, rather than enforce guarantees
that the Data Structure cannot implement.
 
Additionally, the team learned that the developer's mental overhead of managing a library built upon
so many layers of abstraction can be mentally taxing. For example, `addPlayer` from the
`HashTableManager` calls the abstract parent's `addData`, which calls
`HashTable.put(player.id, player)` - just what the heck is `addPlayer` doing? Then there is getting
lost between what is an actual abstract class vs what is an interface. When using generics, it is
easy to get lost when there are 3 different type parameters moving around. The group explored a
possible refactor of the Results class, which would have looked like:
 
```java
public abstract class ExperimentOrchestrator
        <T extends DataType,
         M extends OperationsManager<T,?>>
        implements OperationCountable<T>, Orchestraction
```
 
At this level of abstraction, we could not make guarantees about what type of Data Structure the
manager would use to manage its data because the `Dictionary` interface and the `DataContainer`
interface were so fundamentally different that we needed to be general enough to accommodate both.
 
One thing is for sure: the group learned so much about generics and got a lot of great experience
working with abstraction-heavy design.
 
The implementation aspect of the PA was the least challenging part of the assignment. The most
challenging was defining the problem. Our problem was that we had encountered a data structure that
challenged our design approach. Once we figured out the best path forward, integration into the
existing system was fairly straightforward. This experience taught us that the best path forward is
not a major refactor that risks breaking existing logic in hopes of modest gains, but rather
accommodating new features to deliver project deliverables on time and adjust for future
extensibility as needed.


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
