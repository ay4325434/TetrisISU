 --- TETRIS ISU ---

Instructions:
Pieces, consisting of four blocks, will drop down into a board.
You can speed it up by holding the down key or pressing space.

When a row is completely filled with blocks, the row will be cleared and the blocks
above will be shifted down according to the number of rows cleared. If you clear multiple
rows at once, you will receive a higher score. Combos (clearing lines on consecutive turns)
will also provide a score bonus.

When your pieces reach the top, the game will end of a block interferes with the next
piece's starting position (top middle of the board).

The more lines you clear, the faster the pieces will drop!

The hold slot is a place where you can temporarily store a piece for better use. If a piece is already
in the hold slot, the piece in the hold slot and your current block will be switched. The hold function
can only be used once per piece. It resets once your current piece is dropped on the board.

The next five pieces will be shown to you in queue. They are determined randomly after the starting bag.

A back-to-back chain is obtained by clearing lines using spins or quads. It breaks when lines are cleared
without those methods. A back-to-back chain also increases your score.

Keybinds:
Left arrow - Move piece left
Right arrow - Move piece right
Up arrow/X - Rotate piece clockwise
Down arrow: Move piece down (soft drop)
Space: Hard drop (instantly drops piece to the bottom or on another piece)
Shift: Hold
Z: Rotate counterclockwise
A: Rotate 180 degrees

Tips and tricks:
- Clean stacking: Try to make your stack flat. It opens up more potential to clear lines.
- T-spins: They give a score bonus - A T-spin double gives you the same score as a Quad.

Bugs:
- Rotation: Piece may rotate in the wrong direction or 180 degrees for no reason.
- Blocks may phase into other blocks.
- Line clearing: Especially with the I piece, some lines may be cleared by accident
  (the row hasn't been filled).

Features being implemented:
- Wall kicks: This feature looks for alternative spaces for a piece to fit in if the original
  rotation will result in a collision. This makes T-spin triples possible. (Very buggy right now)
- All-spin score bonus: This feature will recognize spins with all pieces (not just the T-piece).
  However, an error-free wall kick system must be implemented before this.

FAQs:

What is a T-spin?
- A T-spin is a feature in Tetris where the T piece is rotated into a slot that can't be reached
  otherwise.
  Example:
  o o o o 1 2 o o o o
  o o o 3 4 5 o o o o
  o o o o 6 o o o o o
  In the setup above, the T piece cannot be placed upside down on its own to clear the bottom two lines.
  Instead, the piece must be placed pointing to its right. This covers slots 1, 4, 5, and 6.
  Next, rotate it clockwise. Slots 3, 4, 5, and 6 will then be covered.
  This process is known as a T-spin. It gives a score bonus.

How does the game know when to increase the speed?
- For every 10 lines cleared, the drop interval decreases.

Is a T-spin triple possible?
- Yes, but you need wall kicks.

  o o 1 - o o o o o o
  o 2 3 4 o o o o o o
  o 5 o o o o o o o o
  o 6 7 o o o o o o o
  o 8 o o o o o o o o
  o o o o o o o o o o

  If you place the T-piece flat and insert to cover slots 1, 2, 3, and 4, you can rotate it left
  to cover slots 5, 6, 7, and 8. This is only possible because any other case in the wall kick table
  will result in a collision.


Credits

Music Library:
Music Collection 1
-
Music Collection 2
Music Collection 3
Music Collection 4
Music Collection 5
Tetris Songs




