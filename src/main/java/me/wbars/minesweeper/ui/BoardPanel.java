package me.wbars.minesweeper.ui;

import me.wbars.minesweeper.core.Board;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;

import static javax.swing.Box.createHorizontalGlue;

public class BoardPanel extends JPanel {
    private final MyCounter timerPanel = new MyCounter();
    private final MyCounter flagsPanel = new MyCounter();
    private final ImageIcon upsetIcon = getImageIcon("images/upset.png");
    private final ImageIcon sadIcon = getImageIcon("images/sad.png");
    private final ImageIcon smileIcon = getImageIcon("images/smile.png");
    private final ImageIcon surpriseIcon = getImageIcon("images/surprised.png");
    private final ImageIcon flagIcon = getImageIcon("images/flag.png");
    private final ImageIcon mineIcon = getImageIcon("images/mine.png");
    private int rows;
    private int cols;
    private final int minesCount;
    private JTable table;
    private Board board;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private JButton resetButton;
    private int cellSize = 30;
    private JPanel topPanel;
    private int flagsRemain;

    private Timer timer;
    private long start;

    public BoardPanel(int rows, int cols, int minesCount) {
        this.rows = rows;
        this.cols = cols;
        this.minesCount = minesCount;
        restartFlagsCounter();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        topPanel = initTopPanel();
        add(topPanel);

        board = Board.create(cols, rows, minesCount);
        table = initTable(rows, cols);
        add(table);
    }

    private JPanel initTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        initTimer();
        topPanel.add(timerPanel);
        topPanel.add(createHorizontalGlue());
        resetButton = resetButtonInit(minesCount);
        topPanel.add(resetButton);
        topPanel.add(createHorizontalGlue());

        restartFlagsCounter();
        topPanel.add(flagsPanel);
        return topPanel;
    }

    private void initTimer() {
        timer = new Timer(0, e -> timerPanel.setValue((float) ((e.getWhen() - start) * 1.0 / 1000)));
        timer.setDelay(50);
        start = System.currentTimeMillis();
        timer.start();
    }

    private URL getImage(String path) {
        return getClass().getClassLoader().getResource(path);
    }

    private JButton resetButtonInit(int minesCount) {
        JButton button = new JButton(smileIcon);

        button.setMaximumSize(new Dimension(30, 30));
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setIcon(surpriseIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setIcon(smileIcon);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        button.addActionListener(e -> {
            board = Board.create(this.cols, this.rows, minesCount);
            ((DefaultTableModel) table.getModel()).setRowCount(rows);
            ((DefaultTableModel) table.getModel()).setColumnCount(cols);
            restartTimer();
            restartFlagsCounter();
            table.repaint();
        });
        return button;
    }

    private ImageIcon getImageIcon(String path) {
        ImageIcon icon = new ImageIcon(getImage(path));
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        return icon;
    }

    private void restartFlagsCounter() {
        flagsRemain = minesCount;
        flagsPanel.setValue(flagsRemain);
    }

    private void restartTimer() {
        timer.restart();
        start = System.currentTimeMillis();
    }

    private JTable initTable(int rows, int cols) {
        JTable table = new JTable(rows, cols);
        table.setRowHeight(cellSize);
        for (int i = 0; i < cols; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        }
        table.setShowGrid(true);
        table.setGridColor(Color.black);
        for (MouseListener mouseListener : table.getMouseListeners()) {
            table.removeMouseListener(mouseListener);
        }
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setDefaultRenderer(Object.class, new AttributiveCellRenderer());

        table.addMouseMotionListener(new MyMouseAdapter((DefaultTableModel) table.getModel()));
        table.addMouseListener(new MyMouseAdapter((DefaultTableModel) table.getModel()));
        return table;
    }

    public class MyMouseAdapter extends MouseMotionAdapter implements MouseListener {
        private final DefaultTableModel model;

        MyMouseAdapter(DefaultTableModel model) {
            this.model = model;
        }

        public void mouseMoved(MouseEvent e) {
            if (selectedRow >= 0 && selectedCol >= 0) model.fireTableCellUpdated(selectedRow, selectedCol);

            selectedRow = table.rowAtPoint(e.getPoint());
            selectedCol = table.columnAtPoint(e.getPoint());

            model.fireTableCellUpdated(selectedRow, selectedCol);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int i = table.rowAtPoint(e.getPoint());
            int j = table.columnAtPoint(e.getPoint());
            if (board.isOpen(i, j)) return;

            if (SwingUtilities.isRightMouseButton(e) || e.isControlDown()) {
                if (!board.isFlag(i, j) && flagsRemain == 0) return;
                boolean newValue = board.toggleFlag(i, j);
                if (newValue) flagsRemain--;
                else flagsRemain++;
                flagsPanel.setValue(flagsRemain);

                if (board.isWin()) {
                    timer.stop();
                    board.reveal(false);
                    resetButton.setIcon(upsetIcon);
                    showMessageDialog("Win");
                }
            } else {
                board.open(i, j);
                if (board.isMine(i, j)) {
                    timer.stop();
                    board.reveal(true);
                    resetButton.setIcon(sadIcon);
                    showMessageDialog("Loose");
                }
            }

            table.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private void showMessageDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public class AttributiveCellRenderer extends JLabel implements TableCellRenderer {
        private Color background = Color.gray;

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, background.brighter(), 0, getHeight(), background);

            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            super.paintComponent(g);
        }

        public AttributiveCellRenderer() {
            setOpaque(true);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setDefaultStyles();
            if (board.isFlag(row, column)) this.setIcon(flagIcon);

            if (!board.isOpen(row, column)) {
                if (row == selectedRow && column == selectedCol) background = Color.gray;
                else background = Color.lightGray;
                return this;
            }

            if (board.isMine(row, column)) {
                background = Color.red;
                this.setIcon(mineIcon);
                return this;
            }

            background = Color.gray;
            if (board.minesCount(row, column) > 0) {
                this.setForeground(getColor(board.minesCount(row, column)));
                this.setText(String.valueOf(board.minesCount(row, column)));
            }

            return this;
        }

        private void setDefaultStyles() {
            this.setText("");
            this.setIcon(null);
            this.setForeground(Color.MAGENTA);
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setVerticalAlignment(JLabel.CENTER);
        }
    }

    private Color getColor(int minesCount) {
        if (minesCount == 1) return Color.blue;
        if (minesCount == 2) return Color.green;
        if (minesCount == 3) return Color.red;
        if (minesCount == 4) return Color.getColor("06004E");
        if (minesCount == 5) return Color.getColor("7D0E0E");
        return Color.getColor("5D5708");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(cols * cellSize, (int) (rows * cellSize + topPanel.getPreferredSize().getHeight()));
    }
}
