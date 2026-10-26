package org.dbu.library

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.InMemoryLibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.DefaultLibraryService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LibraryServiceTest {

    private lateinit var repository: InMemoryLibraryRepository
    private lateinit var service: DefaultLibraryService

    @BeforeEach
    fun setUp() {
        repository = InMemoryLibraryRepository()
        service = DefaultLibraryService(repository)
    }

    @Test
    fun testAddBookAndRetrieve() {
        val book = Book("101", "Kotlin in Action", "Dmitry Jemerov", 2017)
        assertTrue(service.addBook(book))
        assertFalse(service.addBook(book), "Adding same ISBN again should fail")
        assertEquals(book, repository.findBook("101"))
    }

    @Test
    fun testAddPatron() {
        val patron = Patron("P100", "John Doe")
        assertTrue(repository.addPatron(patron))
        assertFalse(repository.addPatron(patron), "Adding same ID again should fail")
        assertEquals(patron, repository.findPatron("P100"))
    }

    @Test
    fun testBorrowBookSuccess() {
        val book = Book("101", "Kotlin in Action", "Dmitry Jemerov", 2017)
        val patron = Patron("P100", "John Doe")
        repository.addBook(book)
        repository.addPatron(patron)

        val result = service.borrowBook("P100", "101")
        assertEquals(BorrowResult.SUCCESS, result)

        val updatedBook = repository.findBook("101")
        assertNotNull(updatedBook)
        assertFalse(updatedBook!!.isAvailable)

        val updatedPatron = repository.findPatron("P100")
        assertNotNull(updatedPatron)
        assertTrue(updatedPatron!!.borrowedBooks.contains("101"))
    }

    @Test
    fun testBorrowBookNotFound() {
        val patron = Patron("P100", "John Doe")
        repository.addPatron(patron)

        val result = service.borrowBook("P100", "999")
        assertEquals(BorrowResult.BOOK_NOT_FOUND, result)
    }

    @Test
    fun testBorrowPatronNotFound() {
        val book = Book("101", "Kotlin in Action", "Dmitry Jemerov", 2017)
        repository.addBook(book)

        val result = service.borrowBook("NON_EXISTENT", "101")
        assertEquals(BorrowResult.PATRON_NOT_FOUND, result)
    }

    @Test
    fun testBorrowBookNotAvailable() {
        val book = Book("101", "Kotlin in Action", "Dmitry Jemerov", 2017)
        val patron1 = Patron("P1", "Alice")
        val patron2 = Patron("P2", "Bob")
        repository.addBook(book)
        repository.addPatron(patron1)
        repository.addPatron(patron2)

        assertEquals(BorrowResult.SUCCESS, service.borrowBook("P1", "101"))
        assertEquals(BorrowResult.NOT_AVAILABLE, service.borrowBook("P2", "101"))
    }

    @Test
    fun testBorrowBookLimitReached() {
        val patron = Patron("P1", "Alice", listOf("B1", "B2", "B3", "B4", "B5"))
        val book = Book("B6", "New Book", "Author", 2024)
        repository.addPatron(patron)
        repository.addBook(book)

        val result = service.borrowBook("P1", "B6")
        assertEquals(BorrowResult.LIMIT_REACHED, result)
    }

    @Test
    fun testReturnBookSuccess() {
        val book = Book("101", "Kotlin in Action", "Dmitry Jemerov", 2017)
        val patron = Patron("P100", "John Doe")
        repository.addBook(book)
        repository.addPatron(patron)

        service.borrowBook("P100", "101")
        val returnResult = service.returnBook("P100", "101")
        assertTrue(returnResult)

        val updatedBook = repository.findBook("101")
        assertNotNull(updatedBook)
        assertTrue(updatedBook!!.isAvailable)

        val updatedPatron = repository.findPatron("P100")
        assertNotNull(updatedPatron)
        assertFalse(updatedPatron!!.borrowedBooks.contains("101"))
    }

    @Test
    fun testReturnBookNotBorrowed() {
        val book = Book("101", "Kotlin in Action", "Dmitry Jemerov", 2017)
        val patron = Patron("P100", "John Doe")
        repository.addBook(book)
        repository.addPatron(patron)

        assertFalse(service.returnBook("P100", "101"))
    }

    @Test
    fun testSearchBooks() {
        repository.addBook(Book("1", "Clean Code", "Robert Martin", 2008))
        repository.addBook(Book("2", "Clean Architecture", "Robert Martin", 2017))
        repository.addBook(Book("3", "Effective Java", "Joshua Bloch", 2018))

        val resultsTitle = service.search("clean")
        assertEquals(2, resultsTitle.size)

        val resultsAuthor = service.search("bloch")
        assertEquals(1, resultsAuthor.size)
        assertEquals("Effective Java", resultsAuthor[0].title)

        val resultsEmpty = service.search("nonexistent")
        assertTrue(resultsEmpty.isEmpty())
    }

    @Test
    fun testSearchByIsbn() {
        repository.addBook(Book("978-0132350884", "Clean Code", "Robert Martin", 2008))
        val results = service.search("978-0132350884")
        assertEquals(1, results.size)
        assertEquals("Clean Code", results[0].title)
    }

    @Test
    fun testSearchEmptyQueryReturnsEmpty() {
        repository.addBook(Book("1", "Clean Code", "Robert Martin", 2008))
        assertTrue(service.search("   ").isEmpty())
    }

    @Test
    fun testGetAllPatrons() {
        repository.addPatron(Patron("P1", "Alice"))
        repository.addPatron(Patron("P2", "Bob"))
        val patrons = repository.getAllPatrons()
        assertEquals(2, patrons.size)
    }
}
