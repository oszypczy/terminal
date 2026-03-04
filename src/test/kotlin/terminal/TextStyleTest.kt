package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TextStyleTest {

    @Test
    fun `terminal colors include default and 16 standard colors`() {
        assertThat(TerminalColor.entries).hasSize(17)
        assertThat(TerminalColor.DEFAULT.ordinal).isEqualTo(0)
        assertThat(TerminalColor.BLACK.ordinal).isEqualTo(1)
        assertThat(TerminalColor.BRIGHT_WHITE.ordinal).isEqualTo(16)
    }

    @Test
    fun `default style has no flags and default colors`() {
        val style = TextStyle.DEFAULT
        assertThat(style.bold).isFalse()
        assertThat(style.italic).isFalse()
        assertThat(style.underline).isFalse()
        assertThat(style.foreground).isEqualTo(TerminalColor.DEFAULT)
        assertThat(style.background).isEqualTo(TerminalColor.DEFAULT)
    }

    @Test
    fun `style with bold flag`() {
        val style = TextStyle(bold = true)
        assertThat(style.bold).isTrue()
        assertThat(style.italic).isFalse()
        assertThat(style.foreground).isEqualTo(TerminalColor.DEFAULT)
    }

    @Test
    fun `style with foreground color`() {
        val style = TextStyle(foreground = TerminalColor.RED)
        assertThat(style.foreground).isEqualTo(TerminalColor.RED)
        assertThat(style.background).isEqualTo(TerminalColor.DEFAULT)
        assertThat(style.bold).isFalse()
    }

    @Test
    fun `style with all attributes set`() {
        val style = TextStyle(
            bold = true,
            italic = true,
            underline = true,
            foreground = TerminalColor.BRIGHT_CYAN,
            background = TerminalColor.BLUE
        )
        assertThat(style.bold).isTrue()
        assertThat(style.italic).isTrue()
        assertThat(style.underline).isTrue()
        assertThat(style.foreground).isEqualTo(TerminalColor.BRIGHT_CYAN)
        assertThat(style.background).isEqualTo(TerminalColor.BLUE)
    }

    @Test
    fun `styles with same attributes are equal`() {
        val a = TextStyle(bold = true, foreground = TerminalColor.RED)
        val b = TextStyle(bold = true, foreground = TerminalColor.RED)
        assertThat(a).isEqualTo(b)
        assertThat(a.packed).isEqualTo(b.packed)
    }

    @Test
    fun `styles with different attributes are not equal`() {
        val a = TextStyle(bold = true)
        val b = TextStyle(italic = true)
        assertThat(a).isNotEqualTo(b)
    }
}
