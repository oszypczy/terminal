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
            val charWidth = charDisplayWidth(char)
            if (charWidth == 2 && cursor.col >= width - 1) break
            if (cursor.col >= width) break

            clearWideCharAt(line, cursor.col)

            if (charWidth == 2) {
                line.setChar(cursor.col, char, currentStyle, width = 2)
                if (cursor.col + 1 < width) {
                    line.setChar(cursor.col + 1, '\u0000', currentStyle, width = 0)
                }
                cursor.col += 2
                if (cursor.col >= width) { cursor.col = width - 1; break }
            } else {
                line.setChar(cursor.col, char, currentStyle, width = 1)
                if (cursor.col == width - 1) break
                cursor.col++
            }
        }
    }

    private fun clearWideCharAt(line: TerminalLine, col: Int) {
        if (col > 0 && line.widthAt(col) == 0) {
            line.setChar(col - 1, ' ', TextStyle.DEFAULT, width = 1)
            line.setChar(col, ' ', TextStyle.DEFAULT, width = 1)
        }
        if (line.widthAt(col) == 2 && col + 1 < width) {
            line.setChar(col + 1, ' ', TextStyle.DEFAULT, width = 1)
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

    fun clearScreen() {
        for (line in screen) {
            line.clear()
        }
        cursor.col = 0
        cursor.row = 0
    }

    fun clearAll() {
        clearScreen()
        scrollback.clear()
    }

    fun resize(newWidth: Int, newHeight: Int) {
        // Resize width of all lines
        if (newWidth != width) {
            for (i in screen.indices) {
                screen[i] = screen[i].resized(newWidth)
            }
            for (i in scrollback.indices) {
                scrollback[i] = scrollback[i].resized(newWidth)
            }
        }

        // Resize height
        if (newHeight < height) {
            if (cursor.row >= newHeight) {
                val rowsToScrollback = cursor.row - newHeight + 1
                repeat(rowsToScrollback) {
                    scrollback.addLast(screen.removeFirst())
                    if (scrollback.size > maxScrollbackSize) scrollback.removeFirst()
                }
                cursor.row -= rowsToScrollback
            }
            while (screen.size > newHeight) {
                screen.removeLast()
            }
        } else if (newHeight > height) {
            repeat(newHeight - height) {
                screen.add(TerminalLine(newWidth))
            }
        }

        width = newWidth
        height = newHeight
        cursor.col = cursor.col.coerceIn(0, width - 1)
        cursor.row = cursor.row.coerceIn(0, height - 1)
    }

    fun getAllContent(): String {
        val parts = mutableListOf<String>()
        for (line in scrollback) {
            parts.add(line.toString())
        }
        for (line in screen) {
            parts.add(line.toString())
        }
        return parts.joinToString("\n")
    }
}

fun charDisplayWidth(c: Char): Int {
    val code = c.code
    return when {
        code in 0x4E00..0x9FFF -> 2   // CJK Unified Ideographs
        code in 0x3400..0x4DBF -> 2   // CJK Extension A
        code in 0xF900..0xFAFF -> 2   // CJK Compatibility Ideographs
        code in 0xFF01..0xFF60 -> 2   // Fullwidth Forms
        code in 0xFFE0..0xFFE6 -> 2   // Fullwidth Signs
        code in 0x2E80..0x303F -> 2   // CJK Radicals, Kangxi, IDC
        code in 0xAC00..0xD7AF -> 2   // Hangul Syllables
        code in 0x3040..0x30FF -> 2   // Hiragana, Katakana
        code in 0x3100..0x312F -> 2   // Bopomofo
        else -> 1
    }
}
