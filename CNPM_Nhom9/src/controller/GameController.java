package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.BoardView;
import view.GameView;

public class GameController {
	private final Board board;
    private final AIService ai;
    private BoardView view;

    public BoardController(String difficulty) {
        this.board = new Board();
    }

    public void setView(BoardView view) {
        this.view = view;
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

	
}
