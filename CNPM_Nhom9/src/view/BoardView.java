package view;

import model.Board;
import aiService.AIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BoardView extends JFrame {
    
    private JButton[][] buttons;
    private JLabel lblStatus;

    private void onCellClicked(int row, int col) {
        if (gameOver)
            return;
        controller.handlePlayerMove(row, col);
    }

    public void updateBoard(Board board) {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                int cell = board.getCell(i, j);
                JButton btn = buttons[i][j];

                if (cell == 1) {
                    btn.setText("X");
                    btn.setForeground(new Color(220, 50, 50));
                } else if (cell == 2) {
                    btn.setText("O");
                    btn.setForeground(new Color(30, 100, 200));
                }
            }
        }
        lblStatus.setText("Lượt của: " + (board.isXTurn() ? "X" : "O"));
    }

    public void setInputEnabled(boolean enabled) {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                if (btn.getText().isEmpty()) {
                    btn.setEnabled(enabled);
                }
            }
        }
    }
}
