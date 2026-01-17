##  Seahawks Operations & Analytics System (SOAS)
The SOAS app is a simple CLI stats analysis application that parses Seahawks data from CSV files and provides the user with interesting ways to interact with the data.

## Features
 * The app tracks how many operations the CSV parsing algorithm performs and generates a report.
 * Benchmark timer tracks algorithm run time in milliseconds.
 * CLI-driven menu to navigate application options for improved UX.
 * CSV parsing optimized using a `BufferedReader` and `StringBuilder`.  



## Team Information
* **Chris Chun**
* **Ayush** 

---

## Role Assignments

| Role | Member(s) | Primary Responsibilities |
| :--- | :--- | :--- |
| **Implementer: Core Logic** | Chris, Ayush | Developed the `DataLoader` class, CSV parsing algorithm, and integrated Java Reflection. |
| **Tester: JUnit Tests** | Chris | Designed the JUnit 5 test suite, including edge-case testing for File I/O and malformed data. |
| **Analyst: Benchmark + Analysis** | Ayush | Implemented the `OperationCounter` and conducted performance analysis on data loading efficiency. |

---

## Analysis Section
1. Why do we repeat benchmarks instead of timing once?
   - We repeat benchmarks instead of just timing once to get more reliable data. Simply running a test once is not enough because we do not know how consistent the data is. Running tests multiple times allows us to compare test trials and to see if the data is consistent and measure the standard deviation. If it is not consistent, then we know there is something wrong with the code. Additionally, there may be outliers in the data, but without multiple trials, there is no way to identify the outliers. 
3. What factors can make timing unreliable?
   - Measuring algorithm run time in seconds or milliseconds can become unreliable because it can differ significantly between machines and programming languages.   
5. Why might operation counting be more informative than raw time?
   - Operation counting might be more informative than raw run-time data to measure algorithm time complexity because run-time data is more dependent on the machine and programming language, whereas operation counting is less tightly coupled to the operating system and the programming language. 
7. What challenges did you encounter in building the harness?

  We encountered numerous challenges with the assignment. Ayush faced challenges integrating his local workspace with GitHub. Chris grappled with design decision tradeoffs when architecting the project structure. 
  
  Ayush initially struggled when collaborating on the project using GitHub. The main challenge was getting back into the flow after having winter break and familiarizing himself with continuous integration/ continuous deployment (CI/CD) pipelines. Additionally, Ayush kept forgetting his GitHub password, which made authenticating himself challenging. Fortunately, Chris and Ayush were able to resolve the merge conflicts on GitHub and most of the other difficulties using Discord on a live conference call. 
  
  Chris quickly iterated the implementation of the `DataLoader` class, but realized there was an opportunity to make the code more modular by abstracting how individual methods loaded data from a comma-separated value (csv) file into a generic `loadData` helper method. This improved code maintainability and made the code “DRY” (Don’t Repeat Yourself). Chris often struggles with spending too much time thinking of the perfect code instead of quickly iterating and optimizing later. Fortunately Chris was able to pull himself out when he saw himself getting in the weeds. 
  
  When designing the command line interface (CLI) menu, which drives the main interaction between the user and the application, Chris realized there was a potential rich user experience (UX) opportunity. Chris envisioned a nested HashMap or Tree data structure with a main menu containing options and submenus with options. Then a stack could be used to navigate between menu histories to navigate forward and backward throughout the menu. However, the time constraints of the assignment only allowed for Chris to craft a rough sketch of what this might look like, and its actual implementation was pushed aside in favor of meeting the sprint’s requirements. In future releases of the application, this feature may become available.  


---

## Project Structure
The project is organized with separate source and test roots to maintain clean code separation:

* **src/**: Contains production source code.
    * `DataLoader.java`: Core logic for file reading and generic data parsing.
    * `Player.java`, `Drill.java`, `Transaction.java`: Data models.
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
* Gemini, chatgpt
    - Syntax clarification, Java docs, architecture brainstorming, and readme.md template
  


### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/4Michael1Angelo5/SOAS.git](https://github.com/4Michael1Angelo5/SOAS.git)
