package terminal

class TerminalLine(val width: Int) {
    internal val chars = CharArray(width) { ' ' }
    internal val styles = IntArray(width) { TextStyle.DEFAULT.packed }
    internal val widths = ByteArray(width) { 1 }

    fun charAt(col: Int): Char = chars[col]

    fun styleAt(col: Int): TextStyle = TextStyle(styles[col])

    fun widthAt(col: Int): Int = widths[col].toInt()

    fun setChar(
        col: Int,
        char: Char,
        style: TextStyle,
        width: Int = 1,
    ) {
        chars[col] = char
        styles[col] = style.packed
        widths[col] = width.toByte()
    }

    fun clear() {
        chars.fill(' ')
        styles.fill(TextStyle.DEFAULT.packed)
        widths.fill(1)
    }

    fun insertChars(
        col: Int,
        text: String,
        style: TextStyle,
    ) {
        if (col >= width || text.isEmpty()) return
        var cellsNeeded = 0
        for (c in text) {
            cellsNeeded += charDisplayWidth(c)
        }
        val insertLen = cellsNeeded.coerceAtMost(width - col)
        val shiftEnd = width - 1
        val shiftStart = col + insertLen
        for (i in shiftEnd downTo shiftStart) {
            chars[i] = chars[i - insertLen]
            styles[i] = styles[i - insertLen]
            widths[i] = widths[i - insertLen]
        }
        var pos = col
        for (c in text) {
            val cw = charDisplayWidth(c)
            if (pos + cw > col + insertLen) break
            chars[pos] = c
            styles[pos] = style.packed
            widths[pos] = cw.toByte()
            if (cw == 2 && pos + 1 < width) {
                chars[pos + 1] = '\u0000'
                styles[pos + 1] = style.packed
                widths[pos + 1] = 0
            }
            pos += cw
        }
    }

    fun fill(
        char: Char,
        style: TextStyle,
    ) {
        chars.fill(char)
        styles.fill(style.packed)
        widths.fill(1)
    }

    /** Returns line content with trailing spaces trimmed */
    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0 until width) {
            if (widths[i].toInt() != 0) { // skip continuation cells
                sb.append(chars[i])
            }
        }
        return sb.toString().trimEnd()
    }

    fun resized(newWidth: Int): TerminalLine {
        val newLine = TerminalLine(newWidth)
        val copyLen = minOf(width, newWidth)
        for (i in 0 until copyLen) {
            newLine.chars[i] = chars[i]
            newLine.styles[i] = styles[i]
            newLine.widths[i] = widths[i]
        }
        // Check for split wide char at boundary: primary cell is last col,
        // meaning its continuation would be outside the new width
        if (newWidth in 1 until width) {
            if (newLine.widths[newWidth - 1].toInt() == 2) {
                newLine.setChar(newWidth - 1, ' ', TextStyle.DEFAULT, width = 1)
            }
        }
        return newLine
    }

    /** Returns full line content including trailing spaces */
    fun toFullString(): String {
        val sb = StringBuilder()
        for (i in 0 until width) {
            if (widths[i].toInt() != 0) {
                sb.append(chars[i])
            }
        }
        return sb.toString()
    }
}
