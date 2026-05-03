package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.BoardView;
import view.GameView;

public class GameController {
	GameView gameView;
	BoardView boardView;
	public GameController() {
		gameView = new GameView();
		gameView.getBtnCreate().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int sizeIndex = gameView.getCbxBoardSize().getSelectedIndex();
				String sizeString = gameView.getCbxBoardSize().getSelectedItem().toString();
				String difficulty = gameView.getCbxdifficulty().getSelectedItem().toString();
		
				gameView.dispose();
				
				int size = 1;
				switch (sizeIndex) {
				case 0: {
					size = 10;
				}
				case 1: {
					size = 15;
				}
				case 2: {
					size = 20;
				}
				case 3: {
					size = 25;
				}
				default:
					size = 10;
				}
				
				boardView = new BoardView(size, difficulty);
				boardView.setVisible(true);
			}
		});
		
		
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
