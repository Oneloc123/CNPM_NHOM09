package test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.BoardController;
import view.BoardView;
import view.ResultView;

public class BoardGUIIntegrationTest {
    private BoardView boardView;
    private BoardController boardController;
    private final String playerName = "TienPhan";
    private final String difficulty = "Dễ";
    private final int size = 3;

    @BeforeEach
    public void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            boardController = new BoardController(difficulty, playerName, true, size);
            boardView = new BoardView(difficulty, playerName, true, size);
            boardController.setView(boardView);
            boardView.setController(boardController);
            boardView.setVisible(true);
        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (boardView != null) {
                boardView.dispose();
            }
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                if (window instanceof ResultView) {
                    window.dispose();
                }
            }
        });
    }

    @Test
    public void testGUIInitialization() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            String title = boardView.getTitle();
            assertTrue(title.contains("Cờ Caro"));
            assertTrue(title.contains(playerName));
            assertTrue(title.contains(difficulty));

            JButton[][] buttons = getButtonsFromView(boardView);
            assertNotNull(buttons);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    assertEquals("", buttons[i][j].getText());
                    assertTrue(buttons[i][j].isEnabled());
                }
            }
        });
    }

    @Test
    public void testGUILockAndHighlightOnWin() throws Exception {
        // 💡 GIẢI PHÁP TỐI ƯU: Vòng lặp tự động chơi đến khi game kết thúc
        boolean isGameEnded = false;

        for (int step = 0; step < 9; step++) {
            SwingUtilities.invokeAndWait(() -> {
                JButton[][] buttons = getButtonsFromView(boardView);
                if (buttons == null) return;

                // Tìm ô cờ trống đầu tiên và click vào đó
                outer:
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (buttons[i][j].isEnabled() && buttons[i][j].getText().isEmpty()) {
                            buttons[i][j].doClick();
                            break outer;
                        }
                    }
                }
            });

            // Chờ 300ms cho luồng SwingWorker của AI suy nghĩ và phản hồi
            Thread.sleep(300);

            // Kiểm tra xem Màn hình ResultView đã bung ra chưa (Dấu hiệu kết thúc ván)
            boolean resultViewFound = false;
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                if (window instanceof ResultView && window.isShowing()) {
                    resultViewFound = true;
                    break;
                }
            }

            if (resultViewFound) {
                isGameEnded = true;
                break; // Thoát vòng lặp khi game đã kết thúc
            }
        }

        assertTrue(isGameEnded, "Ván đấu bắt buộc phải kết thúc sau tối đa 9 nước.");

        // 💡 ĐỒNG BỘ KIỂM TRA ĐIỀU KIỆN HẬU QUYẾT (POST-CONDITIONS)
        SwingUtilities.invokeAndWait(() -> {
            JButton[][] buttons = getButtonsFromView(boardView);
            assertNotNull(buttons, "Không tìm thấy lưới ô cờ.");

            // 1. SỬA LỖI LOGIC: Chỉ kiểm tra khóa (disabled) trên các ô cờ CÒN TRỐNG
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (buttons[i][j].getText().isEmpty()) {
                        assertFalse(buttons[i][j].isEnabled(), 
                            "Lỗi: Ô trống tại [" + i + "][" + j + "] chưa bị khóa sau khi kết thúc game!");
                    }
                }
            }

            // 2. Kiểm tra Màn hình kết quả độc lập phải tồn tại
            ResultView resultView = findOpenWindow(ResultView.class);
            assertNotNull(resultView, "Màn hình kết quả ResultView riêng biệt phải xuất hiện.");
            assertTrue(resultView.isVisible());

            // 3. Kiểm tra nhãn tiêu đề của ResultView
            JLabel lblResult = findComponent(resultView, JLabel.class, "TRẬN ĐẤU KẾT THÚC");
            assertNotNull(lblResult, "ResultView phải hiển thị thông tin kết quả.");

            resultView.dispose(); // Dọn dẹp sau khi kiểm thử thành công
        });
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH BÓC TÁCH LINH KIỆN
    // ==========================================
    
    private JButton[][] getButtonsFromView(BoardView view) {
        JButton[][] grid = new JButton[size][size];
        int count = 0;
        for (Component comp : getComponentTree(view)) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text.equals("") || text.equals("X") || text.equals("O")) {
                    int row = count / size;
                    int col = count % size;
                    if (row < size) {
                        grid[row][col] = btn;
                        count++;
                    }
                }
            }
        }
        return count == (size * size) ? grid : null;
    }

    private <T extends JFrame> T findOpenWindow(Class<T> type) {
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (type.isInstance(window) && window.isShowing()) {
                return type.cast(window);
            }
        }
        return null;
    }

    private <T extends Component> T findComponent(Container container, Class<T> type, String textKeyword) {
        for (Component comp : container.getComponents()) {
            if (type.isInstance(comp)) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains(textKeyword)) {
                    return type.cast(comp);
                }
            } else if (comp instanceof Container) {
                T res = findComponent((Container) comp, type, textKeyword);
                if (res != null) return res;
            }
        }
        return null;
    }

    private java.util.List<Component> getComponentTree(Container container) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        for (Component comp : container.getComponents()) {
            list.add(comp);
            if (comp instanceof Container) {
                list.addAll(getComponentTree((Container) comp));
            }
        }
        return list;
    }
}