package aiService;

import java.util.Random;
import model.Board;

public class AIService {
	private String difficulty;

	// ID của người chơi (1: X, 2: O)
    private int humanPlayerId;
	
	// =============== [SỬA ĐỔI NÂNG CẤP ĐỒNG BỘ UC1] ===============
	// Thêm thuộc tính để AI biết mình đang cầm quân đi trước (1) hay đi sau (2)
	private int aiPlayerId; 
	// =============================================================
	
    // Điểm số khi AI thắng trong giải thuật Minimax
    private static final int WIN_SCORE =  1000;
	// Điểm số khi AI thua trong giải thuật Minimax
    private static final int LOSE_SCORE = -1000;
    
    
// ==================== HEURISTIC SCORES ====================
    // Điểm cho số quân liên tiếp
    private static final int FIVE_IN_A_ROW = 100000;  // Thắng
    private static final int FOUR_IN_A_ROW = 10000;   // 4 quân liên tiếp
    private static final int THREE_IN_A_ROW = 1000;   // 3 quân liên tiếp
    private static final int TWO_IN_A_ROW = 100;      // 2 quân liên tiếp
    private static final int ONE_IN_A_ROW = 10;       // 1 quân
    
    
// ==================== VỊ TRÍ CHIẾN LƯỢC ====================
    // Điểm thưởng cho vị trí chiến lược
    private static final int CENTER_BONUS = 50;		// Cho ô trung tâm - Vị trí quan trọng nhất
    private static final int EDGE_BONUS = 10;		// Cho ô cạnh bàn cờ

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
		this.humanPlayerId = isPlayerFirst ? 1 : 2;	// Nếu người chơi đi trước: AI = X (1) và Người chơi = O (2)
	}
	// =============================================================

	public String getDifficulty() {
        return difficulty;
    }

	// =============== NÂNG CẤP ===============
	/**
	 * UC-003.1.2 - Xác định nước đi của AI
	 * AI lựa chọn chiến lược dựa trên độ khó:
	 * - Dễ  : Smart Random
	 * - Khó : Alpha-Beta Pruning + Heuristic
	 * @param board Trạng thái bàn cờ hiện tại
	 * @return Tọa độ nước đi [row, col]
	 */
	public int[] getNextMove(Board board) {
		// Chế độ Dễ: Sử dụng random thông minh
		if (difficulty.equals("Dễ")) {
			return getSmartRandomMove(board);
		} 
		// Chế độ Khó: Sử dụng Minimax với Alpha-Beta Pruning
		else {
			return getBestMoveWithPruning(board);
		}
	}

// =============== NÂNG CẤP 1: CHẾ ĐỘ DỄ===============
	/**
	 * AF-01 - Chế độ Dễ (Random thông minh)
	 * Thực hiện:
	 * 3.2.1 Kiểm tra bàn cờ đầy.
	 * 3.2.2 Thu thập toàn bộ ô trống.
	 * 3.2.3 Gán trọng số theo vị trí:
	 *      - Trung tâm: 100
	 *      - Gần trung tâm: 50
	 *      - Góc: 20
	 *      - Cạnh: 10
	 *      - Khác: 1
	 * 3.2.4 Tạo danh sách trọng số.
	 * 3.2.5 Chọn ngẫu nhiên một ô.
	 * 3.2.6 Trả về luồng chính để đặt quân.
	 * Mục tiêu:
	 * AI vẫn mang tính ngẫu nhiên nhưng ưu tiên
	 * các vị trí chiến lược.
	 */
    private int[] getSmartRandomMove(Board board) {
    	// Kiểm tra bàn cờ đã đầy chưa
        if (board.isBoardFull()) 
        	return null;
        
        int size = board.getSize();
        List<int[]> emptyCells = new ArrayList<>();
        
        // Thu thập tất cả ô trống trên bàn cờ
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getCell(i, j) == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        
        if (emptyCells.isEmpty()) return null;
        
        // Tạo danh sách có trọng số dựa trên vị trí chiến lược
        List<int[]> prioritizedCells = new ArrayList<>();
        int center = size / 2;	// Tâm bàn cờ
        
        for (int[] cell : emptyCells) {
            int priority = 1;	// Trọng số mặc định
            
            /* Xác định trọng số theo vị trí:
             * - Trung tâm: quan trọng nhất (priority = 100)
             * - Gần trung tâm (bán kính 1): quan trọng vừa (priority = 50)
             * - Góc bàn cờ: chiến lược tấn công (priority = 20)
             * - Cạnh bàn cờ: ít quan trọng hơn (priority = 10)
             */
            // Trung tâm bàn cờ
            if (cell[0] == center && cell[1] == center) {
                priority = 100;	
            }
            // Gần trung tâm
            else if (Math.abs(cell[0] - center) <= 1 && Math.abs(cell[1] - center) <= 1) {
                priority = 50;
            }
            // Góc bàn cờ
            else if ((cell[0] == 0 || cell[0] == size-1) && (cell[1] == 0 || cell[1] == size-1)) {
                priority = 20;
            }
            // Cạnh bàn cờ
            else if (cell[0] == 0 || cell[0] == size-1 || cell[1] == 0 || cell[1] == size-1) {
                priority = 10;
            }
            
            // Nhân bản ô theo trọng số
            for (int i = 0; i < priority; i++) {
                prioritizedCells.add(cell);
            }
        }
        
        // Chọn ngẫu nhiên từ danh sách đã có trọng số
        Random rand = new Random();
        return prioritizedCells.get(rand.nextInt(prioritizedCells.size()));
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
