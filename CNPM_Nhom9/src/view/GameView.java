package view;

import controller.GameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView extends JFrame {
	private JComboBox<String> cbxDifficulty;
	private JButton btnCreate;
	private JButton btnCancel;
	private GameController gameController;

	/*
	UC1.1.2: Hệ thống khởi tạo khung chứa giao diện
	 */
	public GameView(GameController gameController) {
		setGameController(gameController);
		initUI();
		setListener();
	}
	/*
	UC1.1.3: Hệ thống thiết lập gameController cho giao diện
	 */
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}
	/*
	UC1.1.4: Hệ thống khởi tạo giao diện
	 */
	private void initUI() {
		setTitle("CỜ CARO AI");
		setSize(500, 320);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// Main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(new Color(20, 24, 28));
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// HEADER
		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		JLabel lblTitle = new JLabel("CỜ CARO");
		lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
		lblTitle.setForeground(new Color(0, 200, 255));

		JLabel lblSub = new JLabel("Thi đấu với AI thông minh");
		lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblSub.setForeground(new Color(180, 180, 180));

		headerPanel.add(lblTitle);
		headerPanel.add(Box.createVerticalStrut(10));
		headerPanel.add(lblSub);

		//  CENTER
		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		centerPanel.setBorder(new EmptyBorder(35, 40, 35, 40));
		centerPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblDifficulty = new JLabel("Độ khó:");
		lblDifficulty.setForeground(Color.WHITE);
		lblDifficulty.setFont(new Font("Segoe UI", Font.BOLD, 18));

		cbxDifficulty = new JComboBox<>(new String[]{"Dễ", "Khó"});
		cbxDifficulty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		cbxDifficulty.setBackground(new Color(45, 45, 45));
		cbxDifficulty.setForeground(Color.WHITE);
		cbxDifficulty.setFocusable(false);
		cbxDifficulty.setPreferredSize(new Dimension(180, 40));

		gbc.gridx = 0;
		gbc.gridy = 0;
		centerPanel.add(lblDifficulty, gbc);

		gbc.gridx = 1;
		centerPanel.add(cbxDifficulty, gbc);

		//  FOOTER
		JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		footerPanel.setOpaque(false);

		btnCreate = createButton("Bắt Đầu", new Color(0, 170, 90));
		btnCancel = createButton("Thoát", new Color(220, 53, 69));

		footerPanel.add(btnCreate);
		footerPanel.add(btnCancel);

		mainPanel.add(headerPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(footerPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
	}

	private JButton createButton(String text, Color color) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 16));
		button.setForeground(Color.WHITE);
		button.setBackground(color);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setOpaque(true);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(140, 45));
		return button;
	}

	public JComboBox<String> getCbxDifficulty() {
		return cbxDifficulty;
	}

	public JButton getBtnCreate() {
		return btnCreate;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}


	/*
	UC1.1.5: Hệ thống thiết lập sự kiện nút ấn
	 */
	public void setListener() {

		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameController.createBoardGame();
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameController.cancelGame();
			}
		});
	}
}

