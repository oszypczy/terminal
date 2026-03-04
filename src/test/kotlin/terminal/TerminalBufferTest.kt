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
}
