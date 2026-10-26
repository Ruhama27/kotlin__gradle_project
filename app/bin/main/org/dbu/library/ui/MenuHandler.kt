package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService
import org.dbu.library.util.display
import org.dbu.library.util.formattedStatus
import org.dbu.library.util.summary

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "7" -> {
            listAllPatrons(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option. Please choose from 0 to 7.")
            true
        }
    }
}

private fun addBook(service: LibraryService) {
    print("ISBN: ")
    val isbn = readln().trim()
    if (isbn.isEmpty()) {
        println("✗ Error: ISBN cannot be empty.")
        return
    }

    print("Title: ")
    val title = readln().trim()
    if (title.isEmpty()) {
        println("✗ Error: Title cannot be empty.")
        return
    }

    print("Author: ")
    val author = readln().trim()
    if (author.isEmpty()) {
        println("✗ Error: Author cannot be empty.")
        return
    }

    print("Year: ")
    val yearInput = readln().trim()
    val year = yearInput.toIntOrNull()
    if (year == null || year < 1000 || year > 2030) {
        println("✗ Error: Invalid year. Please enter a 4-digit year.")
        return
    }

    val book = Book(isbn, title, author, year)
    val added = service.addBook(book)
    if (added) {
        println("✓ Book '$title' (ISBN: $isbn) added successfully.")
    } else {
        println("✗ Error: A book with ISBN '$isbn' already exists.")
    }
}

private fun registerPatron(repository: LibraryRepository) {
    print("Patron ID: ")
    val id = readln().trim()
    if (id.isEmpty()) {
        println("✗ Error: Patron ID cannot be empty.")
        return
    }

    print("Name: ")
    val name = readln().trim()
    if (name.isEmpty()) {
        println("✗ Error: Name cannot be empty.")
        return
    }

    val patron = Patron(id, name)
    val added = repository.addPatron(patron)
    if (added) {
        println("✓ Patron '$name' (ID: $id) registered successfully.")
    } else {
        println("✗ Error: A patron with ID '$id' is already registered.")
    }
}

private fun borrowBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln().trim()
    if (patronId.isEmpty()) {
        println("✗ Error: Patron ID cannot be empty.")
        return
    }

    print("ISBN: ")
    val isbn = readln().trim()
    if (isbn.isEmpty()) {
        println("✗ Error: ISBN cannot be empty.")
        return
    }

    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS ->
            println("✓ Book '$isbn' successfully borrowed by patron '$patronId'.")
        BorrowResult.BOOK_NOT_FOUND ->
            println("✗ Error: Book with ISBN '$isbn' not found in library.")
        BorrowResult.PATRON_NOT_FOUND ->
            println("✗ Error: Patron with ID '$patronId' not registered.")
        BorrowResult.NOT_AVAILABLE ->
            println("✗ Error: Book '$isbn' is currently borrowed by another patron.")
        BorrowResult.LIMIT_REACHED ->
            println("✗ Error: Patron has reached the maximum borrowing limit of 5 books.")
    }
}

private fun returnBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln().trim()
    if (patronId.isEmpty()) {
        println("✗ Error: Patron ID cannot be empty.")
        return
    }

    print("ISBN: ")
    val isbn = readln().trim()
    if (isbn.isEmpty()) {
        println("✗ Error: ISBN cannot be empty.")
        return
    }

    val result = service.returnBook(patronId, isbn)
    if (result) {
        println("✓ Book '$isbn' successfully returned by patron '$patronId'.")
    } else {
        println("✗ Error: Return failed. Ensure patron ID is valid and that this book was borrowed by them.")
    }
}

private fun search(service: LibraryService) {
    print("Enter search keyword (title, author, or ISBN): ")
    val query = readln().trim()
    if (query.isEmpty()) {
        println("✗ Error: Search query cannot be empty.")
        return
    }

    val results = service.search(query)
    if (results.isEmpty()) {
        println("No books found matching '$query'.")
        return
    }

    println("\n--- Search Results (${results.size} found) ---")
    results.forEachIndexed { index, book ->
        println("${index + 1}. ${book.display()} | ISBN: ${book.isbn} | ${book.formattedStatus()}")
    }
}

private fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    if (books.isEmpty()) {
        println("No books in library.")
        return
    }

    println("\n==================== LIBRARY CATALOG (${books.size} Books) ====================")
    books.forEach { book ->
        println("${book.isbn.padEnd(8)} | ${book.display().padEnd(45)} | ${book.formattedStatus()}")
    }
    println("=================================================================")
}

private fun listAllPatrons(repository: LibraryRepository) {
    val patrons = repository.getAllPatrons()
    if (patrons.isEmpty()) {
        println("No patrons currently registered.")
        return
    }

    println("\n==================== REGISTERED PATRONS (${patrons.size} Patrons) ====================")
    patrons.forEach { patron ->
        println(patron.summary())
        if (patron.borrowedBooks.isNotEmpty()) {
            val titles = patron.borrowedBooks.mapNotNull { isbn -> repository.findBook(isbn)?.title ?: isbn }
            println("   └─ Borrowed titles: ${titles.joinToString(", ")}")
        }
    }
    println("=======================================================================")
}