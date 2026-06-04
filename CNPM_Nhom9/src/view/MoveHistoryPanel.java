package view;

import model.MoveRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel hiển thị danh sách nước đi kèm điểm số.
 *
 */
public class MoveHistoryPanel extends JPanel {

    // ── Màu sắc chủ đạo ──────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(22, 22, 35);
    private static final Color BG_PANEL     = new Color(30, 30, 48);
    private static final Color BG_HEADER    = new Color(40, 40, 65);
    private static final Color BG_ROW_X     = new Color(60, 25, 25);
    private static final Color BG_ROW_O     = new Color(20, 40, 70);
    private static final Color BG_ROW_EVEN  = new Color(34, 34, 52);
    private static final Color COLOR_X      = new Color(255, 100, 100);
    private static final Color COLOR_O      = new Color(80, 170, 255);
    private static final Color COLOR_GOLD   = new Color(255, 210, 60);
    private static final Color COLOR_TEXT   = new Color(220, 220, 235);
    private static final Color COLOR_BORDER = new Color(60, 60, 90);

    // ── Dữ liệu ──────────────────────────────────────────────────────
    private final List<MoveRecord> records = new ArrayList<>();
    private final String nameX;
    private final String nameO;

    // ── Widgets ──────────────────────────────────────────────────────
    private final DefaultTableModel tableModel;
    private final JTable table;


    private static final String[] COLUMNS =
            { "#", "Người", "Toạ độ" };
    // ─────────────────────────────────────────────────────────────────
    public MoveHistoryPanel(String nameX, String nameO) {
        this.nameX = nameX;
        this.nameO = nameO;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(320, 0));   // chiều rộng cố định, cao co giãn
        // ── Header ────────────────────────────────────────────────
        add(buildHeader(), BorderLayout.NORTH);
        // ── Table ─────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBackground(BG_DARK);
        add(scroll, BorderLayout.CENTER);

    }
    // ── Public API ───────────────────────────────────────────────────
    /** Thêm một nước đi mới vào danh sách và cập nhật giao diện. */
    public void addMove(MoveRecord rec) {
        records.add(rec);
        tableModel.addRow(new Object[]{
                rec.getMoveNumber(),
                rec.getPlayerSymbol(),
                rec.getCoordDisplay()
        });
        // Cuộn xuống dòng mới nhất
        int last = tableModel.getRowCount() - 1;
        table.scrollRectToVisible(table.getCellRect(last, 0, true));

    }
    /** Xóa toàn bộ lịch sử (dùng khi chơi lại). */
    public void reset() {
        records.clear();
        tableModel.setRowCount(0);
    }
    // ── Builders ─────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_HEADER);
        p.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel("📋  LỊCH SỬ NƯỚC ĐI");
        title.setFont(loadFont(Font.BOLD, 14f));
        title.setForeground(COLOR_GOLD);
        p.add(title, BorderLayout.WEST);

        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDER);
        p.add(sep, BorderLayout.SOUTH);

        return p;
    }

    private JTable buildTable() {
        JTable t = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(70, 70, 110));
                } else {
                    // Tô màu theo người chơi
                    String sym = (String) getValueAt(row, 1);
                    if ("X".equals(sym))      c.setBackground(BG_ROW_X);
                    else if ("O".equals(sym)) c.setBackground(BG_ROW_O);
                    else                      c.setBackground(row % 2 == 0 ? BG_ROW_EVEN : BG_PANEL);
                }
                c.setForeground(COLOR_TEXT);
                return c;
            }
        };

        t.setFont(loadFont(Font.PLAIN, 13f));
        t.setRowHeight(26);
        t.setBackground(BG_DARK);
        t.setForeground(COLOR_TEXT);
        t.setGridColor(COLOR_BORDER);
        t.setShowGrid(true);
        t.setIntercellSpacing(new Dimension(1, 1));
        t.setSelectionBackground(new Color(70, 70, 120));
        t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true);

        // Header style
        JTableHeader header = t.getTableHeader();
        header.setBackground(BG_HEADER);
        header.setForeground(COLOR_GOLD);
        header.setFont(loadFont(Font.BOLD, 12f));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));
        header.setReorderingAllowed(false);

        // Căn giữa tất cả cột
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        // Cột "Hàng tạo ra" căn trái
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        // Cột X/O tô màu riêng
        TableCellRenderer playerRenderer = new DefaultTableCellRenderer() {
            {setHorizontalAlignment(SwingConstants.CENTER);}
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, r, c);
                String sym = val == null ? "" : val.toString();
                setForeground("X".equals(sym) ? COLOR_X : COLOR_O);
                setFont(loadFont(Font.BOLD, 16f));
                setBackground(getBackground()); // giữ màu nền từ prepareRenderer
                return this;
            }
        };
        return t;
    }

    private Font loadFont(int style, float size) {
        try {
            Font f = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/JetBrainsMono-Regular.ttf"));
            return f.deriveFont(style, size);
        } catch (Exception e) {
            return new Font("Monospaced", style, (int) size);
        }
    }
}