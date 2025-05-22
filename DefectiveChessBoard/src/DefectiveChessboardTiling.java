public class DefectiveChessboardTiling {
    private static int tileId = 1;  // global tile counter

    /**
     * Recursive tiling function:
     * topRow, leftCol: top-left corner of current sub-board
     * defectRow, defectCol: coordinates of defective cell inside current sub-board
     * size: length of sub-board (power of 2)
     * board: the board array to fill
     */
    private static void tile(int topRow, int leftCol, int defectRow, int defectCol, int size, int[][] board) {
        if (size == 2) {
            // Base case 2x2 board: fill 3 squares with current tileId, skip defective
            for (int r = topRow; r < topRow + size; r++) {
                for (int c = leftCol; c < leftCol + size; c++) {
                    if (r != defectRow || c != defectCol) {
                        board[r][c] = tileId;
                    }
                }
            }
            tileId++;
            return;
        }

        int half = size / 2;

        // Identify which quadrant the defect is in:
        // 0: top-left, 1: top-right, 2: bottom-left, 3: bottom-right
        int quad = 0;
        if (defectRow < topRow + half) {
            if (defectCol < leftCol + half) quad = 0;
            else quad = 1;
        } else {
            if (defectCol < leftCol + half) quad = 2;
            else quad = 3;
        }

        // Place center DefectiveChessboard covering the center cells of the 3 quadrants WITHOUT the defect
        // The positions of the center cells depend on quadrant:
        // center cells coordinates:
        // top-left quadrant center: (topRow+half-1, leftCol+half-1)
        // top-right quadrant center: (topRow+half-1, leftCol+half)
        // bottom-left quadrant center: (topRow+half, leftCol+half-1)
        // bottom-right quadrant center: (topRow+half, leftCol+half)

        // Put a tile in these 3 center squares (excluding the quadrant with defect)
        int centerRow = topRow + half - 1;
        int centerCol = leftCol + half - 1;

        // Place the tileId on three center squares
        if (quad != 0) board[centerRow][centerCol] = tileId;
        if (quad != 1) board[centerRow][centerCol + 1] = tileId;
        if (quad != 2) board[centerRow + 1][centerCol] = tileId;
        if (quad != 3) board[centerRow + 1][centerCol + 1] = tileId;

        int currentTileId = tileId;
        tileId++;

        // Recurse on 4 quadrants with correct defect positions:
        // For the quadrant with the actual defect, keep defectRow, defectCol as is
        // For others, defect is at center cell where we placed tileId

        // top-left quadrant
        tile(topRow, leftCol,
            (quad == 0) ? defectRow : centerRow,
            (quad == 0) ? defectCol : centerCol,
            half, board);

        // top-right quadrant
        tile(topRow, leftCol + half,
            (quad == 1) ? defectRow : centerRow,
            (quad == 1) ? defectCol : centerCol + 1,
            half, board);

        // bottom-left quadrant
        tile(topRow + half, leftCol,
            (quad == 2) ? defectRow : centerRow + 1,
            (quad == 2) ? defectCol : centerCol,
            half, board);

        // bottom-right quadrant
        tile(topRow + half, leftCol + half,
            (quad == 3) ? defectRow : centerRow + 1,
            (quad == 3) ? defectCol : centerCol + 1,
            half, board);
    }

    /**
     * Main method called by GUI or console:
     * n = board size (power of 2)
     * missing = defective cell coordinate [row, col]
     */
    public static int[][] tiling(int n, int[] missing) {
        tileId = 1;
        int[][] board = new int[n][n];

        // Mark defective cell as -1
        board[missing[0]][missing[1]] = -1;

        // Start recursion
        tile(0, 0, missing[0], missing[1], n, board);

        return board;
    }
}
