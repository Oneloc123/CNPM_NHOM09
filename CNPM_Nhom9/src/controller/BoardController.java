package controller;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import aiService.AIService;
import model.Board;
import view.BoardView;

public class BoardController {
    private final Board board;
    private final AIService ai;
    private BoardView view;

    public BoardController(String difficulty) {
        this.board = new Board();
        this.ai = new AIService(difficulty);
    }

    public void handlePlayerMove(int row, int col) {
        int player = board.makeMove(row, col);
        if (player == 0)
            return;

        view.updateBoard(board);

        int[][] winLine = board.getWinLine(row, col, player);
        if (winLine != null) {
            view.highlightWin(winLine, true);
            SwingUtilities.invokeLater(() -> view.showEndGame("X thắng!"));
            return;
        }

        if (board.isBoardFull()) {
            SwingUtilities.invokeLater(() -> view.showEndGame("Hòa!"));
            return;
        }

        view.setInputEnabled(false);
        view.setStatus("Máy đang suy nghĩ...");
        triggerAIMove();
    }

    private void triggerAIMove() {
        new SwingWorker<int[], Void>() {

            @Override
            protected int[] doInBackground() {
                return ai.getNextMove(board);
            }

            @Override
            protected void done() {
                try {
                    int[] move = get();
                    if (move == null) return;

                    int player = board.makeMove(move[0], move[1]);
                    view.updateBoard(board);
                    view.setInputEnabled(true);

                    int[][] winLine = board.getWinLine(move[0], move[1], player);
                    if (winLine != null) {
                        view.highlightWin(winLine, false);
                        SwingUtilities.invokeLater(() -> view.showEndGame("Máy (O) thắng!"));
                        return;
                    }

                    if (board.isBoardFull()) {
                        SwingUtilities.invokeLater(() -> view.showEndGame("Hòa!"));
                        return;
                    }

                    view.setStatus("Lượt của: X");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    view.setInputEnabled(true);
                }
            }
        }.execute();
    }
}
