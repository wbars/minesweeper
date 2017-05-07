package me.wbars.minesweeper.core;

import me.wbars.minesweeper.util.Pair;

public class Cell {
    private State state = State.CLOSED;
    private boolean isMine = false;
    private int minesCount = 0;
    private final Pair<Integer, Integer> position;

    public Cell(int i, int j) {
        this.position = new Pair<>(i, j);
    }

    public void state(State state) {
        this.state = state;
    }

    public State state() {
        return state;
    }

    public boolean isMine() {
        return isMine;
    }

    public void plantMine() {
        isMine = true;
    }

    public void incrementMinesCount() {
        minesCount++;
    }

    public int minesCount() {
        return minesCount;
    }

    public Pair<Integer, Integer> position() {
        return position;
    }

    public enum State {
        OPEN, CLOSED, FLAG;
    }

    @Override
    public String toString() {
        if (state == State.CLOSED) return ".";
        if (state == State.OPEN) return isMine() ? "X" : String.valueOf(minesCount);
        if (state == State.FLAG) return "F";
        return "?";
    }
}
