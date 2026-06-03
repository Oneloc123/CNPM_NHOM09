package view;

import controller.BoardController;
import model.Board;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JFrame {
    private BoardController controller;
    private JButton[][] buttons;
    private JLabel lblStatus;
    private boolean gameOver = false;
    
    private String playerName;
    private boolean isPlayerFirst;
    private String xPlayerName;
    private String oPlayerName;
    private int size; // Lưu kích thước hiển thị bàn cờ

    /*
    UC1.1.6.2: Hệ thống khởi tạo giao diện bàn cờ tự động co giãn lưới ô
     */
    public BoardView(String difficulty, String playerName, boolean isPlayerFirst, int size) {
        this.playerName = playerName;
        this.isPlayerFirst = isPlayerFirst;
        this.size = size;
        
        this.xPlayerName = isPlayerFirst ? playerName : "Máy";
        this.oPlayerName = isPlayerFirst ? "Máy" : playerName;

        // Cập nhật tiêu đề cửa sổ chứa kích cỡ bàn cờ thực tế
        setTitle("Cờ Caro " + size + "x" + size + " | Người chơi: " + playerName + " | Độ khó: " + difficulty);

        // Thay đổi size cửa sổ Windows cho tương thích diện tích lưới nút bấm
        if (size == 3) {
            setSize(480, 550);
        } else {
            setSize(620, 700); 
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblStatus = createStatusLabel();
        add(lblStatus, BorderLayout.NORTH);

        JPanel boardPanel = createBoardPanel();
        add(boardPanel, BorderLayout.CENTER);
    }

    public void setController(BoardController controller) {
        this.controller = controller;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Lượt của: " + xPlayerName, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        return label;
    }

    private JPanel createBoardPanel() {
        // Sinh lưới Layout tùy biến theo size (3x3 hoặc 5x5)
        JPanel panel = new JPanel(new GridLayout(size, size, 6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(45, 45, 45));

        buttons = new JButton[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton btn = createGameButton();
                final int row = i, col = j;

                btn.addActionListener(e -> onCellClicked(row, col));

                buttons[i][j] = btn;
                panel.add(btn);
            }
        }
        return panel;
    }

    private JButton createGameButton() {
        JButton btn = new JButton("");
        btn.setFont(new Font("Arial", Font.BOLD, size == 3 ? 52 : 36));
        btn.setBackground(new Color(245, 245, 245));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        return btn;
    }

    private void onCellClicked(int row, int col) {
        if (gameOver)
            return;
        controller.handlePlayerMove(row, col);
    }

    public void updateBoard(Board board) {
        // ĐÃ SỬA: Bỏ maxLogicSize cũ, chạy hết kích thước thực tế của bàn cờ
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton btn = buttons[i][j];
                
                int cell = board.getCell(i, j);
                if (cell == 1) {
                    btn.setText("X");
                    btn.setForeground(new Color(220, 50, 50));
                } else if (cell == 2) {
                    btn.setText("O");
                    btn.setForeground(new Color(30, 100, 200));
                } else {
                    btn.setText(""); // Đảm bảo ô trống khi restart
                }
            }
        }
        lblStatus.setText("Lượt của: " + (board.isXTurn() ? xPlayerName : oPlayerName));
    }

    public void setStatus(String text) {
        lblStatus.setText(text);
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

    public void highlightWin(int[][] winLine, boolean isPlayer) {
        if (winLine == null) return;

        Color bgColor = isPlayer
                ? new Color(220, 70, 70)
                : new Color(60, 130, 190);

        for (int[] pos : winLine) {
            JButton btn = buttons[pos[0]][pos[1]];
            btn.setBackground(bgColor);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4));
        }
    }

    public void showEndGame(String message) {
        gameOver = true;
        lblStatus.setText(message);

        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                if (btn.getText().isEmpty()) {
                    btn.setEnabled(false);
                }
            }
        }

        Object[] options = {"Chơi lại", "Trang chủ"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message + "\nBạn muốn làm gì tiếp theo?",
                "Kết thúc ván đấu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            controller.handleRestart();
        } else {
            controller.handleGoHome();
        }
    }
}