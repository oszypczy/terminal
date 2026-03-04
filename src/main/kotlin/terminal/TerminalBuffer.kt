package terminal

class TerminalBuffer(
    var width: Int,
    var height: Int,
    val maxScrollbackSize: Int = 1000
) {
    private val screen: MutableList<TerminalLine> = MutableList(height) { TerminalLine(width) }
    private val scrollback: ArrayDeque<TerminalLine> = ArrayDeque()
    private val cursor = CursorPosition(0, 0)
    var currentStyle: TextStyle = TextStyle.DEFAULT

    val cursorCol: Int get() = cursor.col
    val cursorRow: Int get() = cursor.row

    fun setCursorPosition(col: Int, row: Int) {
        cursor.col = col.coerceIn(0, width - 1)
        cursor.row = row.coerceIn(0, height - 1)
    }

    fun getScreenContent(): String {
        return screen.joinToString("\n") { it.toString() }
    }
}
