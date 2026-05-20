package controller;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.BoardView;
import view.GameView;

public class GameController {
	GameView gameView;
	/*
	UC1.1.9: Hệ thống khởi tạo bàn cờ
	 */
	public void createBoardGame(){
		String difficulty = gameView.getCbxDifficulty().getSelectedItem().toString();
		gameView.dispose();
		BoardView boardView = new BoardView(difficulty);
		BoardController boardCtrl = new BoardController(difficulty);
		boardCtrl.setView(boardView);
		boardView.setController(boardCtrl);
		boardView.setVisible(true);	
	}
	/*
	UC1.2.2: Hệ thống đóng ứng dụng sau khi người dùng ấn nút thoát
	 */
	public void cancelGame(){
		System.exit(0);
	}

	/*
		UC1.1.1: Người chơi ấn vào file exe
	 */
	public GameController() {
		gameView = new GameView(this);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gameView.setVisible(true);
			}
		});
	}
}
