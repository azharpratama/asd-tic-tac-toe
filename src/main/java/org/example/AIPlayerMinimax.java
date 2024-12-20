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

import java.util.*;

/** AIPlayer using Minimax algorithm with Alpha-Beta Pruning */
public class AIPlayerMinimax extends AIPlayer {

    private final Map<String, int[]> transpositionTable = new HashMap<>(); // Caching board states

    /** Constructor with the given game board */
    public AIPlayerMinimax(Board board) {
        super(board);
    }

    /** Get next best move for computer. Return int[2] of {row, col} */
    @Override
    int[] move() {
        int depth = 6;
        int[] result = minimax(depth, mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return new int[] { result[1], result[2] }; // row, col
    }

    /** Minimax with alpha-beta pruning and transposition table */
    private int[] minimax(int depth, Seed player, int alpha, int beta) {
        String boardHash = hashBoard(); // Hash current board state
        if (transpositionTable.containsKey(boardHash)) {
            return transpositionTable.get(boardHash);
        }

        List<int[]> nextMoves = generateMoves();
        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            score = evaluate();
            return new int[] { score, bestRow, bestCol };
        }

        for (int[] move : nextMoves) {
            makeMove(move[0], move[1], player);
            if (player == mySeed) {
                score = minimax(depth - 1, oppSeed, alpha, beta)[0];
                if (score > alpha) {
                    alpha = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            } else {
                score = minimax(depth - 1, mySeed, alpha, beta)[0];
                if (score < beta) {
                    beta = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            }
            undoMove(move[0], move[1]);

            if (alpha >= beta)
                break; // Prune
        }
        int[] result = new int[] { (player == mySeed) ? alpha : beta, bestRow, bestCol };
        transpositionTable.put(boardHash, result);
        return result;
    }

    /** Generate moves prioritizing center positions */
    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<>();
        if (hasWon(mySeed) || hasWon(oppSeed))
            return nextMoves;

        int[] colOrder = getPrioritizedColumns();
        for (int col : colOrder) {
            for (int row = Board.ROWS - 1; row >= 0; row--) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    nextMoves.add(new int[] { row, col });
                    break;
                }
            }
        }
        return nextMoves;
    }

    private int[] getPrioritizedColumns() {
        int[] colOrder = new int[Board.COLS];
        colOrder[0] = Board.COLS / 2;
        int left = Board.COLS / 2 - 1;
        int right = Board.COLS / 2 + 1;
        for (int i = 1; i < Board.COLS; i++) {
            if (i % 2 == 1 && left >= 0)
                colOrder[i] = left--;
            else if (right < Board.COLS)
                colOrder[i] = right++;
        }
        return colOrder;
    }

    /** Improved evaluation function */
    private int evaluate() {
        int score = 0;
        score += evaluateLine(1, 0);
        score += evaluateLine(0, 1);
        score += evaluateLine(1, 1);
        score += evaluateLine(1, -1);
        return score;
    }

    private int evaluateLine(int deltaRow, int deltaCol) {
        int score = 0;
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                int myCount = 0, oppCount = 0, empty = 0;
                for (int i = 0; i < 4; i++) {
                    int r = row + i * deltaRow, c = col + i * deltaCol;
                    if (r < 0 || r >= Board.ROWS || c < 0 || c >= Board.COLS)
                        break;
                    Seed content = cells[r][c].content;
                    if (content == mySeed)
                        myCount++;
                    else if (content == oppSeed)
                        oppCount++;
                    else
                        empty++;
                }
                score += evaluateLine(myCount, oppCount, empty);
            }
        }
        return score;
    }

    private int evaluateLine(int myPieces, int oppPieces, int empty) {
        if (myPieces == 4)
            return 100000;
        if (oppPieces == 4)
            return -100000;
        if (myPieces == 3 && empty == 1)
            return 5000;
        if (oppPieces == 3 && empty == 1)
            return -5000;
        if (myPieces == 2 && empty == 2)
            return 500;
        if (oppPieces == 2 && empty == 2)
            return -500;
        return 0;
    }

    private void makeMove(int row, int col, Seed seed) {
        cells[row][col].content = seed;
    }

    private void undoMove(int row, int col) {
        cells[row][col].content = Seed.NO_SEED;
    }

    private boolean hasWon(Seed player) {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                if (checkLine(player, row, col, 1, 0) ||
                        checkLine(player, row, col, 0, 1) ||
                        checkLine(player, row, col, 1, 1) ||
                        checkLine(player, row, col, 1, -1))
                    return true;
            }
        }
        return false;
    }

    private String hashBoard() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                sb.append(cells[row][col].content);
            }
        }
        return sb.toString();
    }

    /** Check if there is a line of 4 of the same seed */
    private boolean checkLine(Seed player, int row, int col, int deltaRow, int deltaCol) {
        for (int i = 0; i < 4; ++i) {
            int r = row + i * deltaRow;
            int c = col + i * deltaCol;

            if (r < 0 || r >= Board.ROWS || c < 0 || c >= Board.COLS || cells[r][c].content != player) {
                return false;
            }
        }
        return true;
    }
}