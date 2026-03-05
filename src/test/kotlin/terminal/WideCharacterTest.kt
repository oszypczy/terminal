package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WideCharacterTest {
    @Test
    fun `write wide character occupies two cells`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("你")
        assertThat(buf.getCharAt(0, 0)).isEqualTo('你')
        assertThat(buf.getCharAt(1, 0)).isEqualTo('\u0000')
        assertThat(buf.cursorCol).isEqualTo(2)
    }

    @Test
    fun `write multiple wide characters`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("你好")
        assertThat(buf.getLine(0)).isEqualTo("你好")
        assertThat(buf.cursorCol).isEqualTo(4)
    }

    @Test
    fun `wide character at last column wraps to next line`() {
        val buf = TerminalBuffer(5, 3)
        buf.setCursorPosition(4, 0)
        buf.write("你")
        assertThat(buf.getCharAt(4, 0)).isEqualTo(' ')
        assertThat(buf.getCharAt(0, 1)).isEqualTo('你')
        assertThat(buf.cursorCol).isEqualTo(2)
        assertThat(buf.cursorRow).isEqualTo(1)
    }

    @Test
    fun `overwrite continuation cell clears wide char`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("你好")
        buf.setCursorPosition(1, 0)
        buf.write("X")
        assertThat(buf.getCharAt(0, 0)).isEqualTo(' ')
        assertThat(buf.getCharAt(1, 0)).isEqualTo('X')
    }

    @Test
    fun `overwrite primary cell of wide char clears continuation`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("你好")
        buf.setCursorPosition(0, 0)
        buf.write("A")
        assertThat(buf.getCharAt(0, 0)).isEqualTo('A')
        assertThat(buf.getCharAt(1, 0)).isEqualTo(' ')
    }

    @Test
    fun `getLine skips continuation cells`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("A你B")
        assertThat(buf.getLine(0)).isEqualTo("A你B")
    }

    @Test
    fun `mixed narrow and wide characters`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("Hi你好AB")
        assertThat(buf.getLine(0)).isEqualTo("Hi你好AB")
        assertThat(buf.cursorCol).isEqualTo(8)
    }
}
