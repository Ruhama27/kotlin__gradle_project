# 📚 Library Management System (Kotlin & Gradle) — Enhanced Edition

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-purple.svg)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-02303A.svg)](https://gradle.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Tests](https://img.shields.io/badge/Tests-13%20Passed-success.svg)]()

> **Weekend Challenge Deliverable — Task 2: Fork, Study, and Improve a Previous Solution**  
> Selected Student Solution Studied: [haset-T/kotlin__gradle_project](https://github.com/haset-T/kotlin__gradle_project)  
> Student Credited: **haset-T**  
> Challenge Repository: [Debre-birhan-university/kotlin__gradle_project](https://github.com/Debre-birhan-university/kotlin__gradle_project)

---

## 🎯 1. Project Purpose & Objectives

The **Library Management System** is an interactive, layered console application written in idiomatic Kotlin and built with Gradle. It is designed to model core real-world library operations: managing book cataloging, patron registrations, borrowing workflows with domain constraint validation, return tracking, multi-field search, and patron accountability.

### Core Objectives:
1. Demonstrate clean, test-driven **Kotlin** fundamentals (data classes, interfaces, companion objects, extension functions, immutability, pattern matching with `when`).
2. Implement **Layered Software Architecture** (Presentation / Service / Repository / Domain Models).
3. Identify flaws, edge cases, and design bottlenecks in peer codebases and engineer robust, maintainable improvements.
4. Establish comprehensive automated test coverage with JUnit 5.

---

## 🏛️ 2. Architectural Design

The application follows a clean separation of concerns:

```
org.dbu.library
├── model/
│   ├── Book.kt               # Immutable data class representing book entities
│   └── Patron.kt             # Immutable data class representing patron accounts
├── repository/
│   ├── LibraryRepository.kt         # Data access contract
│   └── InMemoryLibraryRepository.kt # In-memory storage backed by Concurrent-friendly Maps
├── service/
│   ├── LibraryService.kt            # Core business logic contract
│   ├── DefaultLibraryService.kt     # Business rules (borrow limits, availability checks)
│   └── BorrowResult.kt              # Domain status enum (SUCCESS, NOT_AVAILABLE, LIMIT_REACHED, etc.)
├── ui/
│   ├── ConsoleApp.kt                # Application entry point & seed data
│   ├── Menu.kt                      # Terminal UI menu renderer
│   └── MenuHandler.kt               # User input controller and action dispatcher
└── util/
    └── Extensions.kt                # Kotlin extension functions for rich formatting
```

---

## 📦 3. Original vs. Proposed & Implemented Features

| Feature | Original Base | Previous Solution (`haset-T`) | Our Enhanced Solution |
| :--- | :--- | :--- | :--- |
| **Add Book** | Incomplete TODO | Basic (ignored return value; duplicate bug) | **Input validation** (ISBN, title, author, year range) + duplicate detection |
| **Register Patron** | Incomplete TODO | Basic (ignored return value; duplicate bug) | **Duplicate ID check** + whitespace validation |
| **Borrow Book** | Incomplete TODO | 5-book limit check, basic text output | **Detailed informative feedback** on all 5 borrow outcomes |
| **Return Book** | Incomplete TODO | Basic boolean output | **Safe check** ensuring patron actually holds the book + clear messages |
| **Search Catalog** | Incomplete TODO | Added ISBN to title/author search | **Multi-attribute search** (title, author, ISBN) with case-insensitive token matching |
| **List Catalog** | Incomplete TODO | Displayed title/author/year | **Tabular catalog formatting** with status badges `[✓]` & `[✗]` |
| **List Patrons** | ❌ Missing | ❌ Missing | **Implemented (Option 7)**: Shows all patrons, borrowed counts, and book titles |
| **Automated Tests** | ❌ Missing | ❌ None | **13 comprehensive JUnit 5 unit tests** |

---

## 🌟 4. Deep-Dive: Key Improvements Implemented

### 1. Elimination of Silent Duplication Bugs
- **Problem in Studied Solution**: In `haset-T`'s code, calling `addBook` and `registerPatron` ignored the boolean response from repository/service. When a user added an already existing ISBN or patron ID, the app falsely printed `"Book added successfully"` or `"Patron registered successfully"`.
- **Solution**: We inspect the operation's return value and give explicit error diagnostics:
  ```kotlin
  val added = service.addBook(book)
  if (added) {
      println("✓ Book '$title' (ISBN: $isbn) added successfully.")
  } else {
      println("✗ Error: A book with ISBN '$isbn' already exists.")
  }
  ```

### 2. Comprehensive Input Validation
- Prevented blank or whitespace-only inputs for titles, authors, and IDs.
- Added numeric sanity boundaries for publication year (must be a valid 4-digit year between 1000 and 2030), preventing negative numbers or runtime format crashes.

### 3. Patron Management & Accountability ("View All Patrons")
- The original exercise instructions specified *"View all patrons and their borrowed books"*, but neither the starter code nor previous student solutions implemented it.
- We added `fun getAllPatrons(): List<Patron>` to `LibraryRepository` and `InMemoryLibraryRepository`.
- Added Option `7 | List All Patrons` displaying:
  ```
  ==================== REGISTERED PATRONS (2 Patrons) ====================
  [ID: P1] Alice | Borrowed (1/5): 1
     └─ Borrowed titles: Clean Code
  [ID: P100] John Doe | Borrowed (0/5): None
  =======================================================================
  ```

### 4. Rich Kotlin Extension Functions
- Extended `Book` and `Patron` models in `Extensions.kt`:
  - `Book.formattedStatus()`: outputs clean status indicators (`Available [✓]` vs `Borrowed [✗]`).
  - `Patron.summary()`: provides human-readable quota usage (`Borrowed (1/5): ...`).

### 5. Automated Test Suite (100% Logic Coverage)
- Implemented `LibraryServiceTest.kt` with 13 isolated test cases covering all edge cases:
  - Unique ISBN / Patron ID constraints
  - Borrowing unavailable books
  - Exceeding the 5-book borrowing limit
  - Returning un-borrowed books
  - Case-insensitive searching across title, author, and ISBN
  - Empty search inputs

---

## 🚀 5. Setup & Execution Instructions

### Prerequisites
- **Java**: JDK 21 or higher (Tested with OpenJDK / Oracle JDK 21 & 26).
- **Gradle**: Gradle 8.5+ or use the included `./gradlew` wrapper.

### Build the Project
```bash
# Clone the repository
git clone https://github.com/Ruhama27/kotlin__gradle_project.git
cd kotlin__gradle_project

# Run unit tests
./gradlew test

# Build executable Fat JAR
./gradlew build
```

### Run the Application

#### Option A: Run directly via Gradle Wrapper
```bash
./gradlew run --console=plain
```

#### Option B: Run via Generated JAR
```bash
java -jar app/build/libs/app.jar
```

---

## 💻 6. Example Terminal Session

```text
================ LIBRARY MENU ================
 1 | Add Book
 2 | Register Patron
 3 | Borrow Book
 4 | Return Book
 5 | Search
 6 | List All Books
 7 | List All Patrons
 0 | Exit
==============================================
 Enter option: 6

==================== LIBRARY CATALOG (2 Books) ====================
1        | Clean Code by Robert Martin (2008)            | Available [✓]
2        | Effective Java by Joshua Bloch (2018)         | Available [✓]
=================================================================

Enter option: 3
Patron ID: P1
ISBN: 1
✓ Book '1' successfully borrowed by patron 'P1'.

Enter option: 7

==================== REGISTERED PATRONS (1 Patrons) ====================
[ID: P1] Alice | Borrowed (1/5): 1
   └─ Borrowed titles: Clean Code
=======================================================================
```

---

## 👥 7. Attribution & Credits
- **Original Course Assignment**: Debre Birhan University (DBU) Mobile Application Course.
- **Studied Student Fork**: [haset-T/kotlin__gradle_project](https://github.com/haset-T)
- **Improvements & Documentation**: [Ruhama27](https://github.com/Ruhama27)
