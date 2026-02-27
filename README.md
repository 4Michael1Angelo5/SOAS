##  Seahawks Operations & Analytics System (SOAS)
The SOAS app is a simple CLI stats analysis application that parses Seahawks data from CSV files and provides the user with interesting ways to interact with the data.

## Features
* The app tracks how many operations the CSV parsing algorithm performs and generates a report.
* Benchmark timer tracks algorithm run time in milliseconds.
* CLI-driven menu to navigate application options for improved UX.
* CSV parsing optimized using a `BufferedReader`.



## Team Information
* **Chris Chun**
* **Ayush**

---

## Role Assignments

| Role | Member(s) | Primary Responsibilities |
| :--- | :--- | :--- |
| **Implementer: Core Logic** | Chris, Ayush | Chris and Ayush designed the `UndoManager` and `FanTicketQueue`, and Chris implemented the core ADTs `LinkedQueue` and `ArrayStack`, and scaffolded out the dependency injection strategy. |
| **Tester: JUnit Tests** | Chris, Ayush | Ayush designed the Junit 5 test suite for the `UndoManager`, and `FanTicketQueue` class|
| **Analyst: Benchmark + Analysis** | Chris | Took charge of implementing the results for the `UndoManager` and the `FanTicketQueue`|
---

## Analysis Section


1) **Why does a heap-based PQ support efficient scheduling?**

A heap-based priority queue supports efficient scheduling in $O(\log n)$ time. Each insertion and removal of an element in the priority queue must, in the worst case, compare each of its children for removal, or compare the element being added to its parent for insertion. The number of comparisons scales linearly with the height of the tree, and the tree height is given by $\log_2(n)$. Therefore, the cost of $n$ insertions is $n \log(n)$. However, insertion efficiency can be improved to $O(n)$ using the build heap procedure by building the entire heap from an array in one pass, performing heapify-down when necessary, instead of adding one element at a time.

The priority queue's main strength is that it can retrieve the element with the highest priority in $O(1)$ constant time. This supports efficient scheduling by organizing which task should be performed first without the need to search the heap for what should come next. The trade-off is that, to maintain this efficiency, we pay the price of $O(\log n)$ insertion and removal.

2) **Compare FIFO vs Priority scheduling: what is gained, what is lost?**

In a regular queue that processes items in first-in-first-out (FIFO) order, we gain back the $O(1)$ efficiency lost in a priority queue in the insertion and removal operations, but we lose the ability to retrieve the item with the highest priority in constant time.

3) **How did comparisons/swaps scale from 50 → 5000?**

Our benchmark testing results revealed that the number of comparisons and swaps scales in $n \log(n)$ time. We observed the following numbers using sample sizes of 50, 500, and 5000:

| Operations | 50 | 500 | 5000 |
|------------|----|-----|------|
| Swaps | | | |
| Comparisons | | | |

Using curve fitting software, it reveals that the number of swaps roughly fits in line with the equation …. Which in big O notation can be simplified to .... The number of comparisons roughly fits the equation …. Which in big O notation can be simplified to ....

4) **Did you observe any evidence of starvation?**

We observed significant starvation — drills being continuously denied to be processed because they had a lower priority. We conducted simulations on sample sizes of 50, 500, and 5000, using the following sorting logic to prioritize drills:
- Higher urgency first
- Earlier `install_by_day` first
- Lower `fatigue_cost` preferred (tie-breaker)
- Shorter duration preferred (final tie-breaker)

The simulation compared wait times required to process Seahawks drills between FIFO behavior Queues and Priority Queues. The results from the sample size of 5000 revealed that the average wait time for the Queue was 34,797.50 minutes. The average wait time for the Priority Queue was 34,816.91 minutes, so on average, each drill in the Priority Queue experienced a longer wait time compared to the Queue.

For example, the drill that suffered the most was:
```json
{
  "drill_id": 2091,
  "name": "Run Fits 91",
  "urgency": 1,
  "duration_min": 12,
  "fatigue_cost": 7,
  "install_by_day": 7
}
```

