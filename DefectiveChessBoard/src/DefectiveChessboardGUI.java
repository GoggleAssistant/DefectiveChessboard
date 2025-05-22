import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

public class DefectiveChessboardGUI extends JFrame {
    private JSlider sizeSlider;
    private JPanel boardPanel;
    private int defectiveRow = -1, defectiveCol = -1;
    private final int[] sizes = {2, 4, 8, 16, 32, 64};
    private JButton[][] buttons;

    public DefectiveChessboardGUI() {
        setTitle("Defective Chessboard Tiler");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel for size slider and buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Board Size:"));

        sizeSlider = new JSlider(0, sizes.length - 1, 2); // Default 8
        sizeSlider.setMajorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setLabelTable(createSizeLabels());
        topPanel.add(sizeSlider);

        JButton resetBtn = new JButton("Reset Board");
        JButton clearDefectBtn = new JButton("Clear Defect");
        clearDefectBtn.setEnabled(false);  // disabled initially

        topPanel.add(clearDefectBtn);
        topPanel.add(resetBtn);

        add(topPanel, BorderLayout.NORTH);

        // Board Panel (scrollable)
        boardPanel = new JPanel();
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);

        sizeSlider.addChangeListener(e -> resetBoard());
        resetBtn.addActionListener(e -> resetBoard());

        clearDefectBtn.addActionListener(e -> clearDefect());

        resetBoard(); // initialize the board

        setVisible(true);
    }

    private void resetBoard() {
        int size = sizes[sizeSlider.getValue()];
        defectiveRow = -1;
        defectiveCol = -1;

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(size, size));
        buttons = new JButton[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton btn = new JButton();
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                btn.setPreferredSize(new Dimension(40, 40));
                final int rr = r, cc = c;

                btn.setEnabled(true);
                btn.setText("");
                btn.setForeground(Color.BLACK);

                btn.addActionListener(ev -> {
                    defectiveRow = rr;
                    defectiveCol = cc;
                    disableAllButtons();
                    highlightDefectCell();
                    drawTiling();
                    // Enable Clear Defect button
                    getClearDefectButton().setEnabled(true);
                });

                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        getClearDefectButton().setEnabled(false); // disable clear defect on reset
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void disableAllButtons() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
    }

    private void highlightDefectCell() {
        // Reset all borders first
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons.length; c++) {
                buttons[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
        // Highlight defective cell with red border
        if (defectiveRow >= 0 && defectiveCol >= 0) {
            buttons[defectiveRow][defectiveCol].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }

    private void clearDefect() {
        // Re-enable all buttons and reset their appearance
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons.length; c++) {
                JButton btn = buttons[r][c];
                btn.setEnabled(true);
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setText("");
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
        defectiveRow = -1;
        defectiveCol = -1;
        getClearDefectButton().setEnabled(false);
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void drawTiling() {
        int size = sizes[sizeSlider.getValue()];
        if (defectiveRow == -1 || defectiveCol == -1) return; // no defect yet

        int[][] board = DefectiveChessboardTiling.tiling(size, new int[]{defectiveRow, defectiveCol});

        // Update buttons colors based on tile numbers
        Color[] palette = generateColorPalette(10);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int val = board[r][c];
                JButton btn = buttons[r][c];

                if (val == -1) {
                    btn.setBackground(Color.BLACK);
                    btn.setText("X");
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(palette[val % 10]);
                    btn.setText("");
                    btn.setForeground(Color.BLACK);
                }
            }
        }

        // Highlight defective cell border after tiling
        highlightDefectCell();
    }

    private JButton getClearDefectButton() {
        // Find the "Clear Defect" button from the top panel (assumes added order)
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
        for (Component comp : topPanel.getComponents()) {
            if (comp instanceof JButton btn && "Clear Defect".equals(btn.getText())) {
                return btn;
            }
        }
        return null; // should never happen
    }

    private Hashtable<Integer, JLabel> createSizeLabels() {
        Hashtable<Integer, JLabel> table = new Hashtable<>();
        for (int i = 0; i < sizes.length; i++) {
            table.put(i, new JLabel(String.valueOf(sizes[i])));
        }
        return table;
    }

    private Color[] generateColorPalette(int count) {
        Color[] colors = new Color[count];
        for (int i = 0; i < count; i++) {
            float hue = (i * 1.0f / count);
            colors[i] = Color.getHSBColor(hue, 0.6f, 1.0f);
        }
        return colors;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DefectiveChessboardGUI::new);
    }
}
