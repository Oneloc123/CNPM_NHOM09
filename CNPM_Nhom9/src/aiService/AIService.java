package aiService;

import java.util.Random;
import model.Board;

public class AIService {
	private String difficulty;
	
	// =============== [SỬA ĐỔI NÂNG CẤP ĐỒNG BỘ UC1] ===============
	// Thêm thuộc tính để AI biết mình đang cầm quân đi trước (1) hay đi sau (2)
	private int aiPlayerId; 
	// =============================================================
	
    private static final int WIN_SCORE =  1000;
    private static final int LOSE_SCORE = -1000;
    private static final int MAX_DEPTH = 9;

	/*
	UC1.1.6.5: Hệ thống khởi tạo AIService cho bàn cờ
	 */
	// =============== [SỬA ĐỔI NÂNG CẤP ĐỒNG BỘ UC1] ===============
	// Sửa constructor để nhận biết lượt đi, từ đó xác định ID của AI
	public AIService(String difficulty, boolean isPlayerFirst) {
		this.difficulty = difficulty;
		
		// Nếu người chơi đi trước -> Người chơi là 1, AI là 2
		// Nếu máy đi trước -> AI là 1, Người chơi là 2
		this.aiPlayerId = isPlayerFirst ? 2 : 1;
	}
	// =============================================================

	public String getDifficulty() {
        return difficulty;
    }

	public int[] getNextMove(Board board) {
		if (difficulty.equals("Dễ")) {
			return getRandomMove(board);
		} else {
			int depth = (board.getSize() == 5) ? 3 : MAX_DEPTH;
			return getBestMove(board, depth);
		}
	}

	private int[] getRandomMove(Board board) {
		if (board.isBoardFull())
			return null;

		Random rand = new Random();
		int r, c;
		int boardSize = board.getSize(); // ĐÃ SỬA
		do {
			r = rand.nextInt(boardSize);
			c = rand.nextInt(boardSize);
		} while (board.getCell(r, c) != 0);
		return new int[] { r, c };
	}

	private int[] getBestMove(Board board, int depth) {
		if (board.isBoardFull())
			return null;

		int bestScore = Integer.MIN_VALUE;
		int[] bestMove = null;
		int boardSize = board.getSize(); // ĐÃ SỬA

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board.getCell(i, j) == 0) {
					int player = board.makeMove(i, j);
					int score = minimax(board, false, i, j, player, depth - 1);
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
			return lastPlayer == this.aiPlayerId ? WIN_SCORE + depth : LOSE_SCORE - depth;
		}

		if (board.isBoardFull() || depth == 0) {
			return 0;
		}

		int boardSize = board.getSize(); // ĐÃ SỬA
		if (isMaximizing) {
			int best = Integer.MIN_VALUE;
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
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
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
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