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

| Role | Member(s) | Primary Responsibilities |
| :--- | :--- | :--- |
| **Implementer: Core Logic** | Chris | Implemented the `DrillManager`, `BinaryHeapPQ`, and `DrillSimulator` |
| **Tester: JUnit Tests** | Ayush | Ayush designed the Junit 5 test suite for the `DrillManager`, and `BinaryHeapPQ` class|
| **Analyst: Benchmark + Analysis** | Chris and Ayush | Ayush implemented the Results and `OperationCounter`. Chris drew on these results and reported them in the README and the `DrillSimulator`|
---

## Analysis Section


1) **Why does a heap-based PQ support efficient scheduling?**

A heap-based priority queue supports efficient scheduling in $O(\log n)$ time. Each insertion 
and removal of an element in the priority queue must, in the worst case, compare each of its children 
for removal, or compare the element being added to its parent for insertion. The number of comparisons 
scales linearly with the height of the tree, and the tree height is given by $\log_2(n)$. Therefore,
the cost of $n$ insertions is $n \log(n)$. However, insertion efficiency can be improved to $O(n)$ 
using the build heap procedure by building the entire heap from an array in one pass, performing 
heapify-down when necessary, instead of adding one element at a time.

The priority queue's main strength is that it can retrieve the element with the highest priority 
in $O(1)$ constant time. This supports efficient scheduling by organizing which task should be 
performed first without the need to search the heap for what should come next. The trade-off 
is that, to maintain this efficiency, we pay the price of $O(\log n)$ insertion and removal.

2) **Compare FIFO vs Priority scheduling: what is gained, what is lost?**

In a regular queue that processes items in first-in-first-out (FIFO) order, we gain back the $O(1)$ 
efficiency lost in a priority queue in the insertion and removal operations, but we lose the ability 
to retrieve the item with the highest priority in constant time.

3) **How did comparisons/swaps scale from 50 → 5000?**

Our benchmark testing results revealed that the number of comparisons and swaps scales in 
$n \log(n)$ time. We observed the following numbers using sample sizes of 50, 500, and 5000:

| Size  | Operation | Avg Time (ms) | Comparisons       | swaps   |
|-------|-----------|---------------|-------------------|---------|
| 50    | insert    | 0.042163      | 84                | 36      |
| 50    | extract   | 0.050867      | 301               | 173     |
| 500   | insert    | 0.102023      | 874               | 376     |
| 500   | extract   | 0.323800      | 5447              | 2938    |
| 5000  | insert    | 0.367923      | 9054              | 4058    |
| 5000  | extract   | 1.581247      | 78381             | 41387   |

These results are consitent with the expeceted $n\log(n)$ behavior for insertion and extraction.
If the time complexity is $O(n \log n)$, then when the input size grows, the work should grow by about the same ratio as:

$\frac{n_2 \log n_2}{n_1 \log n_1}$

We compared expected growth vs actual operation growth.

50 → 500

Expected (from $n \log n$):

$\frac{500 \log_2 500}{50 \log_2 50} = \frac{4485}{282} \approx 15.9$

Actual:
- Insert comparisons: $874 / 84 \approx 10.4$
- Extract comparisons: $5447 / 301 \approx 18.1$

These are in the same general range as 15.9, so that matches the $n \log n$ pattern. 
Insert consistently runs better than the predicted ratio, this is likely because 
insertions often don't travel all the way up the tree, so the average case is better 
than the theoretical bound suggests.

500 → 5000

Expected:

$\frac{5000 \log_2 5000}{500 \log_2 500} = \frac{61450}{4485} \approx 13.7$

Actual:
- Insert comparisons: $9054 / 874 \approx 10.4$
- Extract comparisons: $78381 / 5447 \approx 14.4$

Again, the numbers are close to the expected 13.7. So overall, the operation counts grow close to what $n \log n$ predicts. 
So we can say that insert and extract do follow $O(n \log n)$.

Also, operation counts give a clearer picture than time because runtime 
can change depending on JVM warmup or other background factors, but 
comparisons and swaps directly measure the actual work the heap is doing.

4) **Did you observe any evidence of starvation?**

