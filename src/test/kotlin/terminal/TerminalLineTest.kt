package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TerminalLineTest {

    @Test
    fun `new line is filled with spaces and default style`() {
        val line = TerminalLine(10)
        assertThat(line.charAt(0)).isEqualTo(' ')
        assertThat(line.charAt(9)).isEqualTo(' ')
        assertThat(line.styleAt(0)).isEqualTo(TextStyle.DEFAULT)
        assertThat(line.widthAt(0)).isEqualTo(1)
    }

    @Test
    fun `toString returns trimmed content`() {
        val line = TerminalLine(10)
        line.setChar(0, 'H', TextStyle.DEFAULT)
        line.setChar(1, 'i', TextStyle.DEFAULT)
        assertThat(line.toString()).isEqualTo("Hi")
    }

    @Test
    fun `toFullString returns full line with trailing spaces`() {
        val line = TerminalLine(5)
        line.setChar(0, 'A', TextStyle.DEFAULT)
        assertThat(line.toFullString()).isEqualTo("A    ")
    }

    @Test
    fun `setChar stores character and style`() {
        val line = TerminalLine(10)
        val style = TextStyle(bold = true, foreground = TerminalColor.RED)
        line.setChar(3, 'X', style)
        assertThat(line.charAt(3)).isEqualTo('X')
        assertThat(line.styleAt(3)).isEqualTo(style)
    }

    @Test
    fun `clear resets line to spaces with default style`() {
        val line = TerminalLine(5)
        line.setChar(0, 'X', TextStyle(bold = true))
        line.clear()
        assertThat(line.charAt(0)).isEqualTo(' ')
        assertThat(line.styleAt(0)).isEqualTo(TextStyle.DEFAULT)
    }

    @Test
    fun `fill sets entire line to given char and style`() {
        val line = TerminalLine(5)
        val style = TextStyle(foreground = TerminalColor.GREEN)
        line.fill('=', style)
        for (i in 0 until 5) {
            assertThat(line.charAt(i)).isEqualTo('=')
            assertThat(line.styleAt(i)).isEqualTo(style)
        }
    }
}
