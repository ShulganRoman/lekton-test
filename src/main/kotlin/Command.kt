sealed interface Command {
    val name: String
    fun execute(args: String, books: MutableList<Book>)
}

class AddBook : Command {
    override val name = "ADD"

    override fun execute(args: String, books: MutableList<Book>) {
        val parts = args.split(';')
        if (parts.size != 3) {
            println("Error: format — ADD <title>;<author>;<year>")
            return
        }
        val (title, author, yearStr) = parts.map { it.trim() }
        if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty()) {
            println("Error: all fields (title, author, year) are required")
            return
        }
        val year = yearStr.toIntOrNull()
        if (year == null) {
            println("Error: year must be a valid integer")
            return
        }
        val book = Book(title, author, year)
        if (books.contains(book)) {
            println("Error: book \"$title\" by \"$author\" already exists")
            return
        }
        books.add(book)
        println("Added: $book")
    }
}

class RemoveBook : Command {
    override val name = "REMOVE"

    override fun execute(args: String, books: MutableList<Book>) {
        val id = args.trim().toIntOrNull()
        if (id == null) {
            println("Error: format — REMOVE <id>")
            return
        }
        val book = books.find { it.id == id }
        if (book == null) {
            println("Error: book with ID $id not found")
            return
        }
        books.remove(book)
        println("Removed: $book")
    }
}

class BookList : Command {
    override val name = "LIST"

    override fun execute(args: String, books: MutableList<Book>) {
        if (books.isEmpty()) {
            println("Library is empty")
            return
        }
        val sorted = when (args.trim().lowercase()) {
            "" -> books.toList()
            "title" -> books.sortedBy { it.title.lowercase() }
            "author" -> books.sortedBy { it.author.lowercase() }
            "year" -> books.sortedBy { it.year }
            else -> {
                println("Error: unknown sort option. Use: LIST [title|author|year]")
                return
            }
        }
        println("Total books: ${sorted.size}")
        sorted.forEachIndexed { i, book -> println("  ${i + 1}. $book") }
    }
}

class FindBook : Command {
    override val name = "FIND"

    override fun execute(args: String, books: MutableList<Book>) {
        if (args.isBlank()) {
            println("Error: format — FIND <query>")
            return
        }
        val query = args.trim().lowercase()
        val results = books.filter {
            it.title.lowercase().contains(query) || it.author.lowercase().contains(query)
        }
        if (results.isEmpty()) {
            println("No books found for \"${args.trim()}\"")
        } else {
            println("Found ${results.size} book(s):")
            results.forEach { println("  $it") }
        }
    }
}

class BooksStats : Command {
    override val name = "STATS"

    override fun execute(args: String, books: MutableList<Book>) {
        if (books.isEmpty()) {
            println("Library is empty")
            return
        }
        println("Total books: ${books.size}")
        println("Oldest: ${books.minBy { it.year }}")
        println("Newest: ${books.maxBy { it.year }}")
        println("Top-3 authors:")
        books.groupBy { it.author }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.take(3)
            .forEach { (author, count) -> println("  $author — $count book(s)") }
    }
}