We observed significant starvation — drills being continuously denied to be processed because they 
had a lower priority. We conducted simulations on sample sizes of 50, 500, and 5000, using the 
following sorting logic to prioritize drills:
- Higher urgency first
- Earlier `install_by_day` first
- Lower `fatigue_cost` preferred (tie-breaker)
- Shorter duration preferred (final tie-breaker)

The simulation compared wait times required to process Seahawks drills between FIFO behavior Queues 
and Priority Queues. The results from the sample size of 5000 revealed that the average wait time for
the Queue was 34,797.50 minutes. The average wait time for the Priority Queue was 34,816.91 minutes,
so on average, each drill in the Priority Queue experienced a longer wait time compared to the Queue.

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

"Run Fits 91" was processed 67,792 minutes later than it would have been processed in a regular 
queue. This drill was pushed back 4,863 places in line, meaning it was originally in line at position
91, but instead, 4,953 other drills were processed before this drill. It had a Z-time-score of -2.39,
meaning this drill's wait time was 2.39 standard deviations worse than the average change in wait time.
It had a Z-position-score of -2.39, meaning this drill's change in position was 2.39 standard 
deviations worse than the average change in position.

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

"Screen Defense 4927" was processed 68,397 minutes sooner than it would have been processed in a regular
queue. This drill was able to skip 4,912 places in line, meaning it was originally in line at position 
4,927, but because it had a high priority, it jumped to position 15! It had a Z-time-score of 2.41, 
meaning this drill's wait time was 2.41 standard deviations better than the average change in wait time.
It had a Z-position-score of 2.41, meaning this drill's change in position was 2.41 standard deviations 
better than the average change in position.

Our simulation results revealed a near-identical relationship between a drill's Z-time-score and its 
Z-position-score. Additionally, simulation trials between sample sizes of 50, 500, and 5000 revealed a 
linear relationship between the sample size and the z-scores of the top and bottom 1% of most affected 
drills. Meaning that the top 1% of drills that experienced the greatest change in wait time, their deltas
grew proportionally to the sample size — the larger the sample size, the more extreme/volatile the change
in wait times. This result is statistically significant because it suggests that as the input size tends 
to infinity, the most affected drills' change in position and time grows without bound.

5) **What would you change in your priority rule to improve fairness?**

The results demonstrated that the sorting strategy results in significant starvation of lower-priority 
drills. To counteract this, an effective strategy would be to introduce aging. As a drill that gets 
continually pushed further down the queue, we could introduce another field in the `Drills` class that 
keeps track of how many times this drill was skipped. As the skip count grows to a certain threshold, 
its priority increases, preventing the drill from being skipped indefinitely. This would effectively 
balance out the sorting strategy to become more fair, so that no drill waits indefinitely to be processed.

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
      * `RosterManager`: concrete child class of `DataManager` that brings specific functionality needed to manage the Seahawks roster.
      * `TransactionFeed`: concrete child class of `DataManager` that brings specific functionality needed to manage the Seahawks transactions.
      * `UndoManager`:  concrete child class of `DataManager` that brings specific functionality needed to undo actions from the other managers.
      * `FanTicketQueue`: concrete child class of the `DataManager` that brings specific functionality needed to manage the Seahawks fan ticket line.
      * `DrillManager`: concrete child class of the `DataManager` that brings specific functionality needed to manage Seahawks Drills.
    * results/
        * `ExperimentResults.java`: A simple Record class used to report a specific result from a benchmark test. 
        * `Results.java`: Abstract parent class defining all common behavior to concrete Benchmark Results classes: `FanTicketResults`, `RosterResults`, `TransactionResults`, `UndoResults`. It automates experiments across 50, 500, and 5000 records,
        * `FanTicketResults`, `RosterResults`, `TransactionResults`, `UndoResults`: Concrete child classes of the `Results` class that handle specific testing needed for managing their respective `DataTypes`.
        * `Experiment.java`: Interface defining contract for all Results classes.
        * 
          calculating the average execution time (ms) for Add, Remove, and Search operations.
    * types/
        * `DataType`: Sealed interface that ensures all data managed by the system has a consistent identity.
        * `Player.java`, `Drill.java`, `Transaction.java`, `Action.java`, `FanRequest.java`: Data models.
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
