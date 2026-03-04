package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CursorMovementTest {

    @Test
    fun `move cursor right`() {
        val buf = TerminalBuffer(10, 5)
        buf.moveCursorRight(3)
        assertThat(buf.cursorCol).isEqualTo(3)
        assertThat(buf.cursorRow).isEqualTo(0)
    }

    @Test
    fun `move cursor right clamps at right edge`() {
        val buf = TerminalBuffer(10, 5)
        buf.moveCursorRight(100)
        assertThat(buf.cursorCol).isEqualTo(9)
    }

    @Test
    fun `move cursor left`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(5, 0)
        buf.moveCursorLeft(3)
        assertThat(buf.cursorCol).isEqualTo(2)
    }

    @Test
    fun `move cursor left clamps at left edge`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(2, 0)
        buf.moveCursorLeft(10)
        assertThat(buf.cursorCol).isEqualTo(0)
    }

    @Test
    fun `move cursor down`() {
        val buf = TerminalBuffer(10, 5)
        buf.moveCursorDown(3)
        assertThat(buf.cursorRow).isEqualTo(3)
    }

    @Test
    fun `move cursor down clamps at bottom edge`() {
        val buf = TerminalBuffer(10, 5)
        buf.moveCursorDown(100)
        assertThat(buf.cursorRow).isEqualTo(4)
    }

    @Test
    fun `move cursor up`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(0, 3)
        buf.moveCursorUp(2)
        assertThat(buf.cursorRow).isEqualTo(1)
    }

    @Test
    fun `move cursor up clamps at top edge`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(0, 2)
        buf.moveCursorUp(10)
        assertThat(buf.cursorRow).isEqualTo(0)
    }

    @Test
    fun `move cursor by zero does nothing`() {
        val buf = TerminalBuffer(10, 5)
        buf.setCursorPosition(3, 2)
        buf.moveCursorRight(0)
        buf.moveCursorLeft(0)
        buf.moveCursorUp(0)
        buf.moveCursorDown(0)
        assertThat(buf.cursorCol).isEqualTo(3)
        assertThat(buf.cursorRow).isEqualTo(2)
    }
}
