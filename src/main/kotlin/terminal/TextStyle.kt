package terminal

@JvmInline
value class TextStyle(val packed: Int) {

    constructor(
        bold: Boolean = false,
        italic: Boolean = false,
        underline: Boolean = false,
        foreground: TerminalColor = TerminalColor.DEFAULT,
        background: TerminalColor = TerminalColor.DEFAULT
    ) : this(
        (if (bold) BOLD_BIT else 0) or
        (if (italic) ITALIC_BIT else 0) or
        (if (underline) UNDERLINE_BIT else 0) or
        (foreground.ordinal shl FG_SHIFT) or
        (background.ordinal shl BG_SHIFT)
    )

    val bold: Boolean get() = packed and BOLD_BIT != 0
    val italic: Boolean get() = packed and ITALIC_BIT != 0
    val underline: Boolean get() = packed and UNDERLINE_BIT != 0
    val foreground: TerminalColor get() = TerminalColor.entries[(packed shr FG_SHIFT) and COLOR_MASK]
    val background: TerminalColor get() = TerminalColor.entries[(packed shr BG_SHIFT) and COLOR_MASK]

    companion object {
        private const val BOLD_BIT = 1
        private const val ITALIC_BIT = 2
        private const val UNDERLINE_BIT = 4
        private const val FG_SHIFT = 3
        private const val BG_SHIFT = 8
        private const val COLOR_MASK = 0x1F // 5 bits

        val DEFAULT = TextStyle(0)
    }
}
