package controller;

import service.LeaderboardService;
import view.BoardView;
import view.ResultView;


public class ResultController {
    private final ResultView view;
    private final BoardView boardView;
    
    // Lưu cấu hình phiên chạy để phục vụ tính năng "Chơi lại"
    private final String difficulty;
    private final String playerName;
    private final boolean isPlayerFirst;
    private final int size;

    
    /**
     * 5.1.0 - 5.1.3: Khởi tạo luồng kết quả và tích hợp BXH cục bộ.
     *
     * @param resultMessage  Chuỗi kết quả: "<tên> thắng!" / "Máy thắng!" / "Hòa!"
     * @param moveCount      Tổng số nước đi trong ván đấu
     * @param difficulty     Độ khó đang chơi ("Dễ" / "Khó")
     * @param playerName     Tên người chơi do người dùng nhập
     * @param isPlayerFirst  true nếu người chơi đi trước (X), false nếu Máy đi trước
     * @param size           Kích thước bàn cờ (3 hoặc 5)
     * @param boardView      Tham chiếu đến cửa sổ bàn cờ, dùng để đóng khi cần
     */
    public ResultController(String resultMessage, int moveCount, String difficulty, 
                            String playerName, boolean isPlayerFirst, int size, BoardView boardView) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.isPlayerFirst = isPlayerFirst;
        this.size = size;
        this.boardView = boardView;

        // 5.1.0: Chuyển size thành định dạng lưu BXH, ví dụ "3x3" hoặc "5x5".
        String boardSizeStr = size + "x" + size;

        // 5.1.1: Ghi nhận và cộng dồn thành tích theo tên, độ khó, cỡ bàn.
        LeaderboardService.record(playerName, difficulty, boardSizeStr, resultMessage);

        // 5.1.2: Lấy Top 5 sau khi cập nhật để truyền sang ResultView.
        var topEntries = LeaderboardService.getTopEntries();

        // 5.1.3: Tạo màn hình kết quả; BXH chỉ hiện khi người chơi bấm nút.
        this.view = new ResultView(resultMessage, moveCount, topEntries);
        this.view.setController(this);
    }

    public void showResult() {
        this.view.setVisible(true);
    }

    public void handleRestart() {
        // Giải phóng cả 2 màn hình cũ
        view.dispose();
        boardView.dispose();

        // Tạo ván mới với đúng cấu hình cũ nhận được từ game nguồn
        BoardView newView = new view.BoardView(difficulty, playerName, isPlayerFirst, size);
        BoardController newCtrl = new BoardController(difficulty, playerName, isPlayerFirst, size);
        
        newCtrl.setView(newView);
        newView.setController(newCtrl);
        newView.setVisible(true);
    }

    public void handleGoHome() {
        view.dispose();
        boardView.dispose();
        // Quay về menu chính
        new GameController();
    }
}
