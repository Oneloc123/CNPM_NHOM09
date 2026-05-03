package aiService;


import java.util.Random;

import model.Board;

public class AIService {
    private String difficulty;

    public AIService(String difficulty) {
        this.difficulty = difficulty;
    }

    // Hàm tìm nước đi cho máy
    public int[] getNextMove(Board board) {
        int size = board.getSize();
        
        if (difficulty.equals("Dễ")) {
            // Logic DỄ: Đánh ngẫu nhiên vào ô trống
            Random rand = new Random();
            int r, c;
            do {
                r = rand.nextInt(size);
                c = rand.nextInt(size);
            } while (board.getCell(r, c) != 0); // Lặp đến khi tìm được ô trống
            return new int[]{r, c};
        } else {
            // Logic KHÓ: Dùng thuật toán Minimax (Bạn sẽ code sau)
            // Tạm thời trả về ngẫu nhiên
            return new int[]{0, 0}; 
        }
    }
}
