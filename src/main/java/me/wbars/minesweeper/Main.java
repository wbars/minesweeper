package me.wbars.minesweeper;

import me.wbars.minesweeper.ui.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        initSystemProperties();
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void initSystemProperties() {
        try {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Minesweeper");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true"); // for older versions of Java
        } catch (SecurityException unused) {
            /* probably running via webstart, do nothing */
        }
    }

    private static void createAndShowGUI() {
        JFrame mainFrame = new JFrame();
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setJMenuBar(createMenu(mainFrame));
        mainFrame.setContentPane(new BoardPanel(9, 9, 10));
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static JMenuBar createMenu(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu game = new JMenu("Game");
        game.add(addDifficultyItem(frame, 9, 9, 10, "Beginner"));
        game.add(addDifficultyItem(frame, 16, 16, 40, "Medium"));
        game.add(addDifficultyItem(frame, 16, 30, 99, "Expert"));
        game.addSeparator();
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
        game.add(exit);
        menuBar.add(game);
        return menuBar;
    }

    private static JMenuItem addDifficultyItem(JFrame frame, int rows, int cols, int mines, String name) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> {
            frame.setContentPane(new BoardPanel(rows, cols, mines));
            frame.pack();
            frame.repaint();
        });
        return item;
    }


}
