package me.wbars.minesweeper.ui;

import javax.swing.*;

public class Block extends JButton {
    private final int row;
    private final int col;

    public Block(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }
}
