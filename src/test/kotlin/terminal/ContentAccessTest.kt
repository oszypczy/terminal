package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContentAccessTest {
    @Test
    fun `getScreenContent returns all screen lines`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("Line0")
        buf.setCursorPosition(0, 1)
        buf.write("Line1")
        buf.setCursorPosition(0, 2)
        buf.write("Line2")
        assertThat(buf.getScreenContent()).isEqualTo("Line0\nLine1\nLine2")
    }

    @Test
    fun `getAllContent returns scrollback and screen`() {
        val buf = TerminalBuffer(10, 2, maxScrollbackSize = 100)
        buf.write("Scrolled")
        buf.insertLineAtBottom()
        buf.setCursorPosition(0, 0)
        buf.write("Visible0")
        buf.setCursorPosition(0, 1)
        buf.write("Visible1")
        assertThat(buf.getAllContent()).isEqualTo("Scrolled\nVisible0\nVisible1")
    }

    @Test
    fun `getAllContent with no scrollback equals getScreenContent`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("Test")
        assertThat(buf.getAllContent()).isEqualTo(buf.getScreenContent())
    }

    @Test
    fun `getLine returns empty string for out of bounds`() {
        val buf = TerminalBuffer(10, 3)
        assertThat(buf.getLine(100)).isEqualTo("")
        assertThat(buf.getLine(-100)).isEqualTo("")
    }

    @Test
    fun `getCharAt returns space for out of bounds`() {
        val buf = TerminalBuffer(10, 3)
        assertThat(buf.getCharAt(100, 0)).isEqualTo(' ')
        assertThat(buf.getCharAt(0, 100)).isEqualTo(' ')
    }
}
