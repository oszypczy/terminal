package terminal

data class CursorPosition(var col: Int, var row: Int) {
    fun clamp(maxCol: Int, maxRow: Int) {
        col = col.coerceIn(0, maxCol)
        row = row.coerceIn(0, maxRow)
    }

    fun moveRight(n: Int, maxCol: Int) { col = (col + n).coerceAtMost(maxCol) }
    fun moveLeft(n: Int) { col = (col - n).coerceAtLeast(0) }
    fun moveDown(n: Int, maxRow: Int) { row = (row + n).coerceAtMost(maxRow) }
    fun moveUp(n: Int) { row = (row - n).coerceAtLeast(0) }
}
