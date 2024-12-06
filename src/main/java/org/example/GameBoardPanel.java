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
import java.io.Serial;
import javax.swing.*;

public class GameBoardPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;  // to prevent serial warning

    // Define named constants for UI sizes
    public static final int CELL_SIZE = 60;   // Cell width/height in pixels
    public static final int BOARD_WIDTH  = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;
    // Board width/height in pixels

    // Define properties
    /** The game board composes of 9x9 Cells (customized JTextFields) */
    private final Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    /** It also contains a Puzzle with array numbers and isGiven */
    private final Puzzle puzzle = new Puzzle();
    private SudokuMain mainFrame;
    private AudioManager audioManager;

    /** Constructor */
    public GameBoardPanel(SudokuMain mainFrame) {
        this.mainFrame = mainFrame;
        super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));  // JPanel
        audioManager = new AudioManager();

        // Allocate the 2D array of Cell, and added into JPanel.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col);
                super.add(cells[row][col]);   // JPanel
            }
        }

        // [TODO 3] Allocate a common listener as the ActionEvent listener for all the
        //  Cells (JTextFields)
        // .........
        CellInputListener listener = new CellInputListener();

        // [TODO 4] Adds this common listener to all editable cells
        // .........
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].addKeyListener(listener);
                }
            }
        }

        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }

    /**
     * Generate a new puzzle; and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame() {
        // Generate a new puzzle
        puzzle.newPuzzle(2);

        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
        mainFrame.updateStatusBar();
    }

    /**
     * Return true if the puzzle is solved
     * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return the number of cells remaining to be filled
     */
    public int getCellsRemaining() {
        int count = 0;
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    count++;
                }
            }
        }
        return count;
    }

    // [TODO 2] Define a Listener Inner Class for all the editable Cells
    // .........
    private class CellInputListener implements KeyListener {
        @Override

        public void keyTyped(KeyEvent e) {
            // Get a reference of the JTextField that triggers this action event
            Cell sourceCell = (Cell)e.getSource();

            char keyChar = e.getKeyChar();
            if (Character.isDigit(keyChar)) {
                // Retrieve the int entered
                int numberIn = Character.getNumericValue(keyChar);
                // For debugging
                System.out.println("You entered " + numberIn);

                /*
                * [TODO 5] (later - after TODO 3 and 4)
                * Check the numberIn against sourceCell.number.
                * Update the cell status sourceCell.status,
                * and re-paint the cell via sourceCell.paint().
                */
                if (numberIn == sourceCell.number) {
                    sourceCell.status = CellStatus.CORRECT_GUESS;
                    audioManager.playCorrectSound();
                } else {
                    sourceCell.status = CellStatus.WRONG_GUESS;
                    audioManager.playWrongSound();
                }
                sourceCell.paint();   // re-paint this cell based on its status

                // Update the status bar
                mainFrame.updateStatusBar();

                /*
                * [TODO 6] (later)
                * Check if the player has solved the puzzle after this move,
                *   by calling isSolved(). Put up a congratulation JOptionPane, if so.
                */
                if (isSolved()) {
                    audioManager.playWinSound();
                    JOptionPane.showMessageDialog(null, "Congratulations! You have solved the puzzle!");
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Not used
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Not used
        }
    }
}