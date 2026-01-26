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
| **Implementer: Core Logic** | Chris, Ayush | Developed the `DataLoader` class, CSV parsing algorithm, and `Main` CLI menu. Designed and implemented `RosterManager` |
| **Tester: JUnit Tests** | Chris | Designed the JUnit 5 test suite, including edge-case testing for File I/O and malformed data. |
| **Analyst: Benchmark + Analysis** | Ayush | Implemented the `OperationCounter`, `BenchmarkRunner`, and conducted performance analysis on data loading efficiency. Designed and implmented `Results` class |

---

## Analysis Section
1. Why does removal become slower as the roster grows?
   - Removal in an array-based data structure becomes slower as the roster grows because the number of reassignments
     and shifts grows linearly with the number of players in the roster.
2. What causes shifting?
   - Shifting is caused in array-based data when elements are removed from an index other than the end of the list.
      After the element at index i in the array is removed. All elements to the right of i must shift back one
      index position to fill the “hole” created by the removal.
3. Why is searching $O(n)$? 
   - Searching is O(n) because, in the worst case, the element we are searching for is not in the list, and we
     must exhaustively search each element before we can conclude it is not in the list.

4. Would this structure scale to 100,000 players?
   - No. While $O(n)$ is acceptable for 5,000 players, the shifting cost for removals and the comparison cost
     for searches would cause significant latency at 100,000 players. At that scale, an array-based structure
     is inefficient compared to a `HashMap` (for $O(1)$ search) or a `TreeMap` (for $O(\log n)$ operations).
5. When is an array-based structure a good choice?
   - An array-based data structure is a great choice when you do not know the exact length or size of the data you
      need to add. It is possible to achieve amortized constant time complexity for adding elements to the list if we
      dynamically resize by doubling the capacity as we get full. It is also a great choice if we know the exact position
      of the element we need because it has O(1) constant time retrieval through memory address arithmetic. If, however,
      we need to remove frequently from a position other than the end of the list, an array-based data structure can
      become inefficient because it requires shifting all the elements to the right of the removed element back one space.
      Additionally, if frequent searching is required as the array grows, the search can also become costly.

## Reflection & Team Process

Chris spent a lot of time thinking deeply about the project structure and architecture. He embraced abstraction and 
designed an Abstract DataManager class to implement the shared functionality he saw all future Manager classes would 
have. Regardless if the manager was a DrillsManager, RosterManager, or TransactionManager, there were certain 
behaviors and methods each concrete child class would need, such as searching for a data entry by its ID, or fetching
and loading data from a csv. By utilizing inheritance and abstraction, it made it so that adding different types of
data managers becomes trivial, and then they only need to worry about the logic for their specific class. 

Chris went the extra length of creating a sealed interface DataType to ensure only permitted data types were allowed,
such as Player, Transaction, or Drills. Doing this allowed the design of the DataLoader to also be generic so that 
it does not know ahead of time which type of data it will be loading; it only cares that whoever is calling its methods 
is using one of the permitted data types. This made the DataLoader very flexible and made it so that each manager can 
get the data they need without having to worry about the implementation details of how the data is parsed from a csv. 

Reflecting on the assignment, Chris and Ayush feel like they might have overengineered the project. The project seemed 
to only really require a piston, but Chris and Ayush built an engine. Although Chris and Ayush are proud of their work, 
it was more time consuming then it needed to be, and highlights the tension between building something that works because 
I need it now, and building something that will scale with future demands. 


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
            Manager, etc.
      * `RosterManager`: concrete class that brings specific functionality needed to manage the Seahawks roster.
    * results/
        * `Results.java`: The benchmarking suite. It automates experiments across 50, 500, and 5000 records,
            calculating the average execution time (ms) for Add, Remove, and Search operations.
    * types/
      * `DataType`: Sealed interface that ensures all data managed by the system has a consistent identity.
      * `Player.java`, `Drill.java`, `Transaction.java`: Data models.
    * util/
      * `ArrayStore.java`: A generic, low-level utility class that manages a raw array (`T[]`). 
      It handles **dynamic resizing** (doubling capacity) via `System.arraycopy` and ensures **contiguous memory** by shifting elements during `removeAtIndex` operations.
    * `Main` - CLI-driven menu interface for interacting with the SOASS application.
* **test/**: Contains unit tests and test resources.
    * `LoaderTest.java`: JUnit 5 test cases.
    * `badFormatPlayers.csv`: Resource for testing error handling.
* **data/**: Contains the primary CSV datasets (players, drills, and transactions).

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
