import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class DefectiveChessboardGUI extends JFrame {
    private JSlider sizeSlider;
    private JPanel boardPanel;
    private int defectiveRow = -1, defectiveCol = -1;
    private final int[] sizes = {2, 4, 8, 16, 32, 64, 128};
    private JButton[][] buttons;
    private boolean showTileNumbers = false;
    private int[][] board;
    private Timer animationTimer;
    private int currentDigit = 0;


    public DefectiveChessboardGUI() {
        setTitle("Defective Chessboard Tiler");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Board Size:"));

        sizeSlider = new JSlider(0, sizes.length - 1, 2);
        sizeSlider.setMajorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setLabelTable(createSizeLabels());
        topPanel.add(sizeSlider);

        JButton clearDefectBtn = new JButton("Clear Defect");
        clearDefectBtn.setEnabled(false);
        clearDefectBtn.setFocusable(false);
        clearDefectBtn.setBackground(Color.LIGHT_GRAY);
        topPanel.add(clearDefectBtn);

        JToggleButton toggleShowNumbers = new JToggleButton("Show Tile Numbers");
        toggleShowNumbers.setFocusable(false);
        toggleShowNumbers.setBackground(Color.LIGHT_GRAY);
        topPanel.add(toggleShowNumbers);

        add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);

        // Update slider listener: reset board then update toggle
        sizeSlider.addChangeListener(e -> {
            resetBoard();
            updateShowNumbersToggle(toggleShowNumbers);
        });

        toggleShowNumbers.addItemListener(e -> {
            showTileNumbers = toggleShowNumbers.isSelected();
            drawTiling();
        });
        clearDefectBtn.addActionListener(e -> clearDefect());

        resetBoard();
        updateShowNumbersToggle(toggleShowNumbers);

        setVisible(true);
    }

    private void updateShowNumbersToggle(JToggleButton toggleShowNumbers) {
        int size = sizes[sizeSlider.getValue()];
        if (size >= 64) {
            toggleShowNumbers.setSelected(false);
            toggleShowNumbers.setEnabled(false);
            showTileNumbers = false;
            drawTiling();  // refresh so numbers disappear
        } else {
            toggleShowNumbers.setEnabled(true);
        }
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
                final int rr = r, cc = c;

                btn.addActionListener(ev -> {
                    defectiveRow = rr;
                    defectiveCol = cc;
                    disableAllButtons();
                    highlightDefectCell();
                    drawTiling();
                    getClearDefectButton().setEnabled(true);
                });

                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        getClearDefectButton().setEnabled(false);
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
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons.length; c++) {
                buttons[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
        if (defectiveRow >= 0 && defectiveCol >= 0) {
            buttons[defectiveRow][defectiveCol].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }

    private void clearDefect() {
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
        if (defectiveRow == -1 || defectiveCol == -1) return;

        board = DefectiveChessboardTiling.tiling(size, new int[]{defectiveRow, defectiveCol});
        Color[] palette = generateColorPalette(10);

        // Reset buttons to gray and clear text initially
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                buttons[r][c].setBackground(Color.LIGHT_GRAY);
                buttons[r][c].setText("");
                buttons[r][c].setForeground(Color.BLACK);
            }
        }
        highlightDefectCell();

        currentDigit = 0;
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(40, e -> {

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    int val = board[r][c];
                    JButton btn = buttons[r][c];

                    // Only update tiles that match the currentDigit and haven't been colored yet
                    if (val != -1 && val % 10 == currentDigit) {
                        btn.setBackground(palette[val % 10]);
                        btn.setText(showTileNumbers ? String.valueOf(val) : "");
                        btn.setForeground(Color.BLACK);
                    } else if (val == -1) {
                        // defective cell remains black with X
                        btn.setBackground(Color.BLACK);
                        btn.setText("X");
                        btn.setForeground(Color.WHITE);
                    }
                }
            }

            highlightDefectCell();

            currentDigit++;
            if (currentDigit > 9) {
                animationTimer.stop(); // stop animation after all digits are processed
            }
        });

        animationTimer.start();
    }

    private JButton getClearDefectButton() {
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
        for (Component comp : topPanel.getComponents()) {
            if (comp instanceof JButton btn && "Clear Defect".equals(btn.getText())) {
                return btn;
            }
        }
        return null;
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
