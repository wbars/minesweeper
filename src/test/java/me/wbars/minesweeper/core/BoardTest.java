package me.wbars.minesweeper.core;

import me.wbars.minesweeper.util.Pair;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.wbars.minesweeper.core.Cell.State.CLOSED;
import static me.wbars.minesweeper.core.Cell.State.OPEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BoardTest {
    @Test
    public void boardSize() throws Exception {
        Board board = Board.create(5, 4, 3);
        assertThat(board.cols(), is(5));
        assertThat(board.rows(), is(4));
        assertThat(board.minesCount(), is(3));
    }

    @Test
    public void boardInitWithClosedCells() throws Exception {
        Board board = Board.create(5, 4, 3);
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                assertThat(board.cellState(i, j), is(CLOSED));
            }
        }
    }

    @Test
    public void totalMinesCountShouldMatch() throws Exception {
        Board board = Board.create(5, 4, 6);
        int minesCount = 0;
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                if (board.isMine(i, j)) minesCount++;
            }
        }

        assertThat(minesCount, is(6));
    }

    @Test
    public void cellsMinesCountShouldMatch() throws Exception {
        Board board = Board.create(5, 5, 15);
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                assertThat(board.minesCount(i, j), is(minesCount(board, i, j)));
            }
        }
    }

    private int minesCount(Board board, int i, int j) {
        return (int) surroundingPositions(board, i, j).stream()
                .filter(p -> board.isMine(p.first(), p.second()))
                .count();
    }

    private List<Pair<Integer, Integer>> surroundingPositions(Board board, int i, int j) {
        return Stream.of(
                new Pair<>(i - 1, j - 1),
                new Pair<>(i - 1, j),
                new Pair<>(i - 1, j + 1),
                new Pair<>(i, j + 1),
                new Pair<>(i + 1, j + 1),
                new Pair<>(i + 1, j),
                new Pair<>(i + 1, j - 1),
                new Pair<>(i, j - 1))
                .filter(p -> board.hasIndexes(p.first(), p.second()))
                .collect(Collectors.toList());
    }

    @Test
    public void defaultStatePlaying() throws Exception {
        Board board = Board.create(5, 5, 15);
        assertThat(board.isWin(), is(false));
    }

    @Test
    public void winState() throws Exception {
        Board board = Board.create(5, 5, 15);
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                if (board.isMine(i, j)) board.flag(i, j);
            }
        }
        assertThat(board.isWin(), is(true));
    }

    @Test
    public void openCell() throws Exception {
        Board board = Board.create(5, 5, 15);
        board.open(4, 4);
        assertThat(board.cellState(4, 4), is(OPEN));
    }

    /**
     * ....
     * ....
     * .X..
     * ....
     * <p>
     * ->
     * <p>
     * ++++
     * 111+
     * .X1+
     * ..1+
     */
    @Test
    public void allNearEmptyCellsOpen() throws Exception {
        Board board = Board.create(4, 4, 0);
        board.plantMine(new Pair<>(2, 1));

        board.open(0, 0);

        assertThat(board.cellState(2, 1), is(CLOSED));
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                if (i <= 1 || i >= 2 && j >= 2) assertThat(board.cellState(i, j), is(OPEN));
                else assertThat(board.cellState(i, j), is(CLOSED));
            }
        }

    }
}
