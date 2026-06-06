package view;

import model.MoveRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel hiển thị lịch sử nước đi kèm điểm số – Giao diện tối giản cho game cờ caro.
 */
public class MoveHistoryPanel extends JPanel {

    // ── Màu sắc tối giản, trung tính ─────────────────────────────────
    private static final Color BG_MAIN       = new Color(250, 250, 252);
    private static final Color BG_HEADER     = new Color(240, 240, 245);
    private static final Color BG_ROW_EVEN   = new Color(255, 255, 255);
    private static final Color BG_ROW_ODD    = new Color(248, 248, 250);
    private static final Color COLOR_TEXT    = new Color(30, 30, 35);
    private static final Color COLOR_DIM     = new Color(120, 120, 130);
    private static final Color COLOR_BORDER  = new Color(210, 210, 220);
    private static final Color COLOR_X       = new Color(180, 60, 60);   // đỏ nhẹ cho X
    private static final Color COLOR_O       = new Color(40, 100, 180);  // xanh nhẹ cho O
    private static final Color COLOR_GOLD    = new Color(200, 140, 40);  // vàng nhẹ cho điểm cao

    // ── Dữ liệu ──────────────────────────────────────────────────────
    private final List<MoveRecord> records = new ArrayList<>();
    private final String nameX;
    private final String nameO;

    // ── Widgets ──────────────────────────────────────────────────────
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel lblScoreX;
    private final JLabel lblScoreO;

    private static final String[] COLUMNS =
            { "#", "Người", "Toạ độ", "Điểm", "Hàng tạo ra" };

    // ─────────────────────────────────────────────────────────────────
    public MoveHistoryPanel(String nameX, String nameO) {
        this.nameX = nameX;
        this.nameO = nameO;

        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        setPreferredSize(new Dimension(340, 0));

        add(buildHeader(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_MAIN);
        add(scroll, BorderLayout.CENTER);

        JPanel footer = buildFooter();
        lblScoreX = (JLabel) ((JPanel) footer.getComponent(0)).getComponent(1);
        lblScoreO = (JLabel) ((JPanel) footer.getComponent(1)).getComponent(1);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Public API ───────────────────────────────────────────────────
    public void addMove(MoveRecord rec) {
        records.add(rec);
        tableModel.addRow(new Object[]{
                rec.getMoveNumber(),
                rec.getPlayerSymbol(),
                rec.getCoordDisplay(),
                rec.getMoveScore(),
                rec.getDescription()
        });
        int last = tableModel.getRowCount() - 1;
        table.scrollRectToVisible(table.getCellRect(last, 0, true));
        lblScoreX.setText(String.valueOf(rec.getTotalScoreX()));
        lblScoreO.setText(String.valueOf(rec.getTotalScoreO()));
    }

    public void reset() {
        records.clear();
        tableModel.setRowCount(0);
        lblScoreX.setText("0");
        lblScoreO.setText("0");
    }

    // ── Builders ─────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_HEADER);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel title = new JLabel("Lịch sử nước đi");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(COLOR_TEXT);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JTable buildTable() {
        JTable t = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(220, 220, 240));
                } else {
                    c.setBackground(row % 2 == 0 ? BG_ROW_EVEN : BG_ROW_ODD);
                }
                c.setForeground(COLOR_TEXT);
                return c;
            }
        };

        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.setRowHeight(26);
        t.setBackground(BG_MAIN);
        t.setForeground(COLOR_TEXT);
        t.setGridColor(COLOR_BORDER);
        t.setShowGrid(true);
        t.setIntercellSpacing(new Dimension(1, 1));
        t.setSelectionBackground(new Color(210, 210, 230));
        t.setSelectionForeground(COLOR_TEXT);
        t.setFillsViewportHeight(true);

        // Header style
        JTableHeader header = t.getTableHeader();
        header.setBackground(BG_HEADER);
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        header.setReorderingAllowed(false);

        // Căn giữa các cột trừ cột cuối
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        // Renderer cho cột Người chơi (X/O)
        TableCellRenderer playerRenderer = new DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.CENTER); }
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, r, c);
                String sym = val == null ? "" : val.toString();
                setForeground("X".equals(sym) ? COLOR_X : COLOR_O);
                setFont(new Font("SansSerif", Font.BOLD, 14));
                return this;
            }
        };

        // Renderer cho cột Điểm (tô màu nhẹ nếu cao)
        TableCellRenderer scoreRenderer = new DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.CENTER); }
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, r, c);
                int score = val == null ? 0 : (int) val;
                setForeground(score >= 50 ? COLOR_GOLD : COLOR_DIM);
                setFont(new Font("SansSerif", score >= 50 ? Font.BOLD : Font.PLAIN, 12));
                return this;
            }
        };

        // Độ rộng cột
        int[] colWidths = {35, 55, 70, 50, 150};
        for (int i = 0; i < COLUMNS.length; i++) {
            TableColumn col = t.getColumnModel().getColumn(i);
            col.setPreferredWidth(colWidths[i]);
            if (i == 0 || i == 2 || i == 3) col.setCellRenderer(center);
            else if (i == 1) col.setCellRenderer(playerRenderer);
            else col.setCellRenderer(left);
        }

        return t;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 0));
        footer.setBackground(BG_HEADER);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER),
                new EmptyBorder(6, 12, 8, 12)
        ));
        footer.add(scoreRow(nameX + " (X):", "0", COLOR_X));
        footer.add(scoreRow(nameO + " (O):", "0", COLOR_O));
        return footer;
    }

    private JPanel scoreRow(String label, String initialVal, Color valColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG_HEADER);
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(COLOR_DIM);
        JLabel val = new JLabel(initialVal);
        val.setFont(new Font("SansSerif", Font.BOLD, 15));
        val.setForeground(valColor);
        val.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }
}