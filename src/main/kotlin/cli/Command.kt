package cli

import Book
import exception.BookAlreadyExistException
import exception.IllegalBookAddArgumentException
import java.util.concurrent.ConcurrentSkipListSet

data class ValidationError(val message: String)

data class ValidationResult<V>(
    val value: V?,
    val errors: List<ValidationError>
)

sealed interface Command {
    val name: String
    fun execute(args: String, books: ConcurrentSkipListSet<Book>)
}

class AddBook : Command {
    override val name = "ADD"
    override fun execute(args: String, books: ConcurrentSkipListSet<Book>) {
        val result = validate(args)

        if (result.value == null) {
            throw IllegalBookAddArgumentException(
                result.errors.joinToString(
                    separator = ", ",
                    prefix = "",
                    postfix = "."
                )
            )
        } else {
            val value = result.value

            val book = Book(value[0], value[1], value[2].toInt())

            if (!books.add(book)) {
                throw BookAlreadyExistException("Book already exists")
            }

            books.add(book)
        }
    }

    private fun validate(input: String): ValidationResult<List<String>> {
        val errors = mutableListOf<ValidationError>()

        val bookInfo = input.split(';')

        if (bookInfo.size != 3) {
            errors.add(ValidationError("The `ADD` command supports three parameters; you have entered ${bookInfo.size} parameters."))
            return ValidationResult(null, errors)
        }

        if (bookInfo[0].isEmpty()) errors.add(ValidationError("book must have an author"))
        if (bookInfo[1].isEmpty()) errors.add(ValidationError("book must have a title"))
        if (bookInfo[2].isEmpty()) errors.add(ValidationError("the book must have a publication year"))
        if (bookInfo[2].matches(Regex("\\d+"))) errors.add(ValidationError("invalid publication year"))

        if (errors.isEmpty()) return ValidationResult(null, errors)

        errors.add(0, ValidationError("The `ADD` command only supports the following format: `ADD title;author;year`."))
        return ValidationResult(null, errors)
    }
}

class RemoveBook : Command {
    override val name = "REMOVE"
    override fun execute(args: String, books: ConcurrentSkipListSet<Book>) {}
}

class BookList : Command {
    override val name = "LIST"
    override fun execute(args: String, books: ConcurrentSkipListSet<Book>) {
        books.addAll(books)
    }
}

class FindBook : Command {
    override val name = "FIND"
    override fun execute(args: String, books: ConcurrentSkipListSet<Book>) {}
}

class BooksStats : Command {
    override val name = "STATS"
    override fun execute(args: String, books: ConcurrentSkipListSet<Book>) {}
}