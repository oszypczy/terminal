package terminal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScrollbackTest {
    @Test
    fun `insertLineAtBottom moves top line to scrollback`() {
        val buf = TerminalBuffer(10, 3, maxScrollbackSize = 100)
        buf.write("Line0")
        buf.setCursorPosition(0, 1)
        buf.write("Line1")
        buf.setCursorPosition(0, 2)
        buf.write("Line2")

        buf.insertLineAtBottom()

        assertThat(buf.getLine(-1)).isEqualTo("Line0")
        assertThat(buf.getLine(0)).isEqualTo("Line1")
        assertThat(buf.getLine(1)).isEqualTo("Line2")
        assertThat(buf.getLine(2)).isEqualTo("")
    }

    @Test
    fun `scrollback evicts oldest when full`() {
        val buf = TerminalBuffer(10, 2, maxScrollbackSize = 2)
        // Fill and scroll 3 times
        buf.write("A")
        buf.insertLineAtBottom()
        buf.write("B")
        buf.insertLineAtBottom()
        buf.write("C")
        buf.insertLineAtBottom()

        assertThat(buf.scrollbackSize).isEqualTo(2)
        assertThat(buf.getLine(-2)).isEqualTo("B")
        assertThat(buf.getLine(-1)).isEqualTo("C")
    }

    @Test
    fun `negative row access into scrollback`() {
        val buf = TerminalBuffer(10, 2, maxScrollbackSize = 100)
        buf.write("First")
        buf.insertLineAtBottom()
        buf.write("Second")
        buf.insertLineAtBottom()

        assertThat(buf.getLine(-1)).isEqualTo("Second")
        assertThat(buf.getLine(-2)).isEqualTo("First")
    }

    @Test
    fun `getChar and getStyle work for scrollback`() {
        val buf = TerminalBuffer(10, 2, maxScrollbackSize = 100)
        buf.setAttributes(bold = true)
        buf.write("AB")
        buf.insertLineAtBottom()

        assertThat(buf.getCharAt(0, -1)).isEqualTo('A')
        assertThat(buf.getStyleAt(0, -1).bold).isTrue()
    }

    @Test
    fun `scrollbackSize tracks number of scrollback lines`() {
        val buf = TerminalBuffer(10, 3, maxScrollbackSize = 100)
        assertThat(buf.scrollbackSize).isEqualTo(0)
        buf.insertLineAtBottom()
        assertThat(buf.scrollbackSize).isEqualTo(1)
        buf.insertLineAtBottom()
        assertThat(buf.scrollbackSize).isEqualTo(2)
    }
}
