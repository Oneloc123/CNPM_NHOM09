package view;

import model.Board;
import aiService.AIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BoardView extends JFrame {
    private final Board boardModel;
    private final AIService aiModel;
    private JButton[][] buttons;
    private final JLabel lblStatus;
    private boolean gameOver = false;

    public BoardView(String difficulty) {
        this.boardModel = new Board();
        this.aiModel = new AIService(difficulty);

        setTitle("Cờ Caro 3x3 | Độ khó: " + difficulty);
        setSize(480, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblStatus = createStatusLabel();
        add(lblStatus, BorderLayout.NORTH);

        JPanel boardPanel = createBoardPanel();
        add(boardPanel, BorderLayout.CENTER);
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Lượt của: X", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        return label;
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE, 6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(45, 45, 45));

        buttons = new JButton[Board.SIZE][Board.SIZE];

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                JButton btn = createGameButton();
                final int row = i, col = j;
                btn.addActionListener(e -> handlePlayerMove(row, col));

                buttons[i][j] = btn;
                panel.add(btn);
            }
        }
        return panel;
    }

    private JButton createGameButton() {
        JButton btn = new JButton("");
        btn.setFont(new Font("Arial", Font.BOLD, 52));
        btn.setBackground(new Color(245, 245, 245));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        return btn;
    }

    private void handlePlayerMove(int row, int col) {
        if (gameOver || !boardModel.makeMove(row, col))
            return;

        updateBoardUI();

        int[][] winLine = boardModel.getWinLine(row, col);
        if (winLine != null) {
            SwingUtilities.invokeLater(() -> highlightWinningCells(winLine, true));
            endGame("🎉 X thắng!");
            return;
        }
        if (boardModel.isBoardFull()) {
            endGame("🤝 Hòa!");
            return;
        }

        setButtonsEnabled(false);
        lblStatus.setText("Máy đang suy nghĩ...");

        new Thread(this::aiThinking).start();
    }

    private void setButtonsEnabled(boolean enabled) {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                if (btn.getText().isEmpty()) {
                    btn.setEnabled(enabled);
                }
            }
        }
    }

    private void updateBoardUI() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                int cell = boardModel.getCell(i, j);
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
        lblStatus.setText("Lượt của: " + (boardModel.isXTurn() ? "X" : "O"));
    }
}