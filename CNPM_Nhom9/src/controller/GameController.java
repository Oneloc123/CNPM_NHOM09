package controller;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.BoardView;
import view.GameView;

public class GameController {

	public void createBoardGame(GameView gameView ){
		String difficulty = gameView.getCbxDifficulty().getSelectedItem().toString();
		gameView.dispose();

		BoardView boardView = new BoardView(difficulty);
		BoardController boardCtrl = new BoardController(difficulty);
		boardCtrl.setView(boardView);
		boardView.setController(boardCtrl);
		boardView.setVisible(true);
	}
	public void cancelGame(GameView gameView){
		System.exit(0);
	}

	public GameController() {
		GameView gameView = new GameView(this);

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
