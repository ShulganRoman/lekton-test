import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class CommandsTest {

    private lateinit var books: MutableList<Book>
    private val out = ByteArrayOutputStream()
    private val originalOut = System.out

    @BeforeEach
    fun setUp() {
        books = mutableListOf()
        System.setOut(PrintStream(out))
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        System.setOut(originalOut)
        out.reset()
    }

    private fun output() = out.toString().trim()

    @Test
    fun `add valid book`() {
        AddBook().execute("Как я провёл лето в подвале;Дядя Фёдор;2003", books)
        assertEquals(1, books.size)
        assertEquals("Как я провёл лето в подвале", books[0].title)
        assertEquals("Дядя Фёдор", books[0].author)
        assertEquals(2003, books[0].year)
        assertTrue(output().startsWith("Added:"))
    }

    @Test
    fun `add duplicate book is rejected`() {
        AddBook().execute("Записки укротителя кошек;Васисуалий Лоханкин;1999", books)
        out.reset()
        AddBook().execute("Записки укротителя кошек;Васисуалий Лоханкин;1999", books)
        assertEquals(1, books.size)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `add book with wrong number of fields prints error`() {
        AddBook().execute("Просто название без автора вообще", books)
        assertEquals(0, books.size)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `add book with non-integer year prints error`() {
        AddBook().execute("Мемуары;Бабуля;позапрошлый", books)
        assertEquals(0, books.size)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `add book with blank title prints error`() {
        AddBook().execute(";Загадочный Незнакомец;2020", books)
        assertEquals(0, books.size)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `remove existing book prints removed book info`() {
        AddBook().execute("Прощай, полка;Грустный Том;2010", books)
        val id = books[0].id
        out.reset()
        RemoveBook().execute("$id", books)
        assertEquals(0, books.size)
        assertTrue(output().startsWith("Removed:"))
    }

    @Test
    fun `remove non-existent id prints error`() {
        RemoveBook().execute("9999", books)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `remove with non-integer arg prints error`() {
        RemoveBook().execute("блокнотик", books)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `list empty library prints message`() {
        BookList().execute("", books)
        assertTrue(output().contains("empty", ignoreCase = true))
    }

    @Test
    fun `list sorted by year ascending`() {
        AddBook().execute("Древние свитки;Дедуля Архивариус;1950", books)
        AddBook().execute("Свежачок с принтера;Внучок-блогер;2020", books)
        AddBook().execute("Что-то из 90-х;Челнок Петрович;1990", books)
        out.reset()
        BookList().execute("year", books)
        val yearRegex = Regex("year=(\\d+)")
        val years = out.toString().lines()
            .mapNotNull { yearRegex.find(it)?.groupValues?.get(1)?.toInt() }
        assertEquals(listOf(1950, 1990, 2020), years)
    }

    @Test
    fun `list sorted by title alphabetically`() {
        AddBook().execute("Яхта мечты;Морской Волк;2000", books)
        AddBook().execute("Яблоко раздора;Ева Садовая;2001", books)
        out.reset()
        BookList().execute("title", books)
        val bookLines = out.toString().lines().filter { it.contains("title=") }
        assertTrue(bookLines[0].contains("Яблоко"))
        assertTrue(bookLines[1].contains("Яхта"))
    }

    @Test
    fun `find case-insensitive match by title`() {
        AddBook().execute("Котики в невесомости;Профессор Мурлыкин;2015", books)
        out.reset()
        FindBook().execute("котик", books)
        assertTrue(output().contains("Found 1"))
    }

    @Test
    fun `find case-insensitive match by author`() {
        AddBook().execute("Труды и дни;МУРЛЫКИН;2015", books)
        out.reset()
        FindBook().execute("мурлыкин", books)
        assertTrue(output().contains("Found 1"))
    }

    @Test
    fun `find returns no results when query does not match`() {
        AddBook().execute("Обычная книга;Обычный Автор;2000", books)
        out.reset()
        FindBook().execute("единорог-программист", books)
        assertTrue(output().contains("No books found"))
    }

    @Test
    fun `find with blank query prints error`() {
        FindBook().execute("   ", books)
        assertTrue(output().startsWith("Error:"))
    }

    @Test
    fun `stats on empty library prints message`() {
        BooksStats().execute("", books)
        assertTrue(output().contains("empty", ignoreCase = true))
    }

    @Test
    fun `stats shows total oldest newest and top authors`() {
        AddBook().execute("Том первый;Граф Пишучий;2008", books)
        AddBook().execute("Том второй;Граф Пишучий;2011", books)
        AddBook().execute("Чужое творчество;Завидующий Сосед;1999", books)
        out.reset()
        BooksStats().execute("", books)
        val result = output()
        assertTrue(result.contains("Total books: 3"))
        assertTrue(result.contains("Oldest:"))
        assertTrue(result.contains("1999"))
        assertTrue(result.contains("Newest:"))
        assertTrue(result.contains("2011"))
        assertTrue(result.contains("Top-3 authors:"))
        assertTrue(result.contains("Граф Пишучий"))
    }
}
