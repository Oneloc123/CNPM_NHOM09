package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Board;

public class BoardUnitTest {
    private Board board3x3;
    private Board board5x5;

    @BeforeEach
    public void setUp() {
        // Khởi tạo bàn cờ mới trước mỗi test case để đảm bảo tính độc lập
        board3x3 = new Board(3);
        board5x5 = new Board(5);
    }

    @Test
    public void testInitialization() {
        // Kiểm tra khởi tạo kích thước bàn cờ
        assertEquals(3, board3x3.getSize());
        assertEquals(5, board5x5.getSize());
        
        // Kiểm tra bộ đếm nước đi ban đầu phải bằng 0 (Commit 1)
        assertEquals(0, board3x3.getMoveCount());
        
        // Lượt đầu tiên phải là X (người chơi)
        assertTrue(board3x3.isXTurn());
    }

    @Test
    public void testMoveCounterAndTurnSwitching() {
        // Người chơi (X - ID: 1) thực hiện nước đi tại vị trí (0, 0)
        int player1 = board3x3.makeMove(0, 0);
        assertEquals(1, player1);
        assertEquals(1, board3x3.getMoveCount(), "Bộ đếm nước đi phải tăng lên 1 sau nước đi đầu tiên.");
        assertFalse(board3x3.isXTurn(), "Lượt đi phải chuyển sang cho O (Máy).");

        // Máy (O - ID: 2) thực hiện nước đi tại vị trí (1, 1)
        int player2 = board3x3.makeMove(1, 1);
        assertEquals(2, player2);
        assertEquals(2, board3x3.getMoveCount(), "Bộ đếm nước đi phải tăng lên 2.");
        assertTrue(board3x3.isXTurn(), "Lượt đi phải quay lại cho X.");
    }

    @Test
    public void testInvalidMoveShouldNotIncrementCounter() {
        board3x3.makeMove(0, 0); // Nước đi hợp lệ đầu tiên (moveCount = 1)
        
        // Thử đánh vào ô đã chọn (0, 0) -> Quy tắc nghiệp vụ BR-002
        int invalidPlayer = board3x3.makeMove(0, 0);
        assertEquals(0, invalidPlayer, "Nước đi vào ô đã có quân phải trả về 0.");
        assertEquals(1, board3x3.getMoveCount(), "Bộ đếm nước đi KHÔNG được tăng khi đi lỗi.");
    }

    @Test
    public void testUndoMoveDecrementsCounter() {
        board3x3.makeMove(0, 0);
        board3x3.makeMove(1, 1);
        assertEquals(2, board3x3.getMoveCount());

        // Thực hiện hoàn tác nước đi (0, 0)
        board3x3.undoMove(0, 0);
        assertEquals(1, board3x3.getMoveCount(), "Bộ đếm nước đi phải giảm đi 1 sau khi undo.");
    }

    @Test
    public void testWinConditionHorizontal() {
        /* Kịch bản kiểm thử luồng chính UC-004: X thắng hàng ngang (Hàng 0)
           Bàn cờ mô phỏng:
           X | X | X (Nước đi quyết định tại 0,2)
           O | O | 
             |   | 
        */
        board3x3.makeMove(0, 0); // X
        board3x3.makeMove(1, 0); // O
        board3x3.makeMove(0, 1); // X
        board3x3.makeMove(1, 1); // O
        
        // Trước nước đi quyết định, chưa có ai thắng
        assertNull(board3x3.getWinLine(0, 1, 1));

        // Nước đi quyết định của X tại (0, 2)
        int lastPlayer = board3x3.makeMove(0, 2);
        int[][] winLine = board3x3.getWinLine(0, 2, lastPlayer);

        // Kiểm tra kết quả trả về của hàm getWinLine phục vụ UC-004
        assertNotNull(winLine, "Hệ thống phải phát hiện được đường thắng hàng ngang.");
        assertEquals(3, winLine.length, "Đường thắng của bàn cờ 3x3 phải có độ dài là 3 ô.");
        
        // Kiểm tra tọa độ các ô trong đường thắng
        assertArrayEquals(new int[]{0, 0}, winLine[0]);
        assertArrayEquals(new int[]{0, 1}, winLine[1]);
        assertArrayEquals(new int[]{0, 2}, winLine[2]);
    }

