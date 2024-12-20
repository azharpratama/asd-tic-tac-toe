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
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMain extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Connect Four";
    public static final Color COLOR_BG = new Color(0, 0, 0,0 );
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80); // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private Board board; // the game board
    private State currentState; // the current state of the game
    private Seed currentPlayer; // the current player
    private final JLabel statusBar; // for displaying status message
    private AIPlayer aiPlayer;

    /** Constructor to set up the UI and game components */
    public GameMain() {

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // mouse-clicked handler
                int mouseX = e.getX();
                // Get the row and column clicked
                // int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (col >= 0 && col < Board.COLS) {
                        for (int row = Board.ROWS - 1; row >= 0; row--) {
                            if (board.cells[row][col].content == Seed.NO_SEED) {
                                // Update cells[][] and return the new game state after the move
                                currentState = board.stepGame(currentPlayer, row, col);
                                // Switch player
                                // Play appropriate sound clip
                                if (currentState == State.PLAYING) {
                                    SoundEffect.EAT_FOOD.play();
                                } else {
                                    SoundEffect.DIE.play();
                                }
                                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                                break;
                            }
                        }
                        // Let the AI make a move if it's the AI's turn
                        if (currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
                            int[] move = aiPlayer.move();
                            currentState = board.stepGame(currentPlayer, move[0], move[1]);
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        }
                    }
                } else { // game over
                    newGame(); // restart the game
                }
                // Refresh the drawing canvas
                repaint(); // Callback paintComponent().

            }
        });


        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        this.setOpaque(false);

        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        // account for statusBar in height
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board(); // allocate the game-board
        aiPlayer = new AIPlayerTableLookup(board);
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS; // cross plays first
        currentState = State.PLAYING; // ready to play
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) { // Callback via repaint()
        super.paintComponent(g);

        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    /** The entry "main" method */
    public static void main(String[] args) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        SoundEffect.initGame();
        SoundEffect.BACKGORUND.playLoop();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);

                GameMain gamePanel = new GameMain();

                // Create the Restart button
                JButton restartButton = new JButton("Restart Game");
                restartButton.setFont(new Font("Arial", Font.BOLD, 16));
                restartButton.setBackground(Color.LIGHT_GRAY);

                // Add action listener to restart the game
                restartButton.addActionListener(e -> {
                    gamePanel.initGame();  // Reinitialize the game
                    gamePanel.newGame();  // Reset the board
                    gamePanel.repaint();  // Redraw the panel
                });

                // Load the background image
                BufferedImage backgroundImage = null;
                try {
                    backgroundImage = ImageIO.read(GameMain.class.getResource("/images/background.jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Define final variable for inner class
                final BufferedImage finalBackgroundImage = backgroundImage;

                // Create a wrapper panel with a background
                JPanel wrapperPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (finalBackgroundImage != null) {
                            g.drawImage(finalBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };

                // Add the game panel and restart button to the wrapper panel
                wrapperPanel.add(restartButton, BorderLayout.NORTH); // Add the button at the top
                wrapperPanel.add(gamePanel, BorderLayout.CENTER);    // Add the game panel in the center

                // Set the wrapper panel as the content pane of the frame
                frame.setContentPane(wrapperPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true);            // Show the frame
            }
        });
    }
}