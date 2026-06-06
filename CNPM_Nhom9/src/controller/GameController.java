package controller;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.BoardView;
import view.GameView;

public class GameController {
	GameView gameView;
	/*
	UC1.1.6.1: Hệ thống khởi tạo bàn cờ
	 */
	public void createBoardGame(){

		// Lấy độ khó người chơi đã chọn
		String difficulty = gameView.getCbxDifficulty()
				.getSelectedItem()
				.toString();

		// lấy tên người chơi
		String playername = "Anh Hung don doc";
		// lấy lượt đi
		boolean isPlayerFirst = true;

		// lấy kích thước bàn cờ
		int size = 5;

		// Đóng giao diện menu chính
		gameView.dispose();

		// Khởi tạo giao diện bàn cờ
		BoardView boardView = new BoardView(difficulty,playername,isPlayerFirst,size);

		// Khởi tạo controller quản lý bàn cờ
		BoardController boardCtrl = new BoardController(difficulty,playername,isPlayerFirst,size);

		// Gán view cho controller
		boardCtrl.setView(boardView);

		// Gán controller cho view
		boardView.setController(boardCtrl);

		// Hiển thị giao diện bàn cờ
		boardView.setVisible(true);
	}

	/*
	UC1.2.2: Hệ thống đóng ứng dụng sau khi người dùng ấn nút thoát
	 */
	public void cancelGame(){

		// Kết thúc toàn bộ chương trình
		System.exit(0);
	}
	/*
		UC1.1.1: Người chơi ấn vào file exe
	 */
	public GameController() {
		// Khởi tạo giao diện màn hình chính của game
		gameView = new GameView(this);
		try {
			// Thiết lập giao diện theo theme của hệ điều hành
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// In lỗi nếu không thiết lập được giao diện
			e.printStackTrace();
		}
		// Hiển thị giao diện trên luồng Swin
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Hiển thị cửa sổ game
				gameView.setVisible(true);
			}
		});
	}
}
