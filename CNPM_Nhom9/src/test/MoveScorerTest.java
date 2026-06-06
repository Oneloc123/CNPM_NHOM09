package test;

import model.MoveScorer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho lớp MoveScorer.
 *
 * Mục đích:
 * Kiểm tra tính chính xác của thuật toán tính điểm
 * cho các trường hợp khác nhau trên bàn cờ.
 */
class MoveScorerTest {

    /**
     * TC01:
     * Kiểm tra khi người chơi chỉ đặt một quân đơn lẻ.
     *
     * Kết quả mong đợi:
     * - Nhận 1 điểm cơ bản.
     * - Mô tả là "đặt cờ(+1)".
     */
    @Test
    void testSingleMove() {

        int[][] board = new int[5][5];
        board[2][2] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(1, result.score);
        assertEquals("đặt cờ(+1)", result.description);
    }

    /**
     * TC02:
     * Tạo chuỗi ngang gồm 2 quân liên tiếp.
     *
     * Kết quả mong đợi:
     * - Nhận 3 điểm.
     */
    @Test
    void testTwoHorizontal() {

        int[][] board = new int[5][5];

        board[2][1] = 1;
        board[2][2] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(3, result.score);
    }

    /**
     * TC03:
     * Tạo chuỗi ngang gồm 3 quân liên tiếp.
     *
     * Kết quả mong đợi:
     * - Nhận 10 điểm.
     */
    @Test
    void testThreeHorizontal() {

        int[][] board = new int[5][5];

        board[2][0] = 1;
        board[2][1] = 1;
        board[2][2] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(10, result.score);
    }

    /**
     * TC04:
     * Tạo chuỗi ngang gồm 4 quân liên tiếp.
     *
     * Kết quả mong đợi:
     * - Nhận 50 điểm.
     */
    @Test
    void testFourHorizontal() {

        int[][] board = new int[5][5];

        board[2][0] = 1;
        board[2][1] = 1;
        board[2][2] = 1;
        board[2][3] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(50, result.score);
    }

    /**
     * TC05:
     * Tạo đủ 5 quân liên tiếp.
     *
     * Kết quả mong đợi:
     * - Được tính là chiến thắng.
     * - Nhận 1000 điểm.
     */
    @Test
    void testWinningMove() {

        int[][] board = new int[5][5];

        for (int i = 0; i < 5; i++) {
            board[2][i] = 1;
        }

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(1000, result.score);
    }

    /**
     * TC06:
     * Kiểm tra chuỗi dọc gồm 3 quân.
     */
    @Test
    void testVerticalLine() {

        int[][] board = new int[5][5];

        board[0][2] = 1;
        board[1][2] = 1;
        board[2][2] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(10, result.score);
    }

    /**
     * TC07:
     * Kiểm tra chuỗi chéo chính (↘).
     */
    @Test
    void testDiagonalLine() {

        int[][] board = new int[5][5];

        board[0][0] = 1;
        board[1][1] = 1;
        board[2][2] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(10, result.score);
    }

    /**
     * TC08:
     * Kiểm tra một nước đi đồng thời tạo
     * chuỗi ngang và chuỗi dọc.
     *
     * Kết quả mong đợi:
     * - 10 điểm ngang
     * - 10 điểm dọc
     * - Tổng 20 điểm
     */
    @Test
    void testMultipleDirections() {

        int[][] board = new int[5][5];

        board[2][1] = 1;
        board[2][2] = 1;
        board[2][3] = 1;

        board[1][2] = 1;
        board[3][2] = 1;

        MoveScorer.ScoreResult result =
                MoveScorer.calculate(board, 2, 2, 1, 5);

        assertEquals(20, result.score);
    }
}