package me.wbars.minesweeper.core;

import me.wbars.minesweeper.util.Pair;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class Board {
    private final int cols;
    private final int rows;
    private final int minesCount;
    private final Cell[][] cells;

    public boolean isWin() {
        return allIndexes().stream()
                .map(this::cell)
                .filter(Cell::isMine)
                .allMatch(p -> p.state() == Cell.State.FLAG);
    }

    public void open(int i, int j) {
        Cell cell = cells[i][j];
        if (cell.state() == Cell.State.OPEN) throw new IllegalStateException();
        if (cell.isMine()) {
            cell.state(Cell.State.OPEN);
            return;
        }
        fillNearClosedCells(cell);
    }

    private void fillNearClosedCells(Cell e) {
        ArrayDeque<Cell> bfs = new ArrayDeque<>();
        bfs.addLast(e);
        while (!bfs.isEmpty()) {
            Cell cell = bfs.pollFirst();
            cell.state(Cell.State.OPEN);

            if (cell.minesCount() > 0) continue;
            surroundingCells(cell.position()).stream()
                    .filter(c -> c.state() == Cell.State.CLOSED)
                    .forEach(bfs::addLast);
        }
    }

    private Board(int cols, int rows, int minesCount) {
        this.cols = cols;
        this.rows = rows;
        this.minesCount = minesCount;
        cells = new Cell[rows][cols];
        initCells(cols, rows);
        initRandomMines();
    }

    private void initRandomMines() {
        getRandomIndexes(minesCount).forEach(this::plantMine);
    }

    private Cell cell(Pair<Integer, Integer> position) {
        return cell(position.first(), position.second());
    }

    private List<Cell> surroundingCells(Pair<Integer, Integer> pos) {
        return Stream.of(
                new Pair<>(pos.first() - 1, pos.second() - 1),
                new Pair<>(pos.first() - 1, pos.second()),
                new Pair<>(pos.first() - 1, pos.second() + 1),
                new Pair<>(pos.first(), pos.second() + 1),
                new Pair<>(pos.first() + 1, pos.second() + 1),
                new Pair<>(pos.first() + 1, pos.second()),
                new Pair<>(pos.first() + 1, pos.second() - 1),
                new Pair<>(pos.first(), pos.second() - 1)
        )
                .filter(p -> hasIndexes(p.first(), p.second()))
                .map(this::cell)
                .collect(toList());
    }

    private List<Pair<Integer, Integer>> getRandomIndexes(int size) {
        List<Pair<Integer, Integer>> allIndexes = allIndexes();
        shuffle(allIndexes);
        return allIndexes.subList(0, size);
    }

    private List<Pair<Integer, Integer>> allIndexes() {
        return range(0, rows).boxed()
                .flatMap(i -> range(0, cols).boxed().map(j -> new Pair<>(i, j)))
                .collect(toList());
    }

    private void initCells(int cols, int rows) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public static Board create(int cols, int rows, int minesCount) {
        if (minesCount > cols * rows) throw new IllegalArgumentException("Too many mines: " + minesCount);
        return new Board(cols, rows, minesCount);
    }

    public int cols() {
        return cols;
    }

    public int rows() {
        return rows;
    }

    public int minesCount() {
        return minesCount;
    }

    private Cell cell(int i, int j) {
        return cells[i][j];
    }

    public boolean hasIndexes(int i, int j) {
        return i >= 0 && i < rows && j >= 0 && j < cols;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(cells[i][j].toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    void plantMine(Pair<Integer, Integer> pos) {
        cell(pos.first(), pos.second()).plantMine();
        surroundingCells(pos).forEach(Cell::incrementMinesCount);
    }

    public void flag(int i, int j) {
        cells[i][j].state(Cell.State.FLAG);
    }

    public boolean isOpen(int i, int j) {
        return cells[i][j].state() == Cell.State.OPEN;
    }

    public boolean isMine(int i, int j) {
        return cells[i][j].isMine();
    }

    public void reveal() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].state(Cell.State.OPEN);
            }
        }
    }

    public Cell.State cellState(int i, int j) {
        return cells[i][j].state();
    }

    public int minesCount(int i, int j) {
        return cells[i][j].minesCount();
    }
}