"Run Fits 91" was processed 67,792 minutes later than it would have been processed in a regular queue. This drill was pushed back 4,863 places in line, meaning it was originally in line at position 91, but instead, 4,953 other drills were processed before this drill. It had a Z-time-score of -2.39, meaning this drill's wait time was 2.39 standard deviations worse than the average change in wait time. It had a Z-position-score of -2.39, meaning this drill's change in position was 2.39 standard deviations worse than the average change in position.

However, the biggest winner was:
```json
{
  "drill_id": 6927,
  "name": "Screen Defense 4927",
  "urgency": 5,
  "duration_min": 20,
  "fatigue_cost": 1,
  "install_by_day": 1
}
```

"Screen Defense 4927" was processed 68,397 minutes sooner than it would have been processed in a regular queue. This drill was able to skip 4,912 places in line, meaning it was originally in line at position 4,927, but because it had a high priority, it jumped to position 15! It had a Z-time-score of 2.41, meaning this drill's wait time was 2.41 standard deviations better than the average change in wait time. It had a Z-position-score of 2.41, meaning this drill's change in position was 2.41 standard deviations better than the average change in position.

Our simulation results revealed a near-identical relationship between a drill's Z-time-score and its Z-position-score. Additionally, simulation trials between sample sizes of 50, 500, and 5000 revealed a linear relationship between the sample size and the z-scores of the top and bottom 1% of most affected drills. Meaning that the top 1% of drills that experienced the greatest change in wait time/position, the decrease/increase in wait time/position grew proportionally to the sample size — the larger the sample size, the more extreme/volatile the change in wait times. This result is statistically significant because it indicates that as the input size tends to infinity, the most affected drills' change in position and time grows without bound.

**What would you change in your priority rule to improve fairness?**

The results demonstrated that the sorting strategy results in significant starvation of lower-priority drills. To counteract this, an effective strategy would be to introduce aging. As a drill which gets continually pushed further down the queue, we could introduce another field in the `Drills` class that keeps track of how many times this drill was skipped. As the skip count grows to a certain threshold, its priority increases, preventing the drill from being skipped indefinitely. This would effectively balance out the sorting strategy to become more fair, so that no drill waits indefinitely to be processed.

> **Note:** Fairness means lower-priority items eventually get scheduled, not blocked forever. Two easy fairness strategies are: **Aging** — increase effective priority the longer a drill waits; and **Priority with a "quota"** — run mostly high-priority drills but guarantee occasional low-priority selection.Want to be notified when Claude responds?

# Reflection: Code Architecture Improvements for PA3

Chris and Ayush made significant improvements to the code base for PA3. The team utilized
inheritance by creating a generic abstract parent class `DataManager<T>`. The DataManager
centralizes shared behavior of all the concrete `Manager` classes, e.g., `RosterManager`,
`TransactionFeed`. The team built upon this design for PA3 by scaffolding out a dependency
injection strategy into the `DataManagers`, making them not only `DataType` agnostic but also
`DataContainer` agnostic - meaning each concrete child of the DataManager class can easily swap
different DataContainers to test the efficiency and suitability of different data structures for
managing the data they are responsible for.

The dependency injection strategy was achieved by modifying the constructors of the DataManager
to accept a `Supplier<DataContainer<T>>`, and by changing the method signature of DataManager to
implement a shared abstract interface `Manager<T>`. This design direction was chosen because it
allowed the team to treat the DataContainers as independent variables for benchmark testing.
Essentially, the team built a benchmark testing framework for data structures disguised as a
Seahawks Data Analysis app!

The team did not stop here. The team carried out this strategy at all levels of the application,
including the `Results` classes, mirroring the same dependency injection and abstract parent
hierarchy of `Manager` classes. The team decoupled the DataContainer and DataType from the
Results classes and enforced strict type safety by tying each instance of its child classes to a
`DataType<T>` and `Manager<T>` of type `M`.
```java
public abstract class Results<T extends DataType, M extends Manager<T>>
```

