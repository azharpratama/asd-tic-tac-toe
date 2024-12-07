/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #6
 * 1 - 5026231084 - Azhar Aditya Pratama
 * 2 - 5026231109 - Abdul Ghoni
 * 3 - 5026231200 - Cristo Pison Ben Jarred
 */

package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SudokuMain extends JFrame {
    // private variables
    private static final long serialVersionUID = 1L;
    private AudioManager audioManager;
    GameBoardPanel board = new GameBoardPanel(this);
    JButton btnNewGame = new JButton("New Game");
    JTextField statusBar = new JTextField("Welcome to Sudoku!");

    public SudokuMain() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(board, BorderLayout.CENTER);
        cp.add(btnNewGame, BorderLayout.SOUTH);

        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.newGame();
            }
        });

        board.newGame();

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem resetGameItem = new JMenuItem("Reset Game");
        JMenuItem exitItem = new JMenuItem("Exit");

        newGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.newGame();
            }
        });

        resetGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.newGame();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(newGameItem);
        fileMenu.add(resetGameItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Build the Options menu
        JMenu optionsMenu = new JMenu("Options");
        // Add options menu items here

        // Build the Help menu
        JMenu helpMenu = new JMenu("Help");
        // Add help menu items here

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Add the status bar
        statusBar.setEditable(false);
        cp.add(statusBar, BorderLayout.SOUTH);

        pack();     // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
        setTitle("Sudoku");
        setVisible(true);

        audioManager = new AudioManager();
        audioManager.playBackgroundMusic();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audioManager.cleanup();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SudokuMain();
            }
        });
    }
    /** Update the status bar with the number of cells remaining */
    public void updateStatusBar() {
        int cellsRemaining = board.getCellsRemaining();
        statusBar.setText("Cells remaining: " + cellsRemaining);
    }
}