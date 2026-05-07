package aiService;

import java.util.Random;
import model.Board;

public class AIService {
	private String difficulty;
	
    private static final int WIN_SCORE =  1000;
    private static final int LOSE_SCORE = -1000;
    private static final int MAX_DEPTH = 9;

    public AIService(String difficulty) {
        this.difficulty = difficulty;
    }

	public int[] getNextMove(Board board) {
		if (difficulty.equals("Dễ")) {
			return getRandomMove(board);
		} else {
			return getBestMove(board);
		}
	}

	private int[] getRandomMove(Board board) {
		if (board.isBoardFull())
			return null;

		Random rand = new Random();
		int r, c;
		do {
			r = rand.nextInt(Board.SIZE);
			c = rand.nextInt(Board.SIZE);
		} while (board.getCell(r, c) != 0);
		return new int[] { r, c };
	}

	private int[] getBestMove(Board board) {
		if (board.isBoardFull())
			return null;

		int bestScore = Integer.MIN_VALUE;
		int[] bestMove = null;

		for (int i = 0; i < Board.SIZE; i++) {
			for (int j = 0; j < Board.SIZE; j++) {
				if (board.getCell(i, j) == 0) {
					int player = board.makeMove(i, j);
					int score = minimax(board, false, i, j, player, MAX_DEPTH - 1);
					board.undoMove(i, j);

					if (score > bestScore) {
						bestScore = score;
						bestMove = new int[] { i, j };
					}
				}
			}
		}
		return bestMove;
	}

	private int minimax(Board board, boolean isMaximizing, int lastRow, int lastCol, int lastPlayer, int depth) {

		if (board.getWinLine(lastRow, lastCol, lastPlayer) != null) {
			return lastPlayer == 2 ? WIN_SCORE + depth : LOSE_SCORE - depth;
		}

		if (board.isBoardFull()) {
			return 0;
		}

		if (depth == 0) {
			return 0;
		}

		if (isMaximizing) {
			int best = Integer.MIN_VALUE;
			for (int i = 0; i < Board.SIZE; i++) {
				for (int j = 0; j < Board.SIZE; j++) {
					if (board.getCell(i, j) == 0) {
						int player = board.makeMove(i, j);
						int score = minimax(board, false, i, j, player, depth - 1);
						board.undoMove(i, j);
						best = Math.max(best, score);
					}
				}
			}
			return best;
		} else {
			int best = Integer.MAX_VALUE;
			for (int i = 0; i < Board.SIZE; i++) {
				for (int j = 0; j < Board.SIZE; j++) {
					if (board.getCell(i, j) == 0) {
						int player = board.makeMove(i, j);
						int score = minimax(board, true, i, j, player, depth - 1);
						board.undoMove(i, j);
						best = Math.min(best, score);
					}
				}
			}
			return best;
		}
	}
}
