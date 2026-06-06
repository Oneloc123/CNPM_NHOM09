package aiService;

import java.util.ArrayList;
import java.util.List;
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

// =============== NÂNG CẤP 1: CHẾ ĐỘ DỄ ===============
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

	/* UC-003.3.4 - Đánh giá trạng thái bằng Alpha-Beta
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
// =============== NÂNG CẤP 3: HÀM ĐÁNH GIÁ HEURISTIC ===============   
    /**
     * UC-003.3.4 - Đánh giá heuristic bàn cờ
     * Được gọi khi:
     * - Hết độ sâu tìm kiếm.
     * - Chưa xác định thắng/thua.
     * Điểm đánh giá:
     * Score = AI Score- Human Score
     * Dương: AI đang có lợi thế.
     * Âm: Người chơi đang có lợi thế.
     */
    private int evaluateBoard(Board board) {
        int aiScore = 0;	// Điểm của AI
        int humanScore = 0;	// Điểm của người chơi
        int size = board.getSize();
        int center = size / 2;
        
        // Đánh giá từng ô trên bàn cờ
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int cell = board.getCell(i, j);
                if (cell != 0) {
                	// Tính điểm cho vị trí này dựa trên các pattern
                    int cellScore = evaluatePosition(board, i, j, cell);
                    
                    // Điểm thưởng vị trí chiến lược
                    int positionBonus = 0;
                    if (i == center && j == center) {
                        positionBonus = CENTER_BONUS;		// Trung tâm
                    } else if (Math.abs(i - center) <= 1 && Math.abs(j - center) <= 1) {
                        positionBonus = CENTER_BONUS / 2;	// Gần trung tâm
                    } else if (i == 0 || i == size-1 || j == 0 || j == size-1) {
                        positionBonus = EDGE_BONUS;			// Cạnh bàn cờ
                    }
                    
                    // Cộng điểm cho AI hoặc người chơi
                    if (cell == aiPlayerId) {
                        aiScore += cellScore + positionBonus;
                    } else {
                        humanScore += cellScore + positionBonus;
                    }
                }
            }
        }
        // Trả về chênh lệch: AI càng cao càng tốt
        return aiScore - humanScore;
    }
	    /**
     * UC-003.3.4 - Phân tích một vị trí trên bàn cờ
     * Kiểm tra 4 hướng:
     * - Ngang
     * - Dọc
     * - Chéo chính
     * - Chéo phụ
     * Đếm:
     * - Số quân liên tiếp.
     * - Số đầu hở.
     * Kết quả:
     * Trả về điểm heuristic của vị trí.
     */
    private int evaluatePosition(Board board, int row, int col, int player) {
        int totalScore = 0;
        
        // Kiểm tra 4 hướng: ngang, dọc, chéo chính, chéo phụ
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        
        for (int[] dir : directions) {
            int count = 1;  	// Đếm số quân liên tiếp (bắt đầu từ ô hiện tại)
            int openLeft = 0; 	// Đầu bên trái có trống
            int openRight = 0;	// Đầu bên phải có trống
            
            // Đếm về 1 hướng
            // Bắt đầu từ ô kế tiếp theo hướng hiện tại
            int r = row + dir[0];
            int c = col + dir[1];
            // Đếm số quân liên tiếp cùng người chơi
            while (isValid(r, c, board.getSize()) && board.getCell(r, c) == player) {
                count++;
                r += dir[0];
                c += dir[1];
            }
            
            // Kiểm tra đầu hở bên phải: Nếu sau chuỗi quân là ô trống => đầu phải là đầu hở
            if (isValid(r, c, board.getSize()) && board.getCell(r, c) == 0) openRight++;
            
            // Đếm hướng ngược lại
            r = row - dir[0];
            c = col - dir[1];
            // Đếm số quân liên tiếp ở chiều ngược lại
            while (isValid(r, c, board.getSize()) && board.getCell(r, c) == player) {
                count++;
                r -= dir[0];
                c -= dir[1];
            }
            
            // Kiểm tra đầu hở bên trái: Nếu sau chuỗi quân là ô trống => đầu trái là đầu hở
            if (isValid(r, c, board.getSize()) && board.getCell(r, c) == 0) openLeft++;
            
            // Tính điểm dựa trên số quân liên tiếp và đầu hở
            totalScore += getPatternScore(count, openLeft > 0 || openRight > 0);
        }
        return totalScore;
    }
    
    /**
     * UC-003.3.4 - Quy đổi Pattern thành điểm
     * Bảng điểm:
     * 5 quân liên tiếp -> FIVE_IN_A_ROW
     * 4 quân liên tiếp -> FOUR_IN_A_ROW
     * 3 quân liên tiếp -> THREE_IN_A_ROW
     * 2 quân liên tiếp -> TWO_IN_A_ROW
     * 1 quân liên tiếp -> ONE_IN_A_ROW
     * Pattern có đầu hở sẽ được ưu tiên hơn.
     */
    private int getPatternScore(int count, boolean isOpen) {
        switch (count) {
        	// 5 quân -> thắng tuyệt đối
            case 5: return FIVE_IN_A_ROW;
            // 4 quân: có hở thì nguy hiểm hơn
            case 4: return isOpen ? FOUR_IN_A_ROW * 2 : FOUR_IN_A_ROW;
            // 3 quân: có hở thì lợi thế lớn
            case 3: return isOpen ? THREE_IN_A_ROW * 2 : THREE_IN_A_ROW;
            // 2 quân: khởi đầu tốt
            case 2: return isOpen ? TWO_IN_A_ROW * 2 : TWO_IN_A_ROW;
            // 1 quân: khởi đầu
            case 1: return ONE_IN_A_ROW;
            // Không có pattern
            default: return 0;
        }
    }
    
    /**
     * Hàm hỗ trợ kiểm tra tọa độ hợp lệ.
     * Được sử dụng trong:
     * - Đánh giá heuristic.
     * - Kiểm tra các hướng.
     * - Sinh nước đi.
     */
    private boolean isValid(int row, int col, int size) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
