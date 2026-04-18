fun main() {
    val repo = BookRepository()
    println("=== Library ===")
    println("Commands: ADD <title>;<author>;<year> | REMOVE <id> | LIST [title|author|year] | FIND <query> | STATS | EXIT")

    while (true) {
        print("> ")
        val line = readlnOrNull()?.trim() ?: continue
        if (line.isEmpty()) continue

        val spaceIdx = line.indexOf(' ')
        val commandName = (if (spaceIdx == -1) line else line.substring(0, spaceIdx)).uppercase()
        val args = if (spaceIdx == -1) "" else line.substring(spaceIdx + 1)

        if (commandName == "EXIT") {
            println("Goodbye!")
            break
        }

        repo.execute(commandName, args)
    }
}
