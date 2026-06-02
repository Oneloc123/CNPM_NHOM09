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
	UC1.1.2.1: Hệ thống khởi tạo khung chứa giao diện
	 */
	public GameView(GameController gameController) {

		// Thiết lập controller cho giao diện
		setGameController(gameController);

		// Khởi tạo các thành phần giao diện
		initUI();

		// Gắn các sự kiện cho nút bấm
		setListener();
	}

	/*
	UC1.1.2.2: Hệ thống thiết lập gameController cho giao diện
	 */
	public void setGameController(GameController gameController) {

		// Gán controller để giao diện có thể gọi xử lý nghiệp vụ
		this.gameController = gameController;
	}

	/*
	UC1.1.2.3: Hệ thống khởi tạo giao diện
	 */
	private void initUI() {

		// Thiết lập tiêu đề cửa sổ
		setTitle("CỜ CARO AI");

		// Thiết lập kích thước cửa sổ
		setSize(500, 320);

		// Hiển thị cửa sổ ở giữa màn hình
		setLocationRelativeTo(null);

		// Đóng hoàn toàn chương trình khi tắt cửa sổ
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Không cho phép thay đổi kích thước cửa sổ
		setResizable(false);

		// ================= MAIN PANEL =================

		// Tạo panel chính chứa toàn bộ giao diện
		JPanel mainPanel = new JPanel();

		// Sử dụng BorderLayout để chia giao diện
		mainPanel.setLayout(new BorderLayout());

		// Thiết lập màu nền
		mainPanel.setBackground(new Color(20, 24, 28));

		// Thiết lập khoảng cách viền
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// ================= HEADER =================

		// Tạo panel tiêu đề
		JPanel headerPanel = new JPanel();

		// Cho phép nền trong suốt
		headerPanel.setOpaque(false);

		// Sắp xếp thành phần theo chiều dọc
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		// Tạo label tiêu đề game
		JLabel lblTitle = new JLabel("CỜ CARO");

		// Căn giữa tiêu đề
		lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Thiết lập font chữ
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));

		// Thiết lập màu chữ
		lblTitle.setForeground(new Color(0, 200, 255));

		// Tạo label mô tả phụ
		JLabel lblSub = new JLabel("Thi đấu với AI thông minh");

		// Căn giữa label phụ
		lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Thiết lập font chữ
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));

		// Thiết lập màu chữ
		lblSub.setForeground(new Color(180, 180, 180));

		// Thêm tiêu đề vào header
		headerPanel.add(lblTitle);

		// Tạo khoảng cách giữa các thành phần
		headerPanel.add(Box.createVerticalStrut(10));

		// Thêm mô tả phụ
		headerPanel.add(lblSub);

		// ================= CENTER =================

		// Tạo panel trung tâm
		JPanel centerPanel = new JPanel();

		// Thiết lập nền trong suốt
		centerPanel.setOpaque(false);

		// Thiết lập khoảng cách viền
		centerPanel.setBorder(new EmptyBorder(35, 40, 35, 40));

		// Sử dụng GridBagLayout để bố trí linh hoạt
		centerPanel.setLayout(new GridBagLayout());

		// Khởi tạo ràng buộc bố cục
		GridBagConstraints gbc = new GridBagConstraints();

		// Thiết lập khoảng cách giữa các component
		gbc.insets = new Insets(10, 10, 10, 10);

		// Cho component giãn theo chiều ngang
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Label chọn độ khó
		JLabel lblDifficulty = new JLabel("Độ khó:");

		// Thiết lập màu chữ
		lblDifficulty.setForeground(Color.WHITE);

		// Thiết lập font chữ
		lblDifficulty.setFont(new Font("Segoe UI", Font.BOLD, 18));

		// Tạo combobox chọn độ khó
		cbxDifficulty = new JComboBox<>(new String[]{"Dễ", "Khó"});

		// Thiết lập font chữ
		cbxDifficulty.setFont(new Font("Segoe UI", Font.PLAIN, 16));

		// Thiết lập màu nền
		cbxDifficulty.setBackground(new Color(45, 45, 45));

		// Thiết lập màu chữ
		cbxDifficulty.setForeground(Color.WHITE);

		// Bỏ viền focus
		cbxDifficulty.setFocusable(false);

		// Thiết lập kích thước combobox
		cbxDifficulty.setPreferredSize(new Dimension(180, 40));

		// Thiết lập vị trí label
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Thêm label vào panel
		centerPanel.add(lblDifficulty, gbc);

		// Thiết lập vị trí combobox
		gbc.gridx = 1;

		// Thêm combobox vào panel
		centerPanel.add(cbxDifficulty, gbc);

		// ================= FOOTER =================

		// Tạo panel chứa nút chức năng
		JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

		// Thiết lập nền trong suốt
		footerPanel.setOpaque(false);

		// Tạo nút bắt đầu game
		btnCreate = createButton("Bắt Đầu", new Color(0, 170, 90));

		// Tạo nút thoát game
		btnCancel = createButton("Thoát", new Color(220, 53, 69));

		// Thêm nút bắt đầu vào footer
		footerPanel.add(btnCreate);

		// Thêm nút thoát vào footer
		footerPanel.add(btnCancel);

		// Thêm header vào giao diện chính
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		// Thêm phần giữa vào giao diện chính
		mainPanel.add(centerPanel, BorderLayout.CENTER);

		// Thêm footer vào giao diện chính
		mainPanel.add(footerPanel, BorderLayout.SOUTH);

		// Thiết lập panel chính cho cửa sổ
		setContentPane(mainPanel);
	}
	private JButton createButton(String text, Color color) {

		// Khởi tạo button với nội dung truyền vào
		JButton button = new JButton(text);

		// Thiết lập font chữ cho button
		button.setFont(new Font("Segoe UI", Font.BOLD, 16));

		// Thiết lập màu chữ
		button.setForeground(Color.WHITE);

		// Thiết lập màu nền
		button.setBackground(color);

		// Tắt hiệu ứng focus
		button.setFocusPainted(false);

		// Ẩn viền button
		button.setBorderPainted(false);

		// Hiển thị nền button
		button.setOpaque(true);

		// Đổi con trỏ chuột thành hình bàn tay khi hover
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// Thiết lập kích thước button
		button.setPreferredSize(new Dimension(140, 45));

		// Trả về button đã tạo
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
	UC1.1.2.4: Hệ thống thiết lập sự kiện nút ấn
	 */
	public void setListener() {

		// Bắt sự kiện khi người chơi nhấn nút "Bắt Đầu"
		btnCreate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Gọi controller để khởi tạo bàn cờ
				gameController.createBoardGame();
			}
		});

		// Bắt sự kiện khi người chơi nhấn nút "Thoát"
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Gọi controller để thoát game
				gameController.cancelGame();
			}
		});
	}

}

