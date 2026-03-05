package terminal

class TerminalBuffer(
    var width: Int,
    var height: Int,
    val maxScrollbackSize: Int = 1000,
) {
    private val screen: MutableList<TerminalLine> = MutableList(height) { TerminalLine(width) }
    private val scrollback: ArrayDeque<TerminalLine> = ArrayDeque()
    private val cursor = CursorPosition(0, 0)
    var currentStyle: TextStyle = TextStyle.DEFAULT

    val cursorCol: Int get() = cursor.col
    val cursorRow: Int get() = cursor.row

    fun setCursorPosition(
        col: Int,
        row: Int,
    ) {
        cursor.col = col
        cursor.row = row
        cursor.clamp(width - 1, height - 1)
    }

    fun moveCursorRight(n: Int) = cursor.moveRight(n, width - 1)

    fun moveCursorLeft(n: Int) = cursor.moveLeft(n)

    fun moveCursorDown(n: Int) = cursor.moveDown(n, height - 1)

    fun moveCursorUp(n: Int) = cursor.moveUp(n)

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
                if (cursor.col >= width) {
                    cursor.col = width - 1
                    break
                }
            } else {
                line.setChar(cursor.col, char, currentStyle, width = 1)
                if (cursor.col == width - 1) break
                cursor.col++
            }
        }
    }

    private fun clearWideCharAt(
        line: TerminalLine,
        col: Int,
    ) {
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
        cursor.moveRight(text.length, width - 1)
    }

    fun fill(char: Char) {
        screen[cursor.row].fill(char, currentStyle)
    }

    fun getLine(row: Int): String {
        return if (row in 0 until height) {
            screen[row].toString()
        } else if (row < 0) {
            val scrollbackIndex = scrollback.size + row
            if (scrollbackIndex in scrollback.indices) {
                scrollback[scrollbackIndex].toString()
            } else {
                ""
            }
        } else {
            ""
        }
    }

    fun getCharAt(
        col: Int,
        row: Int,
    ): Char {
        val line = getTerminalLine(row) ?: return ' '
        return if (col in 0 until line.width) line.charAt(col) else ' '
    }

    fun getStyleAt(
        col: Int,
        row: Int,
    ): TextStyle {
        val line = getTerminalLine(row) ?: return TextStyle.DEFAULT
        return if (col in 0 until line.width) line.styleAt(col) else TextStyle.DEFAULT
    }

    private fun getTerminalLine(row: Int): TerminalLine? {
        return if (row in 0 until height) {
            screen[row]
        } else if (row < 0) {
            val scrollbackIndex = scrollback.size + row
            if (scrollbackIndex in scrollback.indices) scrollback[scrollbackIndex] else null
        } else {
            null
        }
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

    fun resize(
        newWidth: Int,
        newHeight: Int,
    ) {
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
        cursor.clamp(width - 1, height - 1)
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

private const val FIRST_CJK_RADICAL = 0x2E80
private const val LAST_IDC = 0x303F
private const val FIRST_HIRAGANA = 0x3040
private const val LAST_KATAKANA = 0x30FF
private const val FIRST_BOPOMOFO = 0x3100
private const val LAST_BOPOMOFO = 0x312F
private const val FIRST_CJK_EXTENSION_A = 0x3400
private const val LAST_CJK_EXTENSION_A = 0x4DBF
private const val FIRST_CJK_UNIFIED = 0x4E00
private const val LAST_CJK_UNIFIED = 0x9FFF
private const val FIRST_HANGUL_SYLLABLE = 0xAC00
private const val LAST_HANGUL_SYLLABLE = 0xD7AF
private const val FIRST_CJK_COMPATIBILITY = 0xF900
private const val LAST_CJK_COMPATIBILITY = 0xFAFF
private const val FIRST_FULLWIDTH_FORM = 0xFF01
private const val LAST_FULLWIDTH_FORM = 0xFF60
private const val FIRST_FULLWIDTH_SIGN = 0xFFE0
private const val LAST_FULLWIDTH_SIGN = 0xFFE6

fun charDisplayWidth(c: Char): Int {
    val code = c.code
    return when {
        code in FIRST_CJK_UNIFIED..LAST_CJK_UNIFIED -> 2
        code in FIRST_CJK_EXTENSION_A..LAST_CJK_EXTENSION_A -> 2
        code in FIRST_CJK_COMPATIBILITY..LAST_CJK_COMPATIBILITY -> 2
        code in FIRST_FULLWIDTH_FORM..LAST_FULLWIDTH_FORM -> 2
        code in FIRST_FULLWIDTH_SIGN..LAST_FULLWIDTH_SIGN -> 2
        code in FIRST_CJK_RADICAL..LAST_IDC -> 2
        code in FIRST_HANGUL_SYLLABLE..LAST_HANGUL_SYLLABLE -> 2
        code in FIRST_HIRAGANA..LAST_KATAKANA -> 2
        code in FIRST_BOPOMOFO..LAST_BOPOMOFO -> 2
        else -> 1
    }
}