// =============== NÂNG CẤP 4: SẮP XẾP NƯỚC ĐI THEO HEURISTIC ===============
    /**
     * UC-003.3.3 - Sắp xếp nước đi theo heuristic
     * Tiêu chí:
     * - Gần trung tâm bàn cờ.
     * - Gần các quân đã đánh.
     * Mục đích: Đưa các nước đi tiềm năng lên đầu danh sách.
     * Lợi ích:
     * Alpha-Beta Pruning cắt tỉa hiệu quả hơn.
     */
    private List<int[]> orderMovesByHeuristic(Board board) {
        int size = board.getSize();
        
        // Danh sách lưu các nước đi kèm điểm heuristic
        List<MoveScore> moveScores = new ArrayList<>();
        
        // Xác định vị trí trung tâm bàn cờ
        int center = size / 2;
        
        // Tính heuristic cho từng ô trống
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
            	// Chỉ xét những ô chưa được đánh
                if (board.getCell(i, j) == 0) {
                    int heuristic = 0;	// Điểm heuristic ban đầu
                    
                    // Ưu tiên tâm bàn cờ
                    if (i == center && j == center) {
                        heuristic += 100;
                    } 
                    // Ô nằm gần trung tâm
                    else if (Math.abs(i - center) <= 1 && Math.abs(j - center) <= 1) {
                        heuristic += 50;
                    }
                    
                    // Ưu tiên ô gần các quân đã đánh
                    for (int di = -2; di <= 2; di++) {
                        for (int dj = -2; dj <= 2; dj++) {
                            int ni = i + di;
                            int nj = j + dj;
                            // Nếu ô lân cận hợp lệ và đã có quân cờ
                            if (isValid(ni, nj, size) && board.getCell(ni, nj) != 0) {
                                heuristic += 5;	// Mỗi quân lân cận +5 điểm
                            }
                        }
                    }
                    
                    moveScores.add(new MoveScore(new int[]{i, j}, heuristic));
                }
            }
        }
        
        // Sắp xếp giảm dần theo heuristic
        moveScores.sort((a, b) -> b.score - a.score);
        
        // Chuyển đổi về danh sách các nước đi
        List<int[]> orderedMoves = new ArrayList<>();
        for (MoveScore ms : moveScores) {
            orderedMoves.add(ms.move);
        }
        return orderedMoves;
    }
    
// ==================== LỚP TRỢ GIÚP ====================
    /**
     * Lớp hỗ trợ lưu trữ:
     * - move  : tọa độ nước đi
     * - score : điểm heuristic
     * Được sử dụng tại bước 3.3.3
     * để sắp xếp danh sách nước đi.
     */
    private class MoveScore {
        int[] move;	// Nước đi [hàng, cột]
        int score;	// Điểm heuristic của nước đi
        
        /**
         * Khởi tạo một đối tượng MoveScore
         * @param move  Nước đi [hàng, cột]
         * @param score Điểm heuristic tương ứng
         */
        MoveScore(int[] move, int score) {
            this.move = move;
            this.score = score;
        }
    }
}
	
}
