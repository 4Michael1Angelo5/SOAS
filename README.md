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
Size,Structure,Operation,Avg Time (ms)
50,ArrayStack,Push/Pop,0.0236
50,LinkedQueue,Enq/Deq,0.0200
500,ArrayStack,Push/Pop,0.0402
500,LinkedQueue,Enq/Deq,0.0329
5000,ArrayStack,Push/Pop,0.1376
5000,LinkedQueue,Enq/Deq,0.1441

1. Explain why Undo is a stack problem (LIFO)
    - Undo is a stack problem because the order in which we undo actions is Last-In-First-Out
      (LIFO) by nature. If a linear progression of steps is taken from state A to B to C, to
      get back to state A, I must undo or revert back to state B, then A. The most recent
      action must be reversed first, which aligns perfectly with how a stack operates—the last
      item pushed onto the stack is the first item popped off.

2. Explain why Fan lines are a queue problem (FIFO)
    - Fan lines are a queue problem because they rely on First-In-First-Out (FIFO) behavior.
      Generally speaking, we try to establish a meritocracy when processing requests, and that
      system of merit is built around the philosophy that the first request received should be
      the first one honored. Just like when we stand in line at a coffee shop, if someone cuts
      in front of you, you may be upset because you were in line first. Additionally, if both
      you and the person behind you are ordering the same thing, then you may be upset when
      they are served before you. Processing requests in this way not only satisfies the client
      but also provides a predictable format for systems to ensure that all requests are
      handled.

3. Compare your measured performance trends across dataset sizes.
    - In general, the results from our benchmark testing with data sample sizes of 50, 500,
      and 5,000 reveal that stacks and queues are incredibly efficient at the problems of
      undoing and processing requests. These operations have $O(1)$ time complexity, making them
      well-suited for processing large data sizes. We observed that a maximum peak stack depth of
      5,000 undo actions required, on average, only 0.25 milliseconds for push and pop
      operations. This efficiency is the signature hallmark of these data structures.  

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
