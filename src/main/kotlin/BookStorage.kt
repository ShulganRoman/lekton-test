import cli.Command

class BookRepository {
    private val books = java.util.concurrent.ConcurrentSkipListSet<Book>()

    fun execute(command: Command, args: String) {
        command.execute(args, books)
    }
}