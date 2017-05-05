package me.wbars.minesweeper.ui;

import javax.swing.*;

public class CustomGamePanel extends JPanel {
    private final JTextField rowsField = new JTextField();
    private final JTextField colsField = new JTextField();
    private final JTextField minesField = new JTextField();

    public CustomGamePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Rows: "));
        add(rowsField);
        add(Box.createHorizontalStrut(15));

        add(new JLabel("Cols: "));
        add(colsField);
        add(Box.createHorizontalStrut(15));

        add(new JLabel("Mines count: "));
        add(minesField);
        add(Box.createHorizontalStrut(15));
    }

    public int rows() {
        return Integer.parseInt(rowsField.getText());
    }

    public int cols() {
        return Integer.parseInt(colsField.getText());
    }

    public int mines() {
        return Integer.parseInt(minesField.getText());
    }
}
