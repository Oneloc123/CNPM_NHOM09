package controller;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import aiService.AIService;
import model.Board;
import view.BoardView;

public class BoardController {
    private final Board board;
    private final AIService ai;
    private BoardView view;

    // Lưu trữ thông số cấu hình của ván đấu
    private final String playerName;
    private final boolean isPlayerFirst;
    private final String xPlayerName;
    private final String oPlayerName;
    
    // Thuộc tính lưu kích thước giao diện để dùng khi nhấn nút Chơi lại
    private final int size;

    /*
    UC1.1.6.3: Hệ thống khởi tạo Controller cho bàn cờ
     */
    // =============== [SỬA LỖI ĐỒNG BỘ THAM SỐ] ===============
 // =============== [SỬA LỖI ĐỒNG BỘ THAM SỐ & SIZE] ===============
    public BoardController(String difficulty, String playerName, boolean isPlayerFirst, int size) {
        this.playerName = playerName;
        this.isPlayerFirst = isPlayerFirst;
        this.size = size;
        
        this.xPlayerName = isPlayerFirst ? playerName : "Máy";
        this.oPlayerName = isPlayerFirst ? "Máy" : playerName;

        // ĐÃ SỬA: Khởi tạo dữ liệu bàn cờ động theo size được chọn
        this.board = new Board(size);

        // ĐÃ SỬA LỖI: Gọi đúng constructor 2 tham số của AIService.java
        this.ai = new AIService(difficulty, isPlayerFirst);
    }
    // =========================================================

    /*
    UC1.1.6.6: Hệ thống thiết lập ViewBoard cho BoardController
     */
    public void setView(BoardView view) {
        // Gán giao diện bàn cờ cho controller
        this.view = view;

        // Nếu Máy được cấu hình đi trước (!isPlayerFirst), tự động kích hoạt lượt quét của AI ngay khi mở thảm cờ
        if (!isPlayerFirst) {
            view.setInputEnabled(false);
            view.setStatus("Máy đang suy nghĩ...");
            triggerAIMove();
        }
    }

    // UC 2.1.3 Controller tiếp nhận yêu cầu thực hiện nước đi
    // Mô tả:
    // Controller nhận tọa độ ô cờ từ View.
    // Chuyển thông tin cho lớp Board kiểm tra tính hợp lệ của nước đi.
    public void handlePlayerMove(int row, int col) {

        // UC 2.1.4 Hệ thống kiểm tra tính hợp lệ của nước đi
        // Mô tả:
        // Kiểm tra:
        // tọa độ có nằm trong phạm vi bàn cờ.
        // Ô cờ đã được đánh dấu trước đó hay chưa.
        int player = board.makeMove(row, col);
//        Kết quả trả về
//
//        Giá trị	Ý nghĩa
//        0	        Nước đi không hợp lệ
//        1	        Người chơi X
//        2	        Người chơi O

        // UC2.2.1 Nước đi không hợp lệ
        // giá trị trả về từ phương thức của bước 2.1.4 là 0
        // 0 là giá trị thể hiện nước đi không hợp lệ
        if (player == 0) {
            // UC 2.2.2. Hệ thống hiển thị thông báo "Nước đi không hợp lệ, vui lòng chọn nước đi khác"
            // controller gọi phương thức showError của lớp View để hiển thị giao diện hộp thông báo
            SwingUtilities.invokeLater(() -> {
                // UC 2.2.2a Hệ thống làm nổi bật ô không hợp lệ
                // gọi phương thức highlightInvalidCell() của lớp View
                // để thực hiện hiệu ứng nhấp nháy màu đỏ trên ô cờ lỗi
                view.highlightInvalidCell(row, col);

                view.showError(
                        "Nước đi không hợp lệ, vui lòng chọn nước đi khác"
                );

            });
            return;
        }
        // UC2.1.6: Hệ thống cật nhật giao diện bàn cờ  theo ma trận bàn cờ 2 chiều
        // vẽ lại bàn cờ dựa trên dối tượng matrix 2 chiều của lớp board
        view.updateBoard(board);
        // UC2.1.7 : hệ thống chuyển sang UC-004 để kiểm tra kết thúc ván đấu
//        Mô tả:
//
//        Sau khi ghi nhận nước đi, hệ thống kiểm tra:
//
//        n quân liên tiếp theo hàng ngang.
//        n quân liên tiếp theo hàng dọc.
//        n quân liên tiếp theo đường chéo chính.
//        n quân liên tiếp theo đường chéo phụ.
//      ghi chú : n là số lượng quân để chiến thắng

        int[][] winLine = board.getWinLine(row, col, player);
        if (winLine != null) {
            // UC 2.3.1 : hệ thống phát hiện ván đấu đủ điều kiện kết thúc và tiếp tục UC-004
            view.highlightWin(winLine, true);
            
            // Người chơi click chuột thắng -> Thông báo Người chơi thắng
            SwingUtilities.invokeLater(() -> view.showEndGame(playerName + " thắng!"));
            return;
        }
        //UC 2.1.8 Hệ thống kiểm tra hòa
        //Nếu toàn bộ bàn cờ đã được đánh dấu và không có người thắng:
        if (board.isBoardFull()) {
            // UC 2.3.1 : hệ thống phát hiện ván đấu đủ điều kiện kết thúc và tiếp tục UC-004
            SwingUtilities.invokeLater(() -> view.showEndGame("Hòa!"));
            return;
        }
        // UC 2.1.9 : Hệ thống chuyển san lượt cho đối thủ Nếu chưa thắng và chưa hòa
//            Mô tả:
//                Khóa thao tác người chơi.
//                Hiển thị trạng thái AI đang suy nghĩ.
//                Chuyển sang UC-003 Thực hiện nước đi của AI.
        view.setInputEnabled(false);
        view.setStatus("Máy đang suy nghĩ...");
        triggerAIMove();
    }

    private void triggerAIMove() {
        new SwingWorker<int[], Void>() {

            @Override
            protected int[] doInBackground() {
                return ai.getNextMove(board);
            }

            @Override
            protected void done() {
                try {
                    int[] move = get();
                    if (move == null) return;

                    int player = board.makeMove(move[0], move[1]);
                    view.updateBoard(board);
                    view.setInputEnabled(true);

                    int[][] winLine = board.getWinLine(move[0], move[1], player);
                    if (winLine != null) {
                        view.highlightWin(winLine, false);
                        
                        // Luồng tính toán của Máy thắng -> Thông báo Máy thắng
                        SwingUtilities.invokeLater(() -> view.showEndGame("Máy thắng!"));
                        return;
                    }

                    if (board.isBoardFull()) {
                        SwingUtilities.invokeLater(() -> view.showEndGame("Hòa!"));
                        return;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    view.setInputEnabled(true);
                }
            }
        }.execute();
    }

    public void handleRestart() {
        String difficulty = ai.getDifficulty();
        view.dispose();

        // Truyền đầy đủ tham số bao gồm cả 'size' vào ván đấu mới
        BoardView       newView = new BoardView(difficulty, playerName, isPlayerFirst, size);
        BoardController newCtrl = new BoardController(difficulty, playerName, isPlayerFirst, size);
        newCtrl.setView(newView);
        newView.setController(newCtrl);
        newView.setVisible(true);
    }

    public void handleGoHome() {
        view.dispose();
        new GameController();
    }
}