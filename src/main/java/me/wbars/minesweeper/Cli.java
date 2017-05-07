package me.wbars.minesweeper;

import me.wbars.minesweeper.core.Board;

import java.io.PrintStream;
import java.util.Scanner;

public class Cli {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PrintStream out = System.out;

        out.println("Rows: ");
        int rows = in.nextInt();

        out.println("Cols: ");
        int cols = in.nextInt();

        out.println("Mines count: ");
        int minesCount = in.nextInt();

        Board board = Board.create(cols, rows, minesCount);
        while (true) {
            out.println(board.toString());
            if (board.isWin()) {
                out.print("WIN");
                return;
            }
            out.println("Row: ");
            int row = in.nextInt();
            out.println("Col: ");
            int col = in.nextInt();

            if (board.isOpen(row, col)) {
                out.println("Already opened");
                continue;
            }

            if (!board.hasIndexes(row, col)) {
                out.println("AOOBE");
                continue;
            }

            out.println("Open(1) or Flag(2)?");
            int action = in.nextInt();

            if (action == 1) board.open(row, col);
            else board.flag(row, col);

            if (action == 1 && board.isMine(row, col)) {
                out.println("LOOSE");
                board.reveal(false);
                out.print(board.toString());
                return;
            }
        }
    }
}
