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
| **Implementer: Core Logic** | Chris, Ayush | Ayush designed and implemented the `TransactionFeed`, Chris designed and implemented the `SinglyLinkedList` class|
| **Tester: JUnit Tests** | Chris, Ayush | Ayush designed the Junit 5 test suite for the `TransactionFeed`. Chris created the test suite for the `SinglyLinkedList` class|
| **Analyst: Benchmark + Analysis** | Ayush | Took charge of displaying the results of the `BenchmarkRunner` for the `TransactionFeed`|

---

## Analysis Section
1. Why does addFront differ drastically between arrays and linked lists?
   - Adding to the front of a linked list is more efficent then adding to the front of an array. This is because
     adding to the front of the array requires shifting all the elements in the array one index position to the right.
     Singly linked lists, on the other hand, only require two pointer reassignments: the new head becomes the element we are adding to the list,
     and the new node's next points to the rest of the list. Because each node in the list contains information about which node
     comes after it, we do not need to iterate through each node to maintain contiguous memory, whereas with arrays, contiguous memory
     is maintained by simple arithmetic, by array addressing each element based on where it is located from the front of the list.  
2. When does a linked list outperform an array?
   - A linked list outperforms an array when needing to frequently add or remove elements from the front of the list. The time complexity
     for adding or removing from the front in a linked list is $O(1)$, whereas with an array it is $O(n)$. If, for example, we needed to
     implement a First In First Out (FIFO) data structure like a Queue to manage user requests, an array would be a poor choice because each update
     of adding and removing from the array would result in constant shifting of the data elements in the list.  
3. When is an array better?
   - An array is superior to a linked list when needing frequent access to the contents of the list, not contained in the front or the end of the list.
     An array's main advantage over a linked list is that it can retrieve information about the contents anywhere in the list in $O(1)$ constant time, whereas
     with a singly linked list, it takes $O(n)$ time because each node only knows about what its immediate neighbor's value is. This means that to find the value
     of a specific indexed position node, we need to iterate over the list until we reach that specific node to figure out what its value is. 
4. What is the cost of pointer traversal?
   - The cost of pointer traversal is $O(n)$. If the linked list has 100 elements, and we want to get what is at index position 49.
     We would need to create a dummy pointer node, `walker`. We set `walker` to point to the head of the linked list, and keep track of how many
     steps `walker` has taken. We enter a loop and advance `walker` by setting `walker = walker.next`, and increment the step count. Once the step
     count equals index 49 `walker` is standing on the node, whose value we are interested in retrieving.  
5. Would this scale to 100,000 transactions?
   - A linked list data structure would be a good choice if we did not need to frequently retrieve transaction data that was not at the front or end of the list.
     If managing transaction data requires processing requests in FIFO order, then this data structure is suitable for 100,000 transactions.
     On the other hand, if the transaction data needs to be updated frequently or modified, the performance will deteriorate. However, in real-world applications
     "Transactions" are generally considered immutable. If an error occurs with a transaction, a new transaction is appended or enqueued to correct the error. Example:
     User A pays user B $50. Error: user A should have paid user B $60. Result new transaction: user A pays user B $10 - Not lets go back and change the ledger of the transaction
     histories. So in my opinion, a singly linked list is acceptable for managing transactions, provided a transaction for the domain business logic is expected to be
     an immutable history of all transactions that have taken place.   

## Reflection & Team Process
Ayush and Chris worked more efficiently this sprint. Last week, Chris and Ayush felt overwhelmed because they didn't dedicate enough time during the week 
to finish sprint deliverables for PA1, resulting in them having to spend all day Sunday and Sunday night to meet project deadlines. This week, Chris worked 
ahead of schedule, and as soon as PA1 was finished, Chris designed the `SinglyLinkedList` class for PA2. Ayush commited working extra time to work on Friday 
and quickly implemented the `TransactionFeed` class utilizing the `SinglyLinkedList` class Chris designed. This made Friday's work meeting more productive 
because the lowest-level data structure was already complete, so the team could work on introducing the core project features.