    @Test
    public void testWinConditionDiagonal() {
        /* Kịch bản kiểm thử luồng chính UC-004: X thắng đường chéo chính
           Bàn cờ mô phỏng:
           X | O | 
             | X | O
             |   | X (Nước đi quyết định tại 2,2)
        */
        board3x3.makeMove(0, 0); // X
        board3x3.makeMove(0, 1); // O
        board3x3.makeMove(1, 1); // X
        board3x3.makeMove(1, 2); // O
        
        int lastPlayer = board3x3.makeMove(2, 2); // X quyết định
        int[][] winLine = board3x3.getWinLine(2, 2, lastPlayer);

        assertNotNull(winLine, "Hệ thống phải phát hiện đường thắng chéo chính.");
        assertArrayEquals(new int[]{0, 0}, winLine[0]);
        assertArrayEquals(new int[]{1, 1}, winLine[1]);
        assertArrayEquals(new int[]{2, 2}, winLine[2]);
    }

    @Test
    public void testDrawCondition() {
        /* Kịch bản kiểm thử luồng thay thế AF-01: Hòa cờ
           Điền đầy 9 ô sao cho không bên nào tạo được 3 ký hiệu liên tiếp
           Bàn cờ mô phỏng:
           X | O | X
           X | O | O
           O | X | X
        */
        board3x3.makeMove(0, 0); // X (0,0)
        board3x3.makeMove(0, 1); // O (0,1)
        board3x3.makeMove(0, 2); // X (0,2)
        board3x3.makeMove(1, 1); // O (1,1)
        board3x3.makeMove(1, 0); // X (1,0)
        board3x3.makeMove(1, 2); // O (1,2)
        board3x3.makeMove(2, 1); // X (2,1)
        board3x3.makeMove(2, 0); // O (2,0)
        
        // Trước nước đi cuối cùng, bàn cờ chưa đầy và chưa hòa
        assertFalse(board3x3.isBoardFull());
        
        int lastPlayer = board3x3.makeMove(2, 2); // X đánh ô cuối cùng tại (2,2)

        // Kiểm tra điều kiện hòa theo luồng AF-01
        assertNull(board3x3.getWinLine(2, 2, lastPlayer), "Không được có đường thắng.");
        assertTrue(board3x3.isBoardFull(), "Bàn cờ phải được xác nhận là đã đầy quân.");
        assertEquals(9, board3x3.getMoveCount(), "Tổng số nước đi của ván hòa 3x3 phải đạt tối đa là 9.");
    }

    @Test
    public void testDynamicWinConditionFor5x5() {
        /* Kiểm tra quy tắc nghiệp vụ mở rộng trên bàn cờ 5x5: Cần 4 quân để thắng
           Bàn cờ mô phỏng:
           X | X | X | X |  (Hàng 0 thắng với 4 quân liên tiếp)
           O | O | O |   | 
           ...
        */
        board5x5.makeMove(0, 0); // X
        board5x5.makeMove(1, 0); // O
        board5x5.makeMove(0, 1); // X
        board5x5.makeMove(1, 1); // O
        board5x5.makeMove(0, 2); // X
        board5x5.makeMove(1, 2); // O
        
        // Đánh quân thứ 3, bàn cờ 5x5 chưa thể thắng (vì winCondition = 4)
        int player = board5x5.makeMove(0, 3); // X quân thứ 4
        int[][] winLine = board5x5.getWinLine(0, 3, player);

        assertNotNull(winLine, "Bàn cờ 5x5 phải kích hoạt chiến thắng khi đạt đủ 4 quân liên tiếp.");
        assertEquals(4, winLine.length, "Độ dài đường thắng của bàn cờ 5x5 phải là 4.");
    }
}