package me.wbars.minesweeper.ui;

import me.wbars.minesweeper.core.Board;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import static javax.swing.Box.createHorizontalGlue;

public class BoardPanel extends JPanel {
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

    private JLabel timerLabel = new JLabel();
    private long start;
    private JLabel flagsCounter = new JLabel();

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

        initLabelsStyles();
    }

    private void initLabelsStyles() {
        flagsCounter.setBorder(new EmptyBorder(0, 0, 0, 12));
        timerLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
        timerLabel.setFont(new Font(timerLabel.getFont().getName(), Font.PLAIN, 16));
        flagsCounter.setFont(new Font(flagsCounter.getFont().getName(), Font.PLAIN, 16));
    }

    private JPanel initTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        initTimer();
        topPanel.add(timerLabel);
        topPanel.add(createHorizontalGlue());
        resetButton = resetButtonInit(minesCount);
        topPanel.add(resetButton);
        topPanel.add(createHorizontalGlue());
        topPanel.add(flagsCounter);
        flagsCounter.setText(String.valueOf(flagsRemain));
        return topPanel;
    }

    private void initTimer() {
        timer = new Timer(0, e -> timerLabel.setText(format(e.getWhen() - start)));
        timer.setDelay(50);
        start = System.currentTimeMillis();
        timer.start();
    }

    private static String format(long time) {
        long seconds = (time / 1000) % 60;
        long remainMillis = Math.max(time - seconds * 60, 0) / 10;
        return String.format("%d.%02d", seconds, remainMillis);
    }

    private JButton resetButtonInit(int minesCount) {
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            board = Board.create(this.cols, this.rows, minesCount);
            ((DefaultTableModel) table.getModel()).setRowCount(rows);
            ((DefaultTableModel) table.getModel()).setColumnCount(cols);
            restartTimer();
            restartFlagsCounter();
            table.repaint();
        });
        return resetButton;
    }

    private void restartFlagsCounter() {
        flagsRemain = minesCount;
        flagsCounter.setText(String.valueOf(flagsRemain));
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
        table.addMouseMotionListener(new MyMouseAdapter());
        table.addMouseListener(new MyMouseAdapter());
        table.setDefaultRenderer(Object.class, new AttributiveCellRenderer());
        return table;
    }

    public class MyMouseAdapter extends MouseMotionAdapter implements MouseListener {

        public void mouseMoved(MouseEvent e) {
            selectedRow = table.rowAtPoint(e.getPoint());
            selectedCol = table.columnAtPoint(e.getPoint());
            table.repaint();
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
                flagsCounter.setText(String.valueOf(flagsRemain));

                if (board.isWin()) {
                    timer.stop();
                    board.reveal();
                    showMessageDialog("Win");
                }
            } else {
                board.open(i, j);
                if (board.isMine(i, j)) {
                    timer.stop();
                    board.reveal();
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

        public AttributiveCellRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setDefaultStyles();
            if (board.isFlag(row, column)) this.setText("F");

            if (!board.isOpen(row, column)) {
                if (row == selectedRow && column == selectedCol) this.setBackground(Color.gray);
                else this.setBackground(Color.lightGray);
                return this;
            }

            if (board.isMine(row, column)) {
                this.setBackground(Color.red);
                return this;
            }

            this.setBackground(Color.gray);
            if (board.minesCount(row, column) > 0) {
                this.setForeground(getColor(board.minesCount(row, column)));
                this.setText(String.valueOf(board.minesCount(row, column)));
            }

            return this;
        }

        private void setDefaultStyles() {
            this.setText("");
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
