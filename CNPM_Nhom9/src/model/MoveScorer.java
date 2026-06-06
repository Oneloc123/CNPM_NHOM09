package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp MoveScorer dùng để tính điểm cho một nước đi vừa được thực hiện.
 *
 * Điểm được xác định dựa trên số quân liên tiếp mà nước đi mới tạo ra
 * theo 4 hướng:
 *  - Ngang
 *  - Dọc
 *  - Chéo chính (↘)
 *  - Chéo phụ (↙)
 *
 * Bảng điểm:
 *  ≥ winCondition quân liên tiếp  → 1000 điểm (chiến thắng)
 *  winCondition - 1               → 50 điểm
 *  winCondition - 2               → 10 điểm
 *  2 quân liên tiếp               → 3 điểm
 *  Không tạo chuỗi                → 1 điểm
 */
public class MoveScorer {

    /**
     * Tính điểm cho nước đi vừa được đặt lên bàn cờ.
     *
     * Thuật toán:
     * 1. Kiểm tra 4 hướng xung quanh quân vừa đặt.
     * 2. Đếm số quân liên tiếp cùng loại.
     * 3. Xác định điểm tương ứng với độ dài chuỗi.
     * 4. Cộng dồn điểm của tất cả các hướng.
     * 5. Sinh mô tả chi tiết để hiển thị trong lịch sử nước đi.
     *
     * @param matrix ma trận bàn cờ hiện tại
     * @param row hàng của nước đi
     * @param col cột của nước đi
     * @param player người chơi (1 = X, 2 = O)
     * @param winCond số quân liên tiếp để thắng
     * @return đối tượng ScoreResult chứa điểm và mô tả
     */
    //UC2.1.6.c: tính điểm nước đi vừa thực hiện
    public static ScoreResult calculate(int[][] matrix, int row, int col,
                                        int player, int winCond) {
        // Kích thước bàn cờ
        int size = matrix.length;
        // 4 hướng cần kiểm tra
        int[][] directions = {
                {0,1},   // ngang
                {1,0},   // dọc
                {1,1},   // chéo ↘
                {1,-1}   // chéo ↙
        };
        // Tên hướng dùng cho mô tả
        String[] dirNames = {"ngang", "dọc", "chéo↘", "chéo↙"
        };
        // Tổng điểm của nước đi
        int totalScore = 0;
        // Danh sách mô tả điểm theo từng hướng
        List<String> parts = new ArrayList<>();
        // Duyệt qua từng hướng
        for (int d = 0; d < directions.length; d++) {
            int dr = directions[d][0];
            int dc = directions[d][1];
            /*
             * Đếm số quân liên tiếp theo cả hai chiều
             * và cộng thêm quân hiện tại.
             */
            int len = 1
                    + countDir(matrix, size, row, col, dr, dc, player)
                    + countDir(matrix, size, row, col, -dr, -dc, player);
            // Bỏ qua nếu không tạo được chuỗi từ 2 quân trở lên
            if (len < 2) {
                continue;
            }
            // Xác định điểm của chuỗi vừa tạo
            int pts = scoreForLength(len, winCond);
            if (pts > 0) {
                totalScore += pts;
                // Thêm mô tả cho lịch sử nước đi
                parts.add(len + " " + dirNames[d] + "(+" + pts + ")");
            }
        }
        /*
         * Nếu không tạo được chuỗi nào
         * thì cộng 1 điểm cơ bản.
         */
        if (totalScore == 0) {
            totalScore = 1;
            parts.add("đặt cờ(+1)");
        }
        // Ghép các mô tả thành chuỗi hoàn chỉnh
        String desc = String.join(", ", parts);
        return new ScoreResult(totalScore, desc);
    }

    /**
     * Đếm số quân liên tiếp theo một hướng xác định.
     *
     * Ví dụ:
     *  dr = 0, dc = 1  → sang phải
     *  dr = 1, dc = 0  → xuống dưới
     *
     * @return số quân liên tiếp cùng người chơi
     */
    private static int countDir(int[][] m, int size,
                                int r, int c,
                                int dr, int dc,
                                int player) {
        int count = 0;
        // Di chuyển sang ô tiếp theo
        r += dr;
        c += dc;
        while (r >= 0 && r < size
                && c >= 0 && c < size
                && m[r][c] == player) {
            count++;

            r += dr;
            c += dc;
        }
        return count;
    }

    /**
     * Chuyển độ dài chuỗi thành số điểm.
     *
     * @param len độ dài chuỗi liên tiếp
     * @param winCond điều kiện thắng
     * @return số điểm tương ứng
     */
    private static int scoreForLength(int len, int winCond) {

        if (len >= winCond)
            return 1000;

        if (len == winCond - 1)
            return 50;

        if (len == winCond - 2)
            return 10;

        if (len >= 2)
            return 3;

        return 0;
    }

    /**
     * Lớp lưu kết quả tính điểm.
     */
    public static class ScoreResult {

        // Điểm số của nước đi
        public final int score;

        // Mô tả chi tiết cách tính điểm
        public final String description;

        ScoreResult(int score, String description) {
            this.score = score;
            this.description = description;
        }
    }
}