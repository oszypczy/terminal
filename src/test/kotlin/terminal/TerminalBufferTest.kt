package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TerminalBufferTest {

    @Test
    fun `buffer initializes with correct dimensions`() {
        val buf = TerminalBuffer(80, 24)
        assertThat(buf.width).isEqualTo(80)
        assertThat(buf.height).isEqualTo(24)
    }

    @Test
    fun `cursor starts at 0,0`() {
        val buf = TerminalBuffer(80, 24)
        assertThat(buf.cursorCol).isEqualTo(0)
        assertThat(buf.cursorRow).isEqualTo(0)
    }

    @Test
    fun `set cursor position`() {
        val buf = TerminalBuffer(80, 24)
        buf.setCursorPosition(10, 5)
        assertThat(buf.cursorCol).isEqualTo(10)
        assertThat(buf.cursorRow).isEqualTo(5)
    }

    @Test
    fun `set cursor clamps to screen bounds`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(100, 100)
        assertThat(buf.cursorCol).isEqualTo(9)
        assertThat(buf.cursorRow).isEqualTo(4)
    }

    @Test
    fun `set cursor clamps negative to zero`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(-5, -3)
        assertThat(buf.cursorCol).isEqualTo(0)
        assertThat(buf.cursorRow).isEqualTo(0)
    }

    @Test
    fun `screen starts empty (spaces)`() {
        val buf = TerminalBuffer(5, 3)
        assertThat(buf.getScreenContent()).isEqualTo("\n\n")
    }

    @Test
    fun `write text at cursor position`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("Hello")
        assertThat(buf.getLine(0)).isEqualTo("Hello")
        assertThat(buf.cursorCol).isEqualTo(5)
        assertThat(buf.cursorRow).isEqualTo(0)
    }

    @Test
    fun `write uses current style`() {
        val buf = TerminalBuffer(10, 3)
        buf.currentStyle = TextStyle(bold = true, foreground = TerminalColor.RED)
        buf.write("Hi")
        assertThat(buf.getStyleAt(0, 0).bold).isTrue()
        assertThat(buf.getStyleAt(0, 0).foreground).isEqualTo(TerminalColor.RED)
        assertThat(buf.getStyleAt(1, 0).bold).isTrue()
    }

    @Test
    fun `write stops at right edge`() {
        val buf = TerminalBuffer(5, 3)
        buf.write("HelloWorld")
        assertThat(buf.getLine(0)).isEqualTo("Hello")
        assertThat(buf.cursorCol).isEqualTo(4)
    }

    @Test
    fun `write at middle of line overwrites`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("HelloWorld")
        buf.setCursorPosition(5, 0)
        buf.write("XY")
        assertThat(buf.getLine(0)).isEqualTo("HelloXYrld")
    }

    @Test
    fun `write empty string does nothing`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("")
        assertThat(buf.cursorCol).isEqualTo(0)
    }

    @Test
    fun `insert text shifts content right`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("Hello")
        buf.setCursorPosition(2, 0)
        buf.insert("XY")
        assertThat(buf.getLine(0)).isEqualTo("HeXYllo")
        assertThat(buf.cursorCol).isEqualTo(4)
    }

    @Test
    fun `insert truncates at right edge`() {
        val buf = TerminalBuffer(5, 3)
        buf.write("ABCDE")
        buf.setCursorPosition(2, 0)
        buf.insert("XY")
        assertThat(buf.getLine(0)).isEqualTo("ABXYC")
        assertThat(buf.cursorCol).isEqualTo(4)
    }

    @Test
    fun `fill line with character`() {
        val buf = TerminalBuffer(5, 3)
        buf.setCursorPosition(0, 1)
        buf.currentStyle = TextStyle(foreground = TerminalColor.GREEN)
        buf.fill('=')
        assertThat(buf.getLine(1)).isEqualTo("=====")
        assertThat(buf.getStyleAt(0, 1).foreground).isEqualTo(TerminalColor.GREEN)
    }

    @Test
    fun `fill does not move cursor`() {
        val buf = TerminalBuffer(5, 3)
        buf.setCursorPosition(3, 1)
        buf.fill('-')
        assertThat(buf.cursorCol).isEqualTo(3)
        assertThat(buf.cursorRow).isEqualTo(1)
    }

    @Test
    fun `fill with space clears line`() {
        val buf = TerminalBuffer(5, 3)
        buf.write("Hello")
        buf.fill(' ')
        assertThat(buf.getLine(0)).isEqualTo("")
    }

    @Test
    fun `clearScreen resets screen but preserves scrollback`() {
        val buf = TerminalBuffer(10, 3, maxScrollbackSize = 100)
        buf.write("Hello")
        buf.insertLineAtBottom()
        buf.setCursorPosition(0, 0)
        buf.write("World")

        buf.clearScreen()

        assertThat(buf.getScreenContent()).isEqualTo("\n\n")
        assertThat(buf.cursorCol).isEqualTo(0)
        assertThat(buf.cursorRow).isEqualTo(0)
        assertThat(buf.scrollbackSize).isEqualTo(1)
    }

    @Test
    fun `clearAll resets everything`() {
        val buf = TerminalBuffer(10, 3, maxScrollbackSize = 100)
        buf.write("Hello")
        buf.insertLineAtBottom()
        buf.setCursorPosition(0, 0)
        buf.write("World")

        buf.clearAll()

        assertThat(buf.getScreenContent()).isEqualTo("\n\n")
        assertThat(buf.scrollbackSize).isEqualTo(0)
        assertThat(buf.cursorCol).isEqualTo(0)
        assertThat(buf.cursorRow).isEqualTo(0)
    }
}
