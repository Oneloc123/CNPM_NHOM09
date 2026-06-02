package view;

import controller.ResultController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ResultView extends JFrame {
    private JButton btnRestart;
    private JButton btnHome;
    private ResultController controller;

    public ResultView(String resultMessage, int moveCount) {
        setTitle("Kết Quả Trận Đấu");
        setSize(400, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main Panel (Dark Theme đồng bộ với GameView)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(24, 28, 36));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Nội dung kết quả (Center)
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("TRẬN ĐẤU KẾT THÚC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(160, 170, 180));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblResult = new JLabel(resultMessage);
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 28));
        // Đổi màu chữ động theo kết quả trận đấu
        if (resultMessage.contains("thắng")) {
            if (resultMessage.contains("Máy")) {
                lblResult.setForeground(new Color(225, 55, 75)); // Đỏ nếu máy thắng
            } else {
                lblResult.setForeground(new Color(0, 195, 255)); // Xanh Neon nếu người thắng
            }
        } else {
            lblResult.setForeground(Color.WHITE); // Trắng nếu hòa
        }
        lblResult.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMoves = new JLabel("Tổng số nước đi: " + moveCount);
        lblMoves.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMoves.setForeground(new Color(200, 200, 200));
        lblMoves.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(lblResult);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(lblMoves);

        // Nút bấm điều hướng (South)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        btnRestart = createButton("Chơi lại", new Color(0, 180, 95));
        btnHome = createButton("Trang chủ", new Color(70, 80, 95));

        buttonPanel.add(btnRestart);
        buttonPanel.add(btnHome);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public void setController(ResultController controller) {
        this.controller = controller;
        btnRestart.addActionListener(e -> controller.handleRestart());
        btnHome.addActionListener(e -> controller.handleGoHome());
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));
        return btn;
    }
}
