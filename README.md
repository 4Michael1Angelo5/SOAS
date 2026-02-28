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

| Operations | 50 | 500 | 5000 |
|------------|----|-----|------|
| Swaps | | | |
| Comparisons | | | |

Using curve fitting software, it reveals that the number of swaps roughly fits in line with the 
equation …. Which in big O notation can be simplified to .... The number of comparisons roughly 
fits the equation …. Which in big O notation can be simplified to ....

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
    - Syntax clarification, Javadocs, architecture brainstorming, and readme.md template



### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/4Michael1Angelo5/SOAS.git](https://github.com/4Michael1Angelo5/SOAS.git)
