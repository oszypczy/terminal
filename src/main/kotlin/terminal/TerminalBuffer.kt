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

    fun moveCursorRight(n: Int) {
        cursor.col = (cursor.col + n).coerceAtMost(width - 1)
    }

    fun moveCursorLeft(n: Int) {
        cursor.col = (cursor.col - n).coerceAtLeast(0)
    }

    fun moveCursorDown(n: Int) {
        cursor.row = (cursor.row + n).coerceAtMost(height - 1)
    }

    fun moveCursorUp(n: Int) {
        cursor.row = (cursor.row - n).coerceAtLeast(0)
    }

    fun write(text: String) {
        if (text.isEmpty()) return
        val line = screen[cursor.row]
        for (char in text) {
            if (cursor.col >= width) break
            line.setChar(cursor.col, char, currentStyle)
            if (cursor.col == width - 1) break
            cursor.col++
        }
    }

    fun insert(text: String) {
        if (text.isEmpty()) return
        val line = screen[cursor.row]
        line.insertChars(cursor.col, text, currentStyle)
        cursor.col = (cursor.col + text.length).coerceAtMost(width - 1)
    }

    fun fill(char: Char) {
        screen[cursor.row].fill(char, currentStyle)
    }

    fun getLine(row: Int): String {
        return if (row in 0 until height) {
            screen[row].toString()
        } else if (row < 0) {
            val scrollbackIndex = scrollback.size + row
            if (scrollbackIndex in scrollback.indices) scrollback[scrollbackIndex].toString()
            else ""
        } else ""
    }

    fun getCharAt(col: Int, row: Int): Char {
        val line = getTerminalLine(row) ?: return ' '
        return if (col in 0 until line.width) line.charAt(col) else ' '
    }

    fun getStyleAt(col: Int, row: Int): TextStyle {
        val line = getTerminalLine(row) ?: return TextStyle.DEFAULT
        return if (col in 0 until line.width) line.styleAt(col) else TextStyle.DEFAULT
    }

    private fun getTerminalLine(row: Int): TerminalLine? {
        return if (row in 0 until height) {
            screen[row]
        } else if (row < 0) {
            val scrollbackIndex = scrollback.size + row
            if (scrollbackIndex in scrollback.indices) scrollback[scrollbackIndex] else null
        } else null
    }

    val scrollbackSize: Int get() = scrollback.size

    fun insertLineAtBottom() {
        val topLine = screen.removeFirst()
        scrollback.addLast(topLine)
        if (scrollback.size > maxScrollbackSize) {
            scrollback.removeFirst()
        }
        screen.add(TerminalLine(width))
        cursor.col = 0
    }

    fun getScreenContent(): String {
        return screen.joinToString("\n") { it.toString() }
    }
}
