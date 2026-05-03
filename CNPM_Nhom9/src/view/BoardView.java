package view;

import model.Board;
import aiService.AIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardView extends JFrame {
    private Board boardModel;
    private AIService aiModel;
    private JButton[][] buttons;
    private JLabel lblStatus;
   

    public BoardView(int size, String difficulty) {
        // Khởi tạo bàn cờ
        this.boardModel = new Board(size);
        // Khởi tạo AI
        this.aiModel = new AIService(difficulty);

        setTitle("Cờ Caro | Độ khó: " + difficulty);
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblStatus = new JLabel("Lượt của: X", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblStatus, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(size, size));
        buttons = new JButton[size][size];
        // Vẽ bàn cờ
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                final int row = i;
                final int col = j;
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handlePlayerMove(row, col);
                    }
                });
                buttons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    // Hàm xử lý khi người chơi bấm vào 1 ô
    private void handlePlayerMove(int row, int col) {
        // 1. Cập nhật dữ liệu vào Model
        boolean isValidMove = boardModel.makeMove(row, col);
        if (isValidMove) {
            // 2. Cập nhật Giao diện (View)
            updateBoardUI();
            // 3. chơi với máy và tới lượt máy (O)    
                int[] aiMove = aiModel.getNextMove(boardModel);
                boardModel.makeMove(aiMove[0], aiMove[1]);
                updateBoardUI();    
        }
    }
    

    // Hàm để đồng bộ Giao diện theo dữ liệu của Model
    private void updateBoardUI() {
        int size = boardModel.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int cellValue = boardModel.getCell(i, j);
                if (cellValue == 1) {
                    buttons[i][j].setText("X");
                    buttons[i][j].setForeground(Color.RED);
                } else if (cellValue == 2) {
                    buttons[i][j].setText("O");
                    buttons[i][j].setForeground(Color.BLUE);
                }
            }
        }
        lblStatus.setText("Lượt của: " + (boardModel.isXTurn() ? "X" : "O"));
    }
}