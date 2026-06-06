package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Board;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử đơn vị cấu phần Model Bàn cờ - UC-001")
public class BoardTest {

    @Test
    @DisplayName("TC-B01: Kiểm tra khởi tạo bàn cờ kích thước 3x3")
    public void testInitBoard3x3() {
        // Kịch bản: Người chơi chọn kích thước 3x3 ở giao diện Trang chủ
        Board board = new Board(3);
        
        // Kiểm tra xem ma trận và luật chơi có đúng đặc tả UC-001 không
        assertEquals(3, board.getSize(), "LỖI: Kích thước cạnh bàn cờ phải bằng 3.");
        assertEquals(3, board.getWinCondition(), "LỖI: Bàn 3x3 thì điều kiện thắng phải là 3 quân liên tiếp.");
        assertEquals(0, board.getMoveCount(), "LỖI: Ván đấu mới khởi tạo thì số nước đi phải bằng 0.");
        assertTrue(board.isXTurn(), "LỖI: Theo quy ước hệ thống, lượt đi đầu tiên luôn thuộc về quân X.");
    }

    @Test
    @DisplayName("TC-B02: Kiểm tra khởi tạo bàn cờ kích thước 5x5")
    public void testInitBoard5x5() {
        // Kịch bản: Người chơi chọn kích thước 5x5 ở giao diện Trang chủ
        Board board = new Board(5);
        
        // Kiểm tra logic tự cập nhật điều kiện thắng động trong mã nguồn
        assertEquals(5, board.getSize(), "LỖI: Kích thước cạnh bàn cờ phải bằng 5.");
        assertEquals(4, board.getWinCondition(), "LỖI: Theo code Board.java, bàn 5x5 cần 4 quân liên tiếp để thắng.");
    }

    @Test
    @DisplayName("TC-B03: Kiểm tra trạng thái ma trận trống hoàn toàn lúc vừa tạo ván")
    public void testInitialMatrixIsEmpty() {
        int size = 3;
        Board board = new Board(size);
        
        // Quét toàn bộ ma trận vừa tạo, tất cả các ô phải bằng 0 (chưa được đánh)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                assertEquals(0, board.getCell(i, j), 
                    String.format("LỖI: Ô (%d, %d) phải trống (giá trị 0) khi vừa tạo ván.", i, j));
            }
        }
    }

    @Test
    @DisplayName("TC-B04: Kiểm tra tính sẵn sàng của ma trận cho nước đi đầu tiên (Luồng AF-3)")
    public void testReadyForFirstMove() {
        Board board = new Board(3);
        
        // Mô phỏng nước đi đầu tiên của ván đấu (Cần thiết để test tính hợp lệ trước khi chuyển giao UC)
        int playerResult = board.makeMove(0, 0);
        
        assertEquals(1, playerResult, "LỖI: Người đi đầu tiên (X) phải trả về ID là 1.");
        assertEquals(1, board.getCell(0, 0), "LỖI: Dữ liệu ô (0, 0) không được ghi nhận chính xác.");
        assertFalse(board.isXTurn(), "LỖI: Hệ thống chưa chuyển lượt sang quân O sau nước đi đầu tiên.");
    }
}