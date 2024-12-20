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
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.URL;


/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMain extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Connect Four";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80); // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private Board board; // the game board
    private State currentState; // the current state of the game
    private Seed currentPlayer; // the current player
    private static JButton newGameHuman;
    private static JButton newGameAI;
    private final JPanel buttonPanel; // for the New Game buttons
    private final JPanel statusBar; // for displaying status message
    private final JLabel statusLabel; // JLabel inside the status bar
    private final JLabel difficultyLabel; // JLabel inside the status bar
    private AIPlayer aiPlayer; // AI player
    private String gameMode = "AI"; // Default to vs AI mode
    private String difficulty = "Medium"; // Default difficulty
    private BufferedImage backgroundImage; // Background image
    private BufferedImage dialogBackgroundImage; // Dialog background image

    /** Constructor to set up the UI and game components */
    public GameMain() {
        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // mouse-clicked handler
                int mouseX = e.getX();
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
                        if (gameMode == "AI" && currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
                            int[] move = aiPlayer.move(difficulty);
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

        this.setOpaque(false); // Set the panel to be transparent

        // Setup the status bar (JPanel) to display status message
        statusBar = new JPanel(new BorderLayout());

        statusLabel = new JLabel();
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setBackground(COLOR_BG_STATUS);
        statusLabel.setOpaque(true);
        statusLabel.setPreferredSize(new Dimension(300, 30));
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        difficultyLabel = new JLabel();
        difficultyLabel.setFont(FONT_STATUS);
        difficultyLabel.setBackground(COLOR_BG_STATUS);
        difficultyLabel.setOpaque(true);
        difficultyLabel.setPreferredSize(new Dimension(300, 30));
        difficultyLabel.setHorizontalAlignment(JLabel.RIGHT);
        difficultyLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(difficultyLabel, BorderLayout.EAST);

        // Create the New Game buttons
        newGameHuman = new JButton("New Game Vs Human");
        newGameHuman.setFont(FONT_STATUS);
        newGameHuman.setBackground(Color.LIGHT_GRAY);

        newGameAI = new JButton("New Game Vs AI");
        newGameAI.setFont(FONT_STATUS);
        newGameAI.setBackground(Color.LIGHT_GRAY);

        // Add action listeners to the buttons
        newGameHuman.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = "Human";
                initGame(); // Reinitialize the game
                newGame(); // Reset the board
            }
        });

        newGameAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = "AI";
                showDifficultySelectionDialog();
                initGame(); // Reinitialize the game
                newGame(); // Reset the board
            }
        });

        // Create a panel for the buttons
        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(newGameHuman);
        buttonPanel.add(newGameAI);

        // Load the background image
        try {
            backgroundImage = ImageIO.read(GameMain.class.getResource("/images/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the status bar and button panel to the main panel
        super.setLayout(new BorderLayout());
        super.add(buttonPanel, BorderLayout.PAGE_START); // same as NORTH
        super.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 60)); // account for statusBar
                                                                                             // and buttons in height
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    private void showDifficultySelectionDialog() {
        JDialog dialog = new JDialog((Frame) null, "Select Difficulty", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
    
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");
    
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = "Easy";
                dialog.dispose();
            }
        });
    
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = "Medium";
                dialog.dispose();
            }
        });
    
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = "Hard";
                dialog.dispose();
            }
        });
    
        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);
    
        dialog.add(new JLabel("Choose Difficulty Level:", SwingConstants.CENTER), BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.CENTER);
    
        dialog.setVisible(true);
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board(); // allocate the game-board
        aiPlayer = new AIPlayerMinimax(board);
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
        repaint();
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        board.paint(g);

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusLabel.setForeground(Color.BLACK);
            statusLabel.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("It's a Draw! Click to play again.");
            showCustomNotification("It's a Draw!", "Game Over", "/images/win.png");
        } else if (currentState == State.CROSS_WON) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("'X' Won! Click to play again.");
            showCustomNotification("'X' Won the Game!", "Game Over", "/images/win.png");
        } else if (currentState == State.NOUGHT_WON) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("'O' Won! Click to play again.");
            // Check if it's AI mode and AI (O) won
            if (gameMode.equals("AI")) {
                showLoseNotification();  // Show lose notification instead
            } else {
                showCustomNotification("'O' Won the Game!", "Game Over", "/images/win.png");
            }
        }

        if (gameMode.equals("AI")) {
            difficultyLabel.setForeground(Color.BLACK);
            difficultyLabel.setText("Difficulty: " + difficulty);
        } else {
            difficultyLabel.setText("2 Players");
        }
    }


    private void showCustomNotification(String message, String title, String imagePath) {
        JDialog dialog = new JDialog((Frame) null, title, true); // Create modal dialog
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true); // Remove default title bar

        // Load background image into instance variable
        try {
            dialogBackgroundImage = ImageIO.read(GameMain.class.getResource(imagePath));
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
        }

        // Create panel with background
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (dialogBackgroundImage != null) {
                    g.drawImage(dialogBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setPreferredSize(new Dimension(400, 250)); // Set panel size

        // Add message text
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Adjusted font size for better visibility
        messageLabel.setForeground(Color.WHITE); // Text color
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Add message panel to dialog
        dialog.add(messagePanel, BorderLayout.CENTER);

        // Create button panel to ensure proper rendering of the New Game button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(true); // Ensure visibility of the button panel
        buttonPanel.setBackground(Color.DARK_GRAY); // Set a more neutral background color

        // Add New Game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16)); // Increase font size for better visibility
        newGameButton.setBackground(Color.LIGHT_GRAY); // Set button background to light gray for visibility
        newGameButton.setForeground(Color.BLACK); // Set button text color to black
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close dialog on button click
                newGame(); // Start a new game
            }
        });

        buttonPanel.add(newGameButton); // Add button to button panel
        dialog.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to dialog

        dialog.pack(); // Adjust dialog size
        dialog.setLocationRelativeTo(this); // Center the dialog on screen
        dialog.setVisible(true); // Show dialog
    }

    /** Show notification when player loses against AI */
    private void showLoseNotification() {
        String loseMessage = "You Lost? LMAO Yowaimo!";
        String loseTitle = "Defeat";
        String loseImagePath = "/images/lose.jpg";

        // Create and show dialog with title
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), loseTitle, true);
        dialog.setLayout(new BorderLayout());

        // Load lose image with explicit error handling
        try {
            URL imageUrl = getClass().getResource(loseImagePath);
            if (imageUrl == null) {
                throw new IOException("Cannot find resource: " + loseImagePath);
            }
            dialogBackgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Error loading lose image: " + e.getMessage());
            e.printStackTrace();
            // Set a default color if image fails to load
            dialogBackgroundImage = new BufferedImage(360, 240, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = dialogBackgroundImage.createGraphics();
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, 360, 240);
            g2d.dispose();
        }

        // Create panel with background - match exact image dimensions
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (dialogBackgroundImage != null) {
                    // Draw image at exact size without scaling
                    g.drawImage(dialogBackgroundImage, 0, 0, 360, 240, this);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(360, 240);
            }
        };
        messagePanel.setLayout(new BorderLayout());

        // Create semi-transparent overlay panel
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 90, 10, 120)); // Semi-transparent black
                g2d.fillRect(360, 360, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setLayout(new BorderLayout());

        // Add message with improved visibility
        JLabel messageLabel = new JLabel(loseMessage, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 25));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        overlayPanel.add(messageLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(true);


        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.setBackground(new Color(255, 255, 255)); // Warna biru terang
        newGameButton.setOpaque(true); // Memastikan background terlihat
        newGameButton.setForeground(Color.BLACK); // Warna teks putih agar kontras
        newGameButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Border hitam dengan ketebalan 2px
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(e -> {
            dialog.dispose();
            newGame();
        });


        // Play lose sound
        try {
            SoundEffect.LOSE_AI.play();
        } catch (Exception e) {
            System.err.println("Error playing lose sound: " + e.getMessage());
            e.printStackTrace();
        }

        // Assemble the dialog
        buttonPanel.add(newGameButton);
        overlayPanel.add(buttonPanel, BorderLayout.SOUTH);
        messagePanel.add(overlayPanel, BorderLayout.CENTER);
        dialog.add(messagePanel);

        // Final dialog setup
        dialog.setUndecorated(true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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

                // Create a wrapper panel to combine the new game buttons and game panel
                JPanel wrapperPanel = new JPanel(new BorderLayout());
                wrapperPanel.add(gamePanel, BorderLayout.CENTER);

                // Set the wrapper panel as the content pane of the frame
                frame.setContentPane(wrapperPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true); // Show the frame
            }
        });
    }
}