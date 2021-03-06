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
    private boolean init;

    public boolean isWin() {
        return allIndexes(null).stream()
                .map(this::cell)
                .filter(Cell::isMine)
                .allMatch(p -> p.state() == Cell.State.FLAG);
    }

    public void open(int i, int j) {
        Cell cell = cells[i][j];
        if (cell.state() == Cell.State.OPEN) throw new IllegalStateException();
        if (!init) init(new Pair<>(i, j));
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
            if (cell.state() == Cell.State.OPEN) continue;
            cell.state(Cell.State.OPEN);

            if (cell.minesCount() > 0) continue;
            surroundingCells(cell.position()).stream()
                    .filter(c -> c.state() == Cell.State.CLOSED)
                    .forEach(bfs::addLast);
        }
    }

    private Board(int cols, int rows, int minesCount) {
        if (minesCount > cols * rows) throw new IllegalArgumentException("Too many mines: " + minesCount);
        this.cols = cols;
        this.rows = rows;
        this.minesCount = minesCount;
        cells = new Cell[rows][cols];
        initCells(cols, rows);
    }

    private void initRandomMines(Pair<Integer, Integer> exceptPos) {
        getRandomIndexes(minesCount, exceptPos).forEach(this::plantMine);
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

    private List<Pair<Integer, Integer>> getRandomIndexes(int size, Pair<Integer, Integer> exceptPos) {
        List<Pair<Integer, Integer>> allIndexes = allIndexes(exceptPos);
        if (size > allIndexes.size())
            throw new IllegalStateException("Not enough free cells for plant: " + allIndexes.size());
        shuffle(allIndexes);
        return allIndexes.subList(0, size);
    }

    private List<Pair<Integer, Integer>> allIndexes(Pair<Integer, Integer> exceptPos) {
        return range(0, rows).boxed()
                .flatMap(i -> range(0, cols).boxed().map(j -> new Pair<>(i, j)))
                .filter(p -> !p.equals(exceptPos))
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
        return new Board(cols, rows, minesCount);
    }

    private void init(Pair<Integer, Integer> exceptPos) {
        if (init) throw new IllegalStateException();
        initRandomMines(exceptPos);
        init = true;
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

    public void reveal(boolean showMines) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (showMines || !isMine(i, j)) cells[i][j].state(Cell.State.OPEN);
            }
        }
    }

    public Cell.State cellState(int i, int j) {
        return cells[i][j].state();
    }

    public int minesCount(int i, int j) {
        return cells[i][j].minesCount();
    }

    public boolean toggleFlag(int i, int j) {
        if (isFlag(i, j)) cells[i][j].state(Cell.State.CLOSED);
        else if (cells[i][j].state() == Cell.State.CLOSED) cells[i][j].state(Cell.State.FLAG);
        else throw new IllegalArgumentException();

        return isFlag(i, j);
    }

    public boolean isFlag(int i, int j) {
        return cells[i][j].state() == Cell.State.FLAG;
    }
}
