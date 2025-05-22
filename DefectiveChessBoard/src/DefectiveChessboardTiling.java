import java.util.Scanner;

public class DefectiveChessboardTiling {

    private static final int[][][] TROMINO_SHAPES = {
        {{0, 0}, {0, 1}, {1, 0}}, // ┌
        {{0, 0}, {0, 1}, {1, 1}}, // ┐
        {{0, 0}, {1, 0}, {1, 1}}, // └
        {{0, 0}, {1, 0}, {1, -1}} // ┘
    };

    private static boolean isValid(int[][] board, int r, int c, int[][] shape, int size) {
        for (int[] offset : shape) {
            int nr = r + offset[0];
            int nc = c + offset[1];
            if (nr < 0 || nr >= size || nc < 0 || nc >= size || board[nr][nc] != 0)
                return false;
        }
        return true;
    }

    private static void place(int[][] board, int r, int c, int[][] shape, int tileId) {
        for (int[] offset : shape) {
            board[r + offset[0]][c + offset[1]] = tileId;
        }
    }

    private static void removeL(int[][] board, int r, int c, int[][] shape) {
        for (int[] offset : shape) {
            board[r + offset[0]][c + offset[1]] = 0;
        }
    }

    private static boolean findNext0(int[][] board, int size, int[] out) {
        for (int r = 0; r < size; ++r) {
            for (int c = 0; c < size; ++c) {
                if (board[r][c] == 0) {
                    out[0] = r;
                    out[1] = c;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean tileBoard(int[][] board, int size, int tileId) {
        int[] pos = new int[2];
        if (!findNext0(board, size, pos)) {
            return true;
        }

        int r = pos[0];
        int c = pos[1];

        for (int[][] shape : TROMINO_SHAPES) {
            if (isValid(board, r, c, shape, size)) {
                place(board, r, c, shape, tileId);
                if (tileBoard(board, size, tileId + 1)) {
                    return true;
                }
                removeL(board, r, c, shape);
            }
        }

        return false;
    }

    public static int[][] tiling(int n, int[] missing) {
        int[][] board = new int[n][n];
        board[missing[0]][missing[1]] = -1;

        if (tileBoard(board, n, 1))
            return board;

        return new int[][]{{-1}};
    }

    public static boolean isPowerOfTwo(int x) {
        return x > 0 && (x & (x - 1)) == 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get size of board from user
        System.out.print("Enter board size (must be power of 2, e.g. 2, 4, 8...): ");
        int n = scanner.nextInt();

        while (!isPowerOfTwo(n)) {
            System.out.print("Invalid size. Enter a power of 2: ");
            n = scanner.nextInt();
        }

        // Get defective square from user
        System.out.print("Enter row of defective square (0 to " + (n - 1) + "): ");
        int row = scanner.nextInt();

        System.out.print("Enter column of defective square (0 to " + (n - 1) + "): ");
        int col = scanner.nextInt();

        while (row < 0 || row >= n || col < 0 || col >= n) {
            System.out.println("Invalid index. Try again.");
            System.out.print("Row: ");
            row = scanner.nextInt();
            System.out.print("Col: ");
            col = scanner.nextInt();
        }

        int[] missing = {row, col};

        int[][] grid = tiling(n, missing);

        System.out.println("\nDefective Chessboard Solution:");
        for (int[] line : grid) {
            for (int val : line) {
                System.out.printf("%3d", val);
            }
            System.out.println();
        }

        scanner.close();
    }
}
