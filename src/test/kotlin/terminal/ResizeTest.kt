package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ResizeTest {
    @Test
    fun `grow width pads lines`() {
        val buf = TerminalBuffer(5, 3)
        buf.write("Hello")
        buf.resize(10, 3)
        assertThat(buf.width).isEqualTo(10)
        assertThat(buf.getLine(0)).isEqualTo("Hello")
    }

    @Test
    fun `shrink width truncates lines`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("HelloWorld")
        buf.resize(5, 3)
        assertThat(buf.width).isEqualTo(5)
        assertThat(buf.getLine(0)).isEqualTo("Hello")
    }

    @Test
    fun `grow height adds empty rows at bottom`() {
        val buf = TerminalBuffer(10, 2)
        buf.write("A")
        buf.resize(10, 4)
        assertThat(buf.height).isEqualTo(4)
        assertThat(buf.getLine(0)).isEqualTo("A")
        assertThat(buf.getLine(3)).isEqualTo("")
    }

    @Test
    fun `shrink height drops bottom rows`() {
        val buf = TerminalBuffer(10, 4)
        buf.write("Row0")
        buf.setCursorPosition(0, 1)
        buf.write("Row1")
        buf.resize(10, 2)
        assertThat(buf.height).isEqualTo(2)
        assertThat(buf.getLine(0)).isEqualTo("Row0")
        assertThat(buf.getLine(1)).isEqualTo("Row1")
    }

    @Test
    fun `shrink height with cursor below moves top rows to scrollback`() {
        val buf = TerminalBuffer(10, 4, maxScrollbackSize = 100)
        buf.setCursorPosition(0, 3)
        buf.write("CursorHere")
        buf.resize(10, 2)
        assertThat(buf.height).isEqualTo(2)
        assertThat(buf.cursorRow).isEqualTo(1)
        assertThat(buf.scrollbackSize).isEqualTo(2)
    }

    @Test
    fun `cursor clamped after resize`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(8, 4)
        buf.resize(5, 3)
        assertThat(buf.cursorCol).isEqualTo(4)
        assertThat(buf.cursorRow).isEqualTo(2)
    }

    @Test
    fun `wide char at new width boundary is replaced with space`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("AB你DE")
        buf.resize(4, 3)
        // "你" starts at col 2, continuation at col 3 — col 3 is last col in width 4
        // The wide char fits: cols 2 and 3 are both within width 4
        assertThat(buf.getCharAt(2, 0)).isEqualTo('你')
    }

    @Test
    fun `wide char split by resize is cleared`() {
        val buf = TerminalBuffer(10, 3)
        buf.write("AB你DE")
        buf.resize(3, 3)
        // "你" at cols 2-3 — only col 2 fits in width 3, continuation at 3 is cut
        assertThat(buf.getCharAt(2, 0)).isEqualTo(' ')
    }
}
