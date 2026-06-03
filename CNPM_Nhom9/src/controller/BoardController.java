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

    public void handlePlayerMove(int row, int col) {
        int player = board.makeMove(row, col);
        if (player == 0)
            return;
        view.updateBoard(board);
        int[][] winLine = board.getWinLine(row, col, player);
        if (winLine != null) {
            view.highlightWin(winLine, true);
            
            // Người chơi click chuột thắng -> Thông báo Người chơi thắng
            SwingUtilities.invokeLater(() -> view.showEndGame(playerName + " thắng!"));
            return;
        }

        if (board.isBoardFull()) {
            SwingUtilities.invokeLater(() -> view.showEndGame("Hòa!"));
            return;
        }

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