package controller;

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

    public ResultController(String resultMessage, int moveCount, String difficulty, 
                            String playerName, boolean isPlayerFirst, int size, BoardView boardView) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.isPlayerFirst = isPlayerFirst;
        this.size = size;
        this.boardView = boardView;

        this.view = new ResultView(resultMessage, moveCount);
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
        BoardView newView = new BoardView(difficulty, playerName, isPlayerFirst, size);
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
