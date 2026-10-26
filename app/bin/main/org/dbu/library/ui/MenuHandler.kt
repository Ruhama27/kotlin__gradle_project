package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService

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

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}
private fun addBook(service: LibraryService) {
    print("ISBN: ")
    val isbn = readln()
    print("Title: ")
    val title = readln()
    print("Author: ")
    val author = readln()
    print("Year: ")
    val year = readln().toIntOrNull()
    if (year == null) {
        println("Invalid year")
        return
    }
    val book = Book(isbn, title, author, year)
    val added = service.addBook(book)
    println("Book added: $added")
}

private fun registerPatron(repository: LibraryRepository) {
    print("Patron ID: ")
    val id = readln()
    print("Name: ")
    val name = readln()
    val patron = Patron(id, name)
    val added = repository.addPatron(patron)
    println("Patron added: $added")
}

private fun borrowBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln()
    print("ISBN: ")
    val isbn = readln()
    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS -> println("Borrowed")
        BorrowResult.BOOK_NOT_FOUND -> println("Book not found")
        BorrowResult.PATRON_NOT_FOUND -> println("Patron not found")
        BorrowResult.NOT_AVAILABLE -> println("Book not available")
        BorrowResult.LIMIT_REACHED -> println("Limit reached")
    }
}

private fun returnBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln()
    print("ISBN: ")
    val isbn = readln()
    val result = service.returnBook(patronId, isbn)
    if (result) {
        println("Returned")
    } else {
        println("Return failed")
    }
}

private fun search(service: LibraryService) {
    print("Query: ")
    val results = service.search(readln())
    if (results.isEmpty()) {
        println("No books found")
        return
    }
    results.forEachIndexed { index, book ->
        println("${index + 1}. ${book.title} - ${book.author}")
    }
}

private fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    if (books.isEmpty()) {
        println("No books in library")
        return
    }
    books.forEach {
        val status = if (it.isAvailable) "Available" else "Borrowed"
        println("${it.isbn} | ${it.title} | $status")
    }
}