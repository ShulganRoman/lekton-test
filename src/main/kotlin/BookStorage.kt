class BookRepository {
    private val books = mutableListOf<Book>()
    private val commands: Map<String, Command> = listOf(
        AddBook(), RemoveBook(), BookList(), FindBook(), BooksStats()
    ).associateBy { it.name }

    fun execute(commandName: String, args: String) {
        val command = commands[commandName]
        if (command == null) {
            println("Error: unknown command \"$commandName\". Available commands: ADD, REMOVE, LIST, FIND, STATS, EXIT")
            return
        }
        command.execute(args, books)
    }
}
