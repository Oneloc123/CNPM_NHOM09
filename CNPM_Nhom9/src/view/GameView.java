package view;

import javax.swing.*;
import java.awt.*;


public class GameView extends JFrame {
    private JComboBox<String> cbxBoardSize;
    private JComboBox<String> cbxdifficulty;
    private JButton btnCreate;
    private JButton btnCancel;

    public GameView() {
        // Thiết lập cơ bản cho JFrame
        setTitle("Cờ Caro - Tạo Ván Đấu");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề (Phía trên)
        JLabel lblTitle = new JLabel("CÀI ĐẶT VÁN ĐẤU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Khu vực nhập liệu (Ở giữa)
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Kích thước bàn cờ
        formPanel.add(new JLabel("Kích thước bàn cờ:"));
        String[] sizes = {"10 x 10", "15 x 15", "20 x 20", "25 x 25"};
        cbxBoardSize = new JComboBox<>(sizes);
        cbxBoardSize.setSelectedIndex(1); // Mặc định chọn 15x15
        formPanel.add(cbxBoardSize);

        // Độ khó
        formPanel.add(new JLabel("Độ khó:"));
        String[] difficulty = {"Dễ", "Khó"};
        cbxdifficulty = new JComboBox<>(difficulty);
        cbxdifficulty.setSelectedIndex(0); // Chỉnh mặc định về Dễ (index 0)
        formPanel.add(cbxdifficulty);
        add(formPanel, BorderLayout.CENTER);
        
        // 3. Khu vực nút bấm (Phía dưới)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        btnCreate = new JButton("Tạo Ván");
        btnCreate.setBackground(new Color(40, 167, 69));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        
        btnPanel.add(btnCreate);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

	public JComboBox<String> getCbxBoardSize() {
		return cbxBoardSize;
	}

	public void setCbxBoardSize(JComboBox<String> cbxBoardSize) {
		this.cbxBoardSize = cbxBoardSize;
	}

	public JComboBox<String> getCbxdifficulty() {
		return cbxdifficulty;
	}

	public void setCbxdifficulty(JComboBox<String> cbxdifficulty) {
		this.cbxdifficulty = cbxdifficulty;
	}

	public JButton getBtnCreate() {
		return btnCreate;
	}

	public void setBtnCreate(JButton btnCreate) {
		this.btnCreate = btnCreate;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}

	public void setBtnCancel(JButton btnCancel) {
		this.btnCancel = btnCancel;
	}
    
    
}