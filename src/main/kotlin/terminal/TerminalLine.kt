package terminal

class TerminalLine(val width: Int) {
    internal val chars = CharArray(width) { ' ' }
    internal val styles = IntArray(width) { TextStyle.DEFAULT.packed }
    internal val widths = ByteArray(width) { 1 }

    fun charAt(col: Int): Char = chars[col]
    fun styleAt(col: Int): TextStyle = TextStyle(styles[col])
    fun widthAt(col: Int): Int = widths[col].toInt()

    fun setChar(col: Int, char: Char, style: TextStyle, width: Int = 1) {
        chars[col] = char
        styles[col] = style.packed
        widths[col] = width.toByte()
    }

    fun clear() {
        chars.fill(' ')
        styles.fill(TextStyle.DEFAULT.packed)
        widths.fill(1)
    }

    fun fill(char: Char, style: TextStyle) {
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
