package test;

import model.MoveRecord;
import org.junit.jupiter.api.Test;
import view.MoveHistoryPanel;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho lớp MoveHistoryPanel.
 *
 * Do đây là lớp giao diện Swing nên chỉ kiểm tra
 * các chức năng công khai (public API),
 * không kiểm tra màu sắc, font hay renderer.
 */
class MoveHistoryPanelTest {

    /**
     * Kiểm tra khởi tạo panel thành công.
     */
    @Test
    void testConstructor() {

        MoveHistoryPanel panel =
                new MoveHistoryPanel("Người chơi X", "Người chơi O");

        assertNotNull(panel);
    }

    /**
     * Kiểm tra thêm một nước đi vào lịch sử.
     *
     * Mong đợi:
     * - Không phát sinh Exception.
     */
    @Test
    void testAddMove() {

        MoveHistoryPanel panel =
                new MoveHistoryPanel("Người chơi X", "Người chơi O");

        MoveRecord move = new MoveRecord(
                1,                  // số thứ tự nước đi
                1,                  // player X
                2,                  // row
                3,                  // col
                10,                 // điểm nước đi
                10,                 // tổng điểm X
                0,                  // tổng điểm O
                "3 ngang(+10)"      // mô tả
        );

        assertDoesNotThrow(() -> panel.addMove(move));
    }

    /**
     * Kiểm tra thêm nhiều nước đi liên tiếp.
     *
     * Mong đợi:
     * - Danh sách lịch sử cập nhật bình thường.
     * - Không phát sinh Exception.
     */
    @Test
    void testAddMultipleMoves() {

        MoveHistoryPanel panel =
                new MoveHistoryPanel("Người chơi X", "Người chơi O");

        MoveRecord move1 = new MoveRecord(
                1, 1, 0, 0,
                1, 1, 0,
                "đặt cờ(+1)"
        );

        MoveRecord move2 = new MoveRecord(
                2, 2, 1, 1,
                3, 1, 3,
                "2 ngang(+3)"
        );

        MoveRecord move3 = new MoveRecord(
                3, 1, 2, 2,
                10, 11, 3,
                "3 ngang(+10)"
        );

        assertDoesNotThrow(() -> {
            panel.addMove(move1);
            panel.addMove(move2);
            panel.addMove(move3);
        });
    }

    /**
     * Kiểm tra reset sau khi đã có dữ liệu.
     *
     * Mong đợi:
     * - Lịch sử được xóa.
     * - Không phát sinh Exception.
     */
    @Test
    void testResetAfterAddMove() {

        MoveHistoryPanel panel =
                new MoveHistoryPanel("Người chơi X", "Người chơi O");

        MoveRecord move = new MoveRecord(
                1, 1, 0, 0,
                1, 1, 0,
                "đặt cờ(+1)"
        );

        panel.addMove(move);

        assertDoesNotThrow(panel::reset);
    }

    /**
     * Kiểm tra reset khi panel đang rỗng.
     *
     * Mong đợi:
     * - Không phát sinh Exception.
     */
    @Test
    void testResetEmptyPanel() {

        MoveHistoryPanel panel =
                new MoveHistoryPanel("Người chơi X", "Người chơi O");

        assertDoesNotThrow(panel::reset);
    }

    /**
     * Kiểm tra thêm nước đi của người chơi O.
     *
     * Mong đợi:
     * - Hệ thống xử lý bình thường.
     */
    @Test
    void testAddMoveForPlayerO() {

        MoveHistoryPanel panel =
                new MoveHistoryPanel("Người chơi X", "Người chơi O");

        MoveRecord move = new MoveRecord(
                2,                  // nước đi thứ 2
                2,                  // người chơi O
                4,
                4,
                50,
                10,
                50,
                "4 dọc(+50)"
        );

        assertDoesNotThrow(() -> panel.addMove(move));
    }
}