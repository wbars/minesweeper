package me.wbars.minesweeper.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Works only with positive numbers < 100
 */
public class MyCounter extends JPanel {
    private static final int LINE_LENGTH = 10;
    private static final Color COLOR = Color.red;
    private static final Color BACKGROUND_COLOR = Color.black;
    private static final int SPACE_SIZE = 4;
    private static final int DOT_SIZE = 3;
    private static final int BORDER_WIDTH = 4;
    private static final int INITIAL_OFFSET = 3;
    private static final int STROKE = 3;

    private float value;
    private int xOffset = INITIAL_OFFSET + BORDER_WIDTH + SPACE_SIZE;
    private int yOffset = INITIAL_OFFSET + BORDER_WIDTH + SPACE_SIZE;

    private static final Map<Integer, Set<Integer>> activeLines = new HashMap<>();

    static {
        activeLines.put(0, new HashSet<>(asList(1, 2, 3, 4, 5, 6)));
        activeLines.put(1, new HashSet<>(asList(2, 3)));
        activeLines.put(2, new HashSet<>(asList(1, 2, 4, 5, 7)));
        activeLines.put(3, new HashSet<>(asList(1, 2, 3, 4, 7)));
        activeLines.put(4, new HashSet<>(asList(2, 3, 6, 7)));
        activeLines.put(5, new HashSet<>(asList(1, 3, 4, 6, 7)));
        activeLines.put(6, new HashSet<>(asList(1, 3, 4, 5, 6, 7)));
        activeLines.put(7, new HashSet<>(asList(1, 2, 3)));
        activeLines.put(8, new HashSet<>(asList(1, 2, 3, 4, 5, 6, 7)));
        activeLines.put(9, new HashSet<>(asList(1, 2, 3, 4, 6, 7)));
    }

    private static int firstDigit(int x) {
        while (x > 9) x /= 10;
        return x;
    }

    public void setValue(float value) {
        if (value < 0 || value >= 100) throw new IllegalArgumentException();
        this.value = value;
        repaint();
    }

    @Override
    public Color getBackground() {
        return BACKGROUND_COLOR;
    }

    @Override
    public Border getBorder() {
        Border outer = BorderFactory.createLineBorder(Color.gray, BORDER_WIDTH);
        Border inner = BorderFactory.createLineBorder(Color.darkGray, BORDER_WIDTH);
        return BorderFactory.createCompoundBorder(outer, inner);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        xOffset = INITIAL_OFFSET + BORDER_WIDTH + SPACE_SIZE;
        int intPart = (int) this.value;
        Graphics2D g2d = (Graphics2D) g.create();
        paintDigit(intPart / 10, g2d);
        paintDigit(intPart % 10, g2d);

        if (value != Math.round(value)) {
            normalStroke(g2d);
            paintDot(g2d);
            paintDigit(firstDigit((int) (value % 1 * 10)), g2d);
        }

        g2d.dispose();
    }

    private void normalStroke(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2d.setColor(COLOR);
    }

    private void paintDot(Graphics2D g2d) {
        g2d.fillRect(xOffset, LINE_LENGTH * 2 + yOffset - DOT_SIZE, DOT_SIZE, DOT_SIZE);
        xOffset += DOT_SIZE + SPACE_SIZE;
    }

    private void paintDigit(int number, Graphics2D g2d) {
        int lineCounter = 1;
        Set<Integer> activeLines = MyCounter.activeLines.get(number);
        if (activeLines == null) throw new IllegalStateException();

        chooseStroke(g2d, lineCounter, activeLines);
        lineCounter++;
        g2d.drawLine(xOffset, yOffset, xOffset + LINE_LENGTH, yOffset);

        chooseStroke(g2d, lineCounter, activeLines);
        lineCounter++;
        g2d.drawLine(xOffset + LINE_LENGTH, yOffset, xOffset + LINE_LENGTH, LINE_LENGTH + yOffset);

        chooseStroke(g2d, lineCounter, activeLines);
        lineCounter++;
        g2d.drawLine(xOffset + LINE_LENGTH, LINE_LENGTH + yOffset, xOffset + LINE_LENGTH, LINE_LENGTH * 2 + yOffset);

        chooseStroke(g2d, lineCounter, activeLines);
        lineCounter++;
        g2d.drawLine(xOffset + LINE_LENGTH, LINE_LENGTH * 2 + yOffset, xOffset, LINE_LENGTH * 2 + yOffset);

        chooseStroke(g2d, lineCounter, activeLines);
        lineCounter++;
        g2d.drawLine(xOffset, LINE_LENGTH * 2 + yOffset, xOffset, LINE_LENGTH + yOffset);

        chooseStroke(g2d, lineCounter, activeLines);
        lineCounter++;
        g2d.drawLine(xOffset, LINE_LENGTH + yOffset, xOffset, yOffset);

        chooseStroke(g2d, lineCounter, activeLines);
        g2d.drawLine(xOffset, LINE_LENGTH + yOffset, xOffset + LINE_LENGTH, LINE_LENGTH + yOffset);

        xOffset += LINE_LENGTH + SPACE_SIZE;
    }

    private void chooseStroke(Graphics2D g2d, int lineCounter, Set<Integer> activeLines) {
        if (activeLines.contains(lineCounter)) normalStroke(g2d);
        else dashedStroke(g2d);
    }

    private void dashedStroke(Graphics2D g2d) {
        Stroke dashed = new BasicStroke(STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2, 2}, 0);
        g2d.setStroke(dashed);
        g2d.setColor(Color.decode("#492105"));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(LINE_LENGTH * 3 + DOT_SIZE + SPACE_SIZE * 4 + BORDER_WIDTH * 4, LINE_LENGTH * 2 + SPACE_SIZE * 2 + BORDER_WIDTH * 4);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
