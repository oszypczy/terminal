# Terminal Text Buffer

A terminal text buffer implementation in Kotlin — the core data structure used by terminal emulators to store and manipulate displayed text.

## Features

- Character cells with foreground/background colors (default + 16 standard terminal colors) and style flags (bold, italic, underline)
- Cursor positioning and movement with screen bounds clamping
- Text writing (overwrite), inserting (shift right), and line filling
- Screen and scrollback buffer with configurable max scrollback size
- Wide character support (CJK ideographs, fullwidth forms)
- Screen resize with content preservation

## Requirements

- JDK 21+
- Gradle (wrapper included)

## Build

```bash
./gradlew build
```

## Run Tests

```bash
./gradlew test
```

## Lint

```bash
./gradlew ktlintCheck
```

Auto-format:

```bash
./gradlew ktlintFormat
```

## Project Structure

```
src/main/kotlin/terminal/
  TerminalBuffer.kt   - Main buffer class (screen + scrollback)
  TerminalLine.kt     - Single line of character cells
  TextStyle.kt        - Bit-packed text attributes (colors, bold, italic, underline)
  TerminalColor.kt    - Terminal color enum (default + 16 colors)
  CursorPosition.kt   - Cursor position with movement and clamping

src/test/kotlin/terminal/
  TerminalBufferTest.kt   - Core buffer operations
  CursorMovementTest.kt   - Cursor movement and bounds
  ScrollbackTest.kt       - Scrollback behavior and eviction
  ContentAccessTest.kt    - Content retrieval methods
  WideCharacterTest.kt    - Wide character handling
  ResizeTest.kt           - Screen resize scenarios
  TextStyleTest.kt        - Style and color attributes
  TerminalLineTest.kt     - Line-level operations
```