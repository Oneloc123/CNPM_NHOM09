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
     * 5.1.0 - 5.1.4: Khởi tạo luồng kết quả và tích hợp BXH cục bộ.
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

        // 5.1.0: Ván đấu vừa kết thúc — chuyển size thành định dạng lưu BXH, ví dụ "3x3" hoặc "5x5".
        String boardSizeStr = size + "x" + size;

        // 5.1.1: Ghi nhận và cộng dồn thành tích theo tên, độ khó, cỡ bàn.
        LeaderboardService.record(playerName, difficulty, boardSizeStr, resultMessage);

        // 5.1.2: Xác định loại kết quả để cộng dồn đúng chỉ số
        //        (logic nằm bên trong LeaderboardService.record():
        //         máy thắng → losses++; hòa → draws++; người chơi thắng → wins++).
 
        // 5.1.3: Lưu lại toàn bộ dữ liệu BXH sau khi đã cập nhật
        //        (thực hiện ngay cuối LeaderboardService.record() qua saveAll()).
 
        // 5.1.4: Truy vấn danh sách Top 5 sau khi cập nhật, sắp xếp theo wins rồi totalGames.        
        var topEntries = LeaderboardService.getTopEntries();

        // 5.1.5: Tạo màn hình kết quả với đầy đủ thông tin ván đấu và 3 nút điều hướng;
        //        BXH chỉ hiện khi người chơi bấm nút BXH.
        this.view = new ResultView(resultMessage, moveCount, topEntries);
        this.view.setController(this);
    }

    public void showResult() {
        // 5.1.5: Hiển thị màn hình kết quả.
        this.view.setVisible(true);
    }

    public void handleRestart() {
        // AF-02 / 5.3.3: Người chơi chọn Chơi lại — đóng cả 2 màn hình cũ rồi khởi tạo ván mới.
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
        // AF-02 / 5.3.4: Người chơi chọn Trang chủ — đóng màn hình kết quả và bàn cờ, quay về menu chính.
        view.dispose();
        boardView.dispose();
        new GameController();
    }
}