This design allowed us to fluidly test each Manager class with different data structures. A
RosterManager could use a SinglyLinkedList, an Array, or a HashMap, etc, all while being type-safe
and predictable.

Of course, with such a design, there were and still are significant challenges and drawbacks. For
example, for us to do this, it meant creating a common interface for all manager classes
to use. All managers need the ability to add data and remove data. So the obvious thing to do was
to create a method called addData and removeData. The trouble with this is that we started to
lose the semantic meaning of what adding or removing data actually did! It pushes the
responsibility and cognitive load on the developer to understand that when I am removing from a
stack, removeData means remove from the top of the stack; when removing from a min heap,
removeData means remove the root node and perform heapify down, etc. With so much abstraction,
there are several layers the developer needs to think through just to understand what they are
doing.

Additionally, there are several imperfect details about the abstraction that still bother the
team - and that is the leaky abstraction between the DataTypes and DataManagers. The DataManagers
cannot be truly data container agnostic. They need to be able to do specific things to manage
their data, which demands certain data structures. For example, a RosterManager needs to be able
to search and update players in the roster, but a stack does not provide this functionality. This
required the data structures to reveal implementation details to the managers by having flags
such as `needsIndexedAccess()` and `supportsIndexedAccess()`. In this way, the application can
guard against a developer configuring a RosterManager with a stack.

Although there were several cons for this design approach, the team felt that the benefits
outweighed the drawbacks. This design approach allows the team to be incredibly flexible and
reduces code redundancy. Next week, when we need to design a min stack, the hardest part will be
implementing the data structure logic, but incorporating that data structure into our existing
framework and ecosystem will be frictionless - zero code repeat, just plug and play. 



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
      * `RosterManager`: concrete child class of `DataManager` that brings specific functionality needed to manage the Seahawks roster.
      * `TransactionFeed`: concrete child class of `DataManager` that brings specific functionality needed to manage the Seahawks transactions.
      * `UndoManager`:  concrete child class of `DataManager` that brings specific functionality needed to undo actions from the other managers.
      * `FanTicketQueue`: concrete child class of the `DataManager` that brings specific functionality needed to manage the Seahawks fan ticket line. 
    * results/
        * `ExperimentResults.java`: A simple Record class used to report a specific result from a benchmark test. 
        * `Results.java`: Abstract parent class defining all common behavior to concrete Benchmark Results classes: `FanTicketResults`, `RosterResults`, `TransactionResults`, `UndoResults`. It automates experiments across 50, 500, and 5000 records,
        * `FanTicketResults`, `RosterResults`, `TransactionResults`, `UndoResults`: Concrete child classes of the `Results` class that handle specific testing needed for managing their respective `DataTypes`.
        * `Experiment.java`: Interface defining contract for all Results classes.
        * 
          calculating the average execution time (ms) for Add, Remove, and Search operations.
    * types/
        * `DataType`: Sealed interface that ensures all data managed by the system has a consistent identity.
        * `Player.java`, `Drill.java`, `Transaction.java`, `Action`, `UndoRecord`: Data models.
    * simulator/
      * `Simulator.java`: A Java class that simulates processing 5000 requests in the `TransactionFeed` and `RosterManager`, then undoing those actions with the `UndoManager`. 
    * util/
      * `SinglyLinkedList.java`: A generic, low-level utility class that manages a raw Singly Linked List (`SinglyLinkedList<T>`). 
      * `ArrayStore.java`: A generic, low-level utility class that manages a raw array (`T[]`). 
      It handles **dynamic resizing** (doubling capacity) via `System.arraycopy` and ensures **contiguous memory** by shifting elements during `removeAtIndex` operations.
      * `ArrayStack`: An array-based implementation of a stack.
      * `LinkedQueue`: A singly linked list implementation of a Queue. 
    * `Main` - CLI-driven menu interface for interacting with the SOASS application.
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
    - Syntax clarification, Javadocs, architecture brainstorming, and readme.md template



### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/4Michael1Angelo5/SOAS.git](https://github.com/4Michael1Angelo5/SOAS.git)
