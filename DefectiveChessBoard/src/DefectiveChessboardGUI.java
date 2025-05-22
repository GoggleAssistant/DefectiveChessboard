import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DefectiveChessboardGUI extends JFrame {
    private JTextField sizeField, rowField, colField;
    private JPanel boardPanel;

    public DefectiveChessboardGUI() {
        setTitle("Defective Chessboard Tiler");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        inputPanel.add(new JLabel("Board Size (power of 2, max 64):"));
        sizeField = new JTextField("8", 5);
        inputPanel.add(sizeField);

        inputPanel.add(new JLabel("Defective Row:"));
        rowField = new JTextField("0", 5);
        inputPanel.add(rowField);

        inputPanel.add(new JLabel("Defective Col:"));
        colField = new JTextField("1", 5);
        inputPanel.add(colField);

        JButton generateBtn = new JButton("Generate Board");
        inputPanel.add(generateBtn);

        add(inputPanel, BorderLayout.NORTH);

        // Board Panel
        boardPanel = new JPanel();
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);

        // Generate Button Action
        generateBtn.addActionListener(e -> drawBoard());

        setVisible(true);
    }

    private void drawBoard() {
        try {
            int size = Integer.parseInt(sizeField.getText());
            int row = Integer.parseInt(rowField.getText());
            int col = Integer.parseInt(colField.getText());

            if (!isPowerOfTwo(size) || size > 64 || row < 0 || col < 0 || row >= size || col >= size) {
                JOptionPane.showMessageDialog(this, "Invalid input. Check size and coordinates.");
                return;
            }

            int[][] board = DefectiveChessboardTiling.tiling(size, new int[]{row, col});
            boardPanel.removeAll();
            boardPanel.setLayout(new GridLayout(size, size));


            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    int val = board[r][c];
                    JLabel cell = new JLabel(val == -1 ? "X" : "", SwingConstants.CENTER);
                    cell.setOpaque(true);
                    cell.setPreferredSize(new Dimension(40, 40));

                    if (val == -1) {
                        cell.setBackground(Color.BLACK);
                        cell.setForeground(Color.WHITE);
                    } else {
                        cell.setBackground(Color.RED);
                    }

                    cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    boardPanel.add(cell);
                }
            }

            boardPanel.revalidate();
            boardPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers.");
        }
    }

    private boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0 && x > 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DefectiveChessboardGUI::new);
    }
}
