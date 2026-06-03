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

// =============== NÂNG CẤP 2: CHẾ ĐỘ KHÓ ===============
    /**
     * AF-02 - Chế độ Khó
     * Minimax + Alpha-Beta Pruning + Heuristic
     * Thực hiện:
     * 3.3.1 Kiểm tra bàn cờ đầy.
     * 3.3.2 Xác định độ sâu:
     *      - Bàn 3x3 -> Depth = 8
     *      - Bàn 5x5 -> Depth = 4
     * 3.3.3 Sắp xếp nước đi theo heuristic.
     * 3.3.4 Duyệt từng nước đi: Thử đánh, dánh giá bằng Alpha-Beta
     * 3.3.6 Chọn nước đi có điểm cao nhất.
     * Kết quả: Trả về nước đi tối ưu cho AI.
     */
    private int[] getBestMoveWithPruning(Board board) {
    	// Kiểm tra bàn cờ đã đầy chưa
        if (board.isBoardFull()) 
        	return null;
        
        // Lấy kích thước bàn cờ
        int size = board.getSize();
        
        // Điều chỉnh độ sâu tìm kiếm theo kích thước bàn cờ
	    // - Bàn 3x3: depth = 8 (tìm gần hết toàn bộ khả năng)
	    // - Bàn 5x5: depth = 4 (giới hạn để tránh treo máy do số ô nhiều)
        int depth = (size == 5) ? 4 : 8;  
        
        // Điểm tốt nhất ban đầu
        int bestScore = Integer.MIN_VALUE;
        
        // Nước đi tốt nhất
        int[] bestMove = null;
        
        // Sắp xếp nước đi theo heuristic để tăng hiệu quả pruning
        List<int[]> moves = orderMovesByHeuristic(board);
        
        // Duyệt từng nước đi khả thi
        for (int[] move : moves) {
        	 // Thử đánh nước đi 
            int player = board.makeMove(move[0], move[1]);
            
            // Gọi đệ quy Alpha-Beta để đánh giá nước đi
            int score = alphaBeta(board, false, Integer.MIN_VALUE, Integer.MAX_VALUE, move[0], move[1], player, depth - 1);
            
            // Hoàn tác nước đi để trả bàn cờ về trạng thái cũ
            board.undoMove(move[0], move[1]);
            
            // Cập nhật nước đi tốt nhất
            if (score > bestScore) {
                bestScore = score;	// Cập nhật điểm tốt nhất
                bestMove = move;	// Lưu lại nước đi tốt nhất
            }
        }
        return bestMove;
    }

	* UC-003.3.4 - Đánh giá trạng thái bằng Alpha-Beta
     * Thuật toán:
     * - Mô phỏng các trạng thái tương lai.
     * - Đánh giá thắng/thua.
     * - Tối ưu bằng Alpha-Beta Pruning.
     * Quy tắc:
     * - AI thắng: WIN_SCORE + depth
     * - AI thua: LOSE_SCORE - depth
     * - Hết độ sâu: evaluateBoard()
     * 3.3.5:
     * Nếu beta <= alpha => Cắt tỉa nhánh hiện tại.
     */
    private int alphaBeta(Board board, boolean isMaximizing, int alpha, int beta, int lastRow, int lastCol, int lastPlayer, int depth) {
        
    	// TRƯỜNG HỢP 1: Phát hiện thắng/thua
        if (board.getWinLine(lastRow, lastCol, lastPlayer) != null) {
            if (lastPlayer == aiPlayerId) {
                return WIN_SCORE + depth;  // AI thắng - cộng depth để ưu tiên thắng nhanh
            } else {
                return LOSE_SCORE - depth; // AI thua - trừ depth để tránh thua chậm
            }
        }
        
     // TRƯỜNG HỢP 2: Hết nước đi hoặc hết độ sâu -> đánh giá heuristic
        if (board.isBoardFull() || depth == 0) {
            return evaluateBoard(board);  
        }
        
        int size = board.getSize();
        
      // TRƯỜNG HỢP 3: Lượt của AI (Maximizing - tìm điểm cao nhất)
        if (isMaximizing) {
        	// Giá trị lớn nhất tìm được
            int maxScore = Integer.MIN_VALUE;
            
            // Sinh các nước đi
            List<int[]> moves = orderMovesByHeuristic(board);
            
            for (int[] move : moves) {
            	// Thử đánh nước đi 
                int player = board.makeMove(move[0], move[1]);
                
                // Đệ quy xuống tầng tiếp theo
                int score = alphaBeta(board, false, alpha, beta, move[0], move[1], player, depth - 1);
                
                // Hoàn tác
                board.undoMove(move[0], move[1]);
                
                // Cập nhật max
                maxScore = Math.max(maxScore, score);
                
                // Cập nhật alpha
                alpha = Math.max(alpha, score);
                
                // Cắt tỉa Alpha-Beta: Nếu beta <= alpha, cắt nhánh này
                if (beta <= alpha) break;  
            }
            return maxScore;
        } 
       // TRƯỜNG HỢP 4: Lượt của đối thủ (Minimizing - tìm điểm thấp nhất)
        else {
        	// Giá trị nhỏ nhất tìm được
            int minScore = Integer.MAX_VALUE;
            
            // Sinh các nước đi
            List<int[]> moves = orderMovesByHeuristic(board);
            
            for (int[] move : moves) {
            	// Thử đánh nước đi 
                int player = board.makeMove(move[0], move[1]);
                
                // Đệ quy xuống tầng tiếp theo
                int score = alphaBeta(board, true, alpha, beta, move[0], move[1], player, depth - 1);
                
                // Hoàn tác
                board.undoMove(move[0], move[1]);
                
                // Cập nhật min
                minScore = Math.min(minScore, score);
                
                // Cập nhật beta
                beta = Math.min(beta, score);
                
                // Cắt tỉa Alpha-Beta: Nếu beta <= alpha, cắt nhánh này
                if (beta <= alpha) break;  
            }
            return minScore;
        }
    }
}
