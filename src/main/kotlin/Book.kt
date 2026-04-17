data class Book(val title: String, val author: String, val year: Int) {
    private val id: Int = nextId++

    companion object {
        private var nextId = 1
    }
}