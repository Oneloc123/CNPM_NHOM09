package test;

import model.MoveRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.MoveHistoryPanel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử cho MoveHistoryPanel (giao diện tối giản, vẫn giữ điểm số)
 * dựa trên lớp MoveRecord thực tế.
 */
class MoveHistoryPanelTest {

    private MoveHistoryPanel panel;
    private TableModel tableModel;
    private JLabel lblScoreX;
    private JLabel lblScoreO;

    @BeforeEach
    void setUp() throws Exception {
        panel = new MoveHistoryPanel("Người chơi 1", "Người chơi 2");

        Field tableModelField = MoveHistoryPanel.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        tableModel = (TableModel) tableModelField.get(panel);

        Field lblScoreXField = MoveHistoryPanel.class.getDeclaredField("lblScoreX");
        lblScoreXField.setAccessible(true);
        lblScoreX = (JLabel) lblScoreXField.get(panel);

        Field lblScoreOField = MoveHistoryPanel.class.getDeclaredField("lblScoreO");
        lblScoreOField.setAccessible(true);
        lblScoreO = (JLabel) lblScoreOField.get(panel);
    }

    // Helper: tạo MoveRecord với row, col (0-index)
    private MoveRecord createMove(int moveNumber, int player, int row, int col,
                                  int moveScore, int totalX, int totalO, String desc) {
        return new MoveRecord(moveNumber, player, row, col, moveScore, totalX, totalO, desc);
    }

    @Test
    void testAddMove_updatesTableAndScores() {
        // Nước 1: X đánh ô (0,0) -> hiển thị (1,1)
        MoveRecord rec1 = createMove(1, 1, 0, 0, 10, 10, 0, "Tạo hàng 3");
        panel.addMove(rec1);

        assertEquals(1, tableModel.getRowCount());
        assertEquals(1, tableModel.getValueAt(0, 0));      // moveNumber
        assertEquals("X", tableModel.getValueAt(0, 1));    // playerSymbol
        assertEquals("(1, 1)", tableModel.getValueAt(0, 2)); // coordDisplay
        assertEquals(10, tableModel.getValueAt(0, 3));     // moveScore
        assertEquals("Tạo hàng 3", tableModel.getValueAt(0, 4)); // description

        assertEquals("10", lblScoreX.getText());
        assertEquals("0", lblScoreO.getText());

        // Nước 2: O đánh ô (1,1) -> hiển thị (2,2)
        MoveRecord rec2 = createMove(2, 2, 1, 1, 5, 10, 5, "Chặn đường");
        panel.addMove(rec2);

        assertEquals(2, tableModel.getRowCount());
        assertEquals("O", tableModel.getValueAt(1, 1));
        assertEquals("(2, 2)", tableModel.getValueAt(1, 2));
        assertEquals(5, tableModel.getValueAt(1, 3));
        assertEquals("Chặn đường", tableModel.getValueAt(1, 4));
        assertEquals("10", lblScoreX.getText());
        assertEquals("5", lblScoreO.getText());
    }

    @Test
    void testReset_clearsTableAndScores() {
        panel.addMove(createMove(1, 1, 0, 0, 15, 15, 0, "Mở đầu"));
        panel.addMove(createMove(2, 2, 1, 2, 7, 15, 7, "Phản công"));

        assertEquals(2, tableModel.getRowCount());
        assertEquals("15", lblScoreX.getText());
        assertEquals("7", lblScoreO.getText());

        panel.reset();

        assertEquals(0, tableModel.getRowCount());
        assertEquals("0", lblScoreX.getText());
        assertEquals("0", lblScoreO.getText());
    }

    @Test
    void testMultipleMoves_accumulatesScoresCorrectly() {
        int totalX = 0, totalO = 0;
        for (int i = 1; i <= 5; i++) {
            int player = (i % 2 == 1) ? 1 : 2;
            int moveScore = i * 2;
            if (player == 1) totalX += moveScore;
            else totalO += moveScore;

            MoveRecord rec = createMove(i, player, i-1, i-1, moveScore, totalX, totalO, "Nước " + i);
            panel.addMove(rec);

            assertEquals(i, tableModel.getRowCount());
            assertEquals(String.valueOf(totalX), lblScoreX.getText());
            assertEquals(String.valueOf(totalO), lblScoreO.getText());

            // Kiểm tra dòng cuối cùng
            int lastRow = i - 1;
            String expectedSymbol = (player == 1) ? "X" : "O";
            assertEquals(expectedSymbol, tableModel.getValueAt(lastRow, 1));
            assertEquals(moveScore, tableModel.getValueAt(lastRow, 3));
        }
    }

    @Test
    void testAddMove_scrollsToLastRow() {
        // Thêm nhiều nước đi – không ném ngoại lệ là đủ
        for (int i = 1; i <= 20; i++) {
            panel.addMove(createMove(i, 1, 0, 0, i, i, 0, "Nước " + i));
        }
        assertEquals(20, tableModel.getRowCount());
    }

    @Test
    void testCoordDisplay_correctFormat() {
        // Kiểm tra nhiều tọa độ khác nhau
        int[][] coords = {{0,0}, {0,1}, {1,0}, {2,3}, {4,5}};
        for (int i = 0; i < coords.length; i++) {
            int row = coords[i][0];
            int col = coords[i][1];
            MoveRecord rec = createMove(i+1, 1, row, col, 1, 1, 0, "Test");
            panel.addMove(rec);
            String expected = "(" + (row+1) + ", " + (col+1) + ")";
            assertEquals(expected, tableModel.getValueAt(i, 2));
        }
    }
}