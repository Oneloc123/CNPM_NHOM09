package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.BoardView;
import view.GameView;

public class GameController {
	public GameController() {
		GameView gameView = new G	ameView();

		gameView.getBtnCreate().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String difficulty = gameView.getCbxDifficulty().getSelectedItem().toString();
				gameView.dispose();
				new BoardView(difficulty).setVisible(true);
			}
		});

		gameView.getBtnCancel().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
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