Both Chris and Ayush realized that there was some growing inefficiency in the code base, but the team decided to defer dealing with it until after the completion of 
The sprint deliverables for PA2. Currently, the `BenchmarkRunner` class is not designed to be flexible. Additionally, while the team made a good 
effort at designing a flexible abstract `DataLoader` class and an abstract `DataManager` class, there is still room for improvement. For example, the `DataLoader` is 
data-type agnostic. It does not care if it has loading `Player` data, `Drills` data, or `Transaction` data. It knows how to gracefully handle each data type and parse
CSV data into the correct `Player`, `Drills`, or `Transaction` data object. All the `DataLoader` cares about is that whoever is the caller of the `DataLoader` specifies 
up front which type of data it needs to load. However, the `DataLoader` and `DataManager` are not **data container agnostic**. Each one of those classes can only manage
and load data using an array-based data structure. 

This is where the problem is. With every new sprint, we will be tasked with adding functionality for a new type of data structure to act as our "data container". 
This means our `DataLoader` and `DataManager` will always create an array list of the CSV data objects, and creation of the proper data structure to contain and manipulate
those objects will be left as a chore to its children. This design is inefficient. No matter what data structure acts as our container, we have certain guarantees upfront about
what the container should be able to do. It should be able to modify the contents inside the container via `add()`, `remove()`, and `update()`, 
and it should expose an easy way to iterate over those objects. The better design would be create a sealed interface for all the supported data structures 
that will act as storage containers for `Player`, `Drills`, and `Transaction` data. Then it becomes the responsibility of the caller to specify what data objects we are
dealing with, `Player`, `Drills`, or `Transaction`, and which type of storage container they want to use to manage that data: `ArrayList`, `SinglyLinkedList`, `HashMap`, `Stack`,
`Queue` etc. This design is superior because then the concrete child classes that extend `DataManager` only need to connsern them selves with methods that are specific to managing
`Player`, `Drills`, or `Transaction` objects.

Similarly, the `Results` class that utilizes the `Benchmarkrunner` is not flexible enough. To complete this sprint deliverable of displaying the benchmark 
results of adding, removing, and updating with the new `SinglyLinkedList` data structure, we duplicated the same logic as the `Results` class for PA1, but swapped
the data structure to a `SinglyLinkedList` and used the new `TransactionFeed` instead of `RosterManager` to gather the test results. This is an inefficient design.
The `Results` class's job is to display results. It should not be opinionated about displaying results for a `TransactionFeed`, or a `RosterManager`, or a `DrillsManager`;
It should just be to format and gather results from the `BenchMarkrunner` - that's it! 

Unfortunately, although the team realized there was some inefficiency, we ultimately decided to move forward with the current design in favor of meeting project deadlines. 
We weighed out the pros and cons of introducing these changes into PA2. Although implementing the changes would result in a more robust, flexible, and scalable application, 
doing so would mean introducing breaking changes to the current code base and result in a substantial code refactor, which would take more time than simply duplicating the logic.
The team decided that for PA3, these features would be introduced. 



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
    * results/
        * `Results.java`: The benchmarking suite. It automates experiments across 50, 500, and 5000 records,
          calculating the average execution time (ms) for Add, Remove, and Search operations.
    * types/
        * `DataType`: Sealed interface that ensures all data managed by the system has a consistent identity.
        * `Player.java`, `Drill.java`, `Transaction.java`: Data models.
    * util/
      * `SinglyLinkedList.java`: A generic, low-level utility class that manages a raw Singly Linked List (`SinglyLinkedList<T>`). 
      * `ArrayStore.java`: A generic, low-level utility class that manages a raw array (`T[]`). 
      It handles **dynamic resizing** (doubling capacity) via `System.arraycopy` and ensures **contiguous memory** by shifting elements during `removeAtIndex` operations.
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
