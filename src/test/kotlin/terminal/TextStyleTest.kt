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
}
