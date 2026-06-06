package view;

import controller.ResultController;
import service.LeaderboardService;
import service.LeaderboardService.Entry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


/**
 * ============================================================
 *  UC-005: ResultView — Màn hình kết quả + Bảng Xếp Hạng
 * ============================================================
 *
 *  Giao diện này hiển thị:
 *    1. Tiêu đề "TRẬN ĐẤU KẾT THÚC"
 *    2. Kết quả ván đấu (thắng / thua / hòa) — màu sắc động
 *    3. Tổng số nước đi
 *    4. Nút mở bảng xếp hạng Top 5 người chơi
 *    5. Các nút "Chơi lại" và "Trang chủ"
 *
 *  Dữ liệu bảng xếp hạng (topEntries) được truyền vào từ
 *  ResultController để tránh gọi I/O từ View (tách biệt MVC).
 * ============================================================
 */
public class ResultView extends JFrame {
    private JButton btnRestart;
    private JButton btnHome;
    private JButton btnLeaderboard;
    private ResultController controller;
    private final List<Entry> topEntries;

    // Màu sắc với phong cách dark theme cho BXH
    private static final Color COLOR_BG      = new Color(24, 28, 36);
    private static final Color COLOR_ACCENT  = new Color(255, 200, 60);
    private static final Color COLOR_SUBTEXT  = new Color(160, 170, 180);
    private static final Color COLOR_ROW_ODD  = new Color(32, 37, 48);    // Màu hàng lẻ bảng XH
    private static final Color COLOR_ROW_EVEN = new Color(40, 46, 58);    // Màu hàng chẵn bảng XH
    private static final Color COLOR_HEADER   = new Color(18, 22, 32);    // Màu header bảng XH

    public ResultView(String resultMessage, int moveCount, List<Entry> topEntries) {

        this.topEntries = topEntries;
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
        btnLeaderboard = createButton("🏆 BXH",    new Color(255, 200, 60));
        btnHome = createButton("Trang chủ", new Color(70, 80, 95));

        buttonPanel.add(btnRestart);
        buttonPanel.add(btnLeaderboard);
        buttonPanel.add(btnHome);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public void setController(ResultController controller) {
        this.controller = controller;
        btnRestart.addActionListener(e -> controller.handleRestart());
        btnHome.addActionListener(e -> controller.handleGoHome());
        btnLeaderboard.addActionListener(e -> showLeaderboardDialog());
    }

    // ─── Phương thức tạo UI nội bộ ─────────────────────────────

    /**
     * 5.1.3: Hiển thị kết quả, số nước đi và màu chữ theo trạng thái thắng/thua/hòa.
     */
    private JPanel buildResultPanel(String resultMessage, int moveCount) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("TRẬN ĐẤU KẾT THÚC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(COLOR_SUBTEXT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblResult = new JLabel(resultMessage);
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblResult.setForeground(resolveResultColor(resultMessage));
        lblResult.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMoves = new JLabel("Tổng số nước đi: " + moveCount);
        lblMoves.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMoves.setForeground(new Color(200, 200, 200));
        lblMoves.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblResult);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblMoves);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    /**
     * 5.1.5, AF-4: Dựng nội dung bảng Top 5 hoặc thông báo chưa có dữ liệu.
     */
    private JPanel buildLeaderboardPanel(List<Entry> topEntries) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JLabel lblLbTitle = new JLabel("🏆  BẢNG XẾP HẠNG TOP " + LeaderboardService.TOP_N);
        lblLbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLbTitle.setForeground(COLOR_ACCENT);
        lblLbTitle.setBorder(new EmptyBorder(0, 2, 4, 0));

        wrapper.add(lblLbTitle, BorderLayout.NORTH);

        if (topEntries.isEmpty()) {
            // AF-4 / 5.5.2: Không dựng bảng 7 cột khi danh sách rỗng.
            JLabel lblEmpty = new JLabel("Chưa có dữ liệu xếp hạng.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEmpty.setForeground(COLOR_SUBTEXT);
            lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
            wrapper.add(lblEmpty, BorderLayout.CENTER);
            return wrapper;
        }

        // 5.1.5: Bảng gồm 7 cột: Hạng, Tên, Độ khó, Bàn, Thắng, Thua, Hòa.
        JPanel table = new JPanel(new GridLayout(topEntries.size() + 1, 7, 1, 1));
        table.setBackground(new Color(10, 12, 18)); // Màu viền giữa các ô

        String[] headers = { "#", "Tên", "Độ khó", "Bàn", "Thắng", "Thua", "Hòa" };
        for (String h : headers) {
            table.add(makeCell(h, COLOR_HEADER, COLOR_ACCENT, Font.BOLD, 11));
        }

        for (int i = 0; i < topEntries.size(); i++) {
            Entry e         = topEntries.get(i);
            Color rowBg     = (i % 2 == 0) ? COLOR_ROW_EVEN : COLOR_ROW_ODD;
            String rank     = (i == 0) ? "🥇" : (i == 1) ? "🥈" : (i == 2) ? "🥉" : String.valueOf(i + 1);

            table.add(makeCell(rank,              rowBg, COLOR_ACCENT,             Font.PLAIN, 12));
            table.add(makeCell(e.playerName,      rowBg, Color.WHITE,              Font.BOLD,  12));
            table.add(makeCell(e.difficulty,      rowBg, COLOR_SUBTEXT,            Font.PLAIN, 11));
            table.add(makeCell(e.boardSize,       rowBg, COLOR_SUBTEXT,            Font.PLAIN, 11));
            table.add(makeCell(String.valueOf(e.wins),   rowBg, new Color(0, 200, 100),  Font.BOLD, 12));
            table.add(makeCell(String.valueOf(e.losses), rowBg, new Color(220, 80, 80),  Font.PLAIN, 12));
            table.add(makeCell(String.valueOf(e.draws),  rowBg, new Color(180, 180, 180),Font.PLAIN, 12));
        }

        wrapper.add(table, BorderLayout.CENTER);
        return wrapper;
    }

    private Color resolveResultColor(String resultMessage) {
        if (resultMessage.contains("thắng")) {
            return resultMessage.contains("Máy")
                    ? new Color(225, 55, 75)
                    : new Color(0, 195, 255);
        }
        return Color.WHITE;
    }
    
    /**
     * 5.1.3: Tạo bộ ba nút Chơi lại, BXH và Trang chủ.
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        panel.setOpaque(false);

        btnRestart     = createButton("Chơi lại",       new Color(0, 180, 95));
        btnLeaderboard = createButton("🏆 BXH",         COLOR_ACCENT);
        btnHome        = createButton("Trang chủ",      new Color(70, 80, 95));

        panel.add(btnRestart);
        panel.add(btnLeaderboard);
        panel.add(btnHome);
        return panel;
    }

    // ─── Phương thức tiện ích ──────────────────────────────────

    /** 5.1.5: Tạo một ô trong bảng xếp hạng. */
    private JLabel makeCell(String text, Color bg, Color fg, int fontStyle, int fontSize) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", fontStyle, fontSize));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(4, 4, 4, 4));
        return lbl;
    }

    /** 5.1.5 - 5.1.6: Mở JDialog bảng xếp hạng và cho phép đóng để quay lại kết quả. */
    private void showLeaderboardDialog() {
        JDialog dialog = new JDialog(this, "Bảng Xếp Hạng", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(520, 330);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(COLOR_BG);
        content.setBorder(new EmptyBorder(18, 18, 18, 18));
        content.add(buildLeaderboardPanel(topEntries), BorderLayout.CENTER);

        dialog.setContentPane(content);
        dialog.setVisible(true);
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
