package view;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
	private JComboBox<String> cbxDifficulty;
	private JButton btnCreate;
	private JButton btnCancel;

	public GameView() {
		setTitle("Cờ Caro - Tạo Ván Đấu");
		setSize(380, 220);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		// Tiêu đề
		JLabel lblTitle = new JLabel("CÀI ĐẶT VÁN ĐẤU", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
		lblTitle.setForeground(new Color(0, 102, 204));
		lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
		add(lblTitle, BorderLayout.NORTH);

		// Form chọn độ khó
		JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

		formPanel.add(new JLabel("Độ khó:"));
		cbxDifficulty = new JComboBox<>(new String[] { "Dễ", "Khó" });
		cbxDifficulty.setSelectedIndex(0);
		formPanel.add(cbxDifficulty);

		add(formPanel, BorderLayout.CENTER);

		// Nút bấm
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

		btnCreate = new JButton("Bắt Đầu");
		btnCreate.setBackground(new Color(40, 167, 69));
		btnCreate.setForeground(Color.WHITE);
		btnCreate.setFocusPainted(false);
		btnCreate.setOpaque(true);
		btnCreate.setBorderPainted(false);

		btnCancel = new JButton("Hủy");
		btnCancel.setBackground(new Color(220, 53, 69));
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setFocusPainted(false);
		btnCancel.setOpaque(true);
		btnCancel.setBorderPainted(false);

		btnPanel.add(btnCreate);
		btnPanel.add(btnCancel);
		add(btnPanel, BorderLayout.SOUTH);
	}

	public JComboBox<String> getCbxDifficulty() {
		return cbxDifficulty;
	}

	public void setCbxDifficulty(JComboBox<String> cbxDifficulty) {
		this.cbxDifficulty = cbxDifficulty;
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