public class DefectiveChessboardTiling {
    private DefectiveChessboardTiling() {}
    private static int tileId = 1;

    private static void tile(int topRow, int leftCol, int defectRow, int defectCol, int size, int[][] board) {
        if (isBaseCase(size)) {
            fillBaseCase(topRow, leftCol, defectRow, defectCol, board);
            return;
        }

        int half = size / 2;
        int quad = getQuadrant(topRow, leftCol, defectRow, defectCol, half);
        int centerRow = topRow + half - 1;
        int centerCol = leftCol + half - 1;

        placeCentralLTile(board, quad, centerRow, centerCol);

        int[][] params = {
            {topRow, leftCol, quad == 0 ? defectRow : centerRow, quad == 0 ? defectCol : centerCol},
            {topRow, leftCol + half, quad == 1 ? defectRow : centerRow, quad == 1 ? defectCol : centerCol + 1},
            {topRow + half, leftCol, quad == 2 ? defectRow : centerRow + 1, quad == 2 ? defectCol : centerCol},
            {topRow + half, leftCol + half, quad == 3 ? defectRow : centerRow + 1, quad == 3 ? defectCol : centerCol + 1}
        };

        for (int i = 0; i < 4; i++) {
            tile(params[i][0], params[i][1], params[i][2], params[i][3], half, board);
        }
    }

    private static boolean isBaseCase(int size) {
        return size == 2;
    }

    private static void fillBaseCase(int topRow, int leftCol, int defectRow, int defectCol, int[][] board) {
        for (int r = topRow; r < topRow + 2; r++) {
            for (int c = leftCol; c < leftCol + 2; c++) {
                if (r != defectRow || c != defectCol) {
                    board[r][c] = tileId;
                }
            }
        }
        tileId++;
    }

    private static int getQuadrant(int topRow, int leftCol, int defectRow, int defectCol, int half) {
        if (defectRow < topRow + half) {
            return (defectCol < leftCol + half) ? 0 : 1;
        } else {
            return (defectCol < leftCol + half) ? 2 : 3;
        }
    }

    private static void placeCentralLTile(int[][] board, int quad, int centerRow, int centerCol) {
        if (quad != 0) board[centerRow][centerCol] = tileId;
        if (quad != 1) board[centerRow][centerCol + 1] = tileId;
        if (quad != 2) board[centerRow + 1][centerCol] = tileId;
        if (quad != 3) board[centerRow + 1][centerCol + 1] = tileId;
        tileId++;
    }

    public static int[][] tiling(int n, int[] missing) {
        tileId = 1;
        int[][] board = new int[n][n];
        board[missing[0]][missing[1]] = -1;
        tile(0, 0, missing[0], missing[1], n, board);
        return board;
    }
}
