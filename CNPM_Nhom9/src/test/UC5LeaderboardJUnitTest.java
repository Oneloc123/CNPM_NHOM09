package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LeaderboardService;
import service.LeaderboardService.Entry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JUnit 5 test riêng cho UC-005: Bảng xếp hạng của tôi.
 */
class UC5LeaderboardJUnitTest {
    private String originalUserDir;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        originalUserDir = System.getProperty("user.dir");
        tempDir = Files.createTempDirectory("uc5-leaderboard-test-");

        // LeaderboardService lưu leaderboard.json theo user.dir,
        // nên test dùng thư mục tạm để không ảnh hưởng dữ liệu thật.
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setProperty("user.dir", originalUserDir);
        deleteRecursively(tempDir);
    }

    @Test
    void getTopEntriesReturnsEmptyListWhenLeaderboardFileDoesNotExist() {
        List<Entry> entries = LeaderboardService.getTopEntries();

        assertEquals(0, entries.size(), "BXH mới phải rỗng");
    }

    @Test
    void recordAggregatesWinsLossesAndDrawsForSamePlayerDifficultyAndBoardSize() {
        LeaderboardService.record("Nam", "Dễ", "3x3", "Nam thắng!");
        LeaderboardService.record("Nam", "Dễ", "3x3", "Máy thắng!");
        LeaderboardService.record("Nam", "Dễ", "3x3", "Hòa!");
        LeaderboardService.record("Nam", "Dễ", "3x3", "Nam thắng!");

        List<Entry> entries = LeaderboardService.getTopEntries();

        assertEquals(1, entries.size(), "Cùng playerName + difficulty + boardSize phải gộp một bản ghi");

        Entry nam = entries.get(0);
        assertEquals("Nam", nam.playerName, "Sai tên người chơi");
        assertEquals("Dễ", nam.difficulty, "Sai độ khó");
        assertEquals("3x3", nam.boardSize, "Sai kích thước bàn");
        assertEquals(2, nam.wins, "Sai số ván thắng");
        assertEquals(1, nam.losses, "Sai số ván thua");
        assertEquals(1, nam.draws, "Sai số ván hòa");
        assertEquals(4, nam.totalGames(), "Sai tổng số ván");
    }

    @Test
    void recordSeparatesEntriesByDifficultyAndBoardSize() {
        LeaderboardService.record("Lan", "Dễ", "3x3", "Lan thắng!");
        LeaderboardService.record("Lan", "Khó", "3x3", "Máy thắng!");
        LeaderboardService.record("Lan", "Dễ", "5x5", "Hòa!");

        List<Entry> entries = LeaderboardService.getTopEntries();

        assertEquals(3, entries.size(), "Khác difficulty hoặc boardSize phải là bản ghi riêng");
        assertNotNull(find(entries, "Lan", "Dễ", "3x3"), "Thiếu bản ghi Lan/Dễ/3x3");
        assertNotNull(find(entries, "Lan", "Khó", "3x3"), "Thiếu bản ghi Lan/Khó/3x3");
        assertNotNull(find(entries, "Lan", "Dễ", "5x5"), "Thiếu bản ghi Lan/Dễ/5x5");
    }

    @Test
    void getTopEntriesSortsByWinsThenTotalGamesDescending() {
        addWins("A", 2);
        addWins("B", 3);
        addWins("C", 3);
        LeaderboardService.record("C", "Dễ", "3x3", "Máy thắng!");
        LeaderboardService.record("C", "Dễ", "3x3", "Hòa!");
        addWins("D", 1);

        List<Entry> entries = LeaderboardService.getTopEntries();

        assertEquals("C", entries.get(0).playerName,
                "C và B cùng 3 thắng, C phải đứng trên vì tổng ván nhiều hơn");
        assertEquals("B", entries.get(1).playerName, "B phải đứng thứ hai");
        assertEquals("A", entries.get(2).playerName, "A có 2 thắng phải đứng sau nhóm 3 thắng");
        assertEquals("D", entries.get(3).playerName, "D có 1 thắng phải đứng cuối");
    }

    @Test
    void getTopEntriesReturnsOnlyTopFiveEntries() {
        for (int i = 1; i <= 7; i++) {
            addWins("Player " + i, i);
        }

        List<Entry> entries = LeaderboardService.getTopEntries();

        assertEquals(LeaderboardService.TOP_N, entries.size(), "BXH chỉ hiển thị Top 5");
        assertEquals("Player 7", entries.get(0).playerName, "Người nhiều thắng nhất phải đứng đầu");
        assertEquals("Player 3", entries.get(4).playerName, "Bản ghi thứ 5 phải là Player 3");
    }

    private void addWins(String playerName, int wins) {
        for (int i = 0; i < wins; i++) {
            LeaderboardService.record(playerName, "Dễ", "3x3", playerName + " thắng!");
        }
    }

    private Entry find(List<Entry> entries, String playerName, String difficulty, String boardSize) {
        for (Entry e : entries) {
            if (e.playerName.equals(playerName)
                    && e.difficulty.equals(difficulty)
                    && e.boardSize.equals(boardSize)) {
                return e;
            }
        }
        return null;
    }

    private void deleteRecursively(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            return;
        }

        try (var paths = Files.walk(path)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
}
