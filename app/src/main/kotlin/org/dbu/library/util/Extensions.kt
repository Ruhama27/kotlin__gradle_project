package org.dbu.library.util

import org.dbu.library.model.Book

import org.dbu.library.model.Patron

fun Book.display(): String =
    "$title by $author ($year)"

fun Book.formattedStatus(): String =
    if (isAvailable) "Available [✓]" else "Borrowed [✗]"

fun Patron.summary(): String =
    "[ID: $id] $name | Borrowed (${borrowedBooks.size}/5): ${if (borrowedBooks.isEmpty()) "None" else borrowedBooks.joinToString(", ")}"