package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * ============================================================
 *  UC-005: Bảng Xếp Hạng Cục Bộ (Local Leaderboard)
 * ============================================================
 *
 *  LeaderboardService chịu trách nhiệm duy nhất:
 *    1. Đọc file leaderboard.json từ thư mục ứng dụng.
 *    2. Cập nhật bản ghi (wins / losses / draws) của người chơi
 *       theo tổ hợp (playerName + difficulty + boardSize).
 *    3. Ghi lại dữ liệu mới vào file.
 *    4. Trả về danh sách top-N bản ghi được sắp xếp.
 *
 *  Lớp này hoàn toàn độc lập với Swing / UI — chỉ thao tác I/O.
 *  Tất cả các phương thức public đều là static để dùng không cần
 *  khởi tạo đối tượng.
 *
 *  Cấu trúc file JSON (leaderboard.json):
 *  [
 *    {
 *      "playerName": "Lộc",
 *      "difficulty": "Khó",
 *      "boardSize": "5x5",
 *      "wins": 3,
 *      "losses": 1,
 *      "draws": 0
 *    },
 *    ...
 *  ]
 *
 *  Lưu ý: Dự án không dùng thư viện JSON bên ngoài (Gson / Jackson)
 *  để tránh phụ thuộc thêm. JSON được parse / serialize thủ công
 *  với các chuỗi đơn giản.
 * ============================================================
 */
public class LeaderboardService {

    // ─── Hằng số ───────────────────────────────────────────────

    /** Tên file lưu dữ liệu bảng xếp hạng, nằm cạnh file .jar / thư mục chạy */
    private static final String FILE_NAME = "leaderboard.json";

    /** Số bản ghi tối đa hiển thị trong bảng xếp hạng (top N) */
    public static final int TOP_N = 5;

    // ─── Model nội bộ ──────────────────────────────────────────

    /**
     * UC-005: Cấu trúc một bản ghi bảng xếp hạng.
     *
     * Khóa định danh duy nhất = (playerName + difficulty + boardSize).
     * Mỗi tổ hợp này có một bản ghi tích lũy riêng.
     */
    public static class Entry {
        public String playerName;   // Tên người chơi (FR-001-ext: nhập ở GameView)
        public String difficulty;   // Độ khó: "Dễ" hoặc "Khó"
        public String boardSize;    // Kích thước bàn cờ: "3x3" hoặc "5x5"
        public int    wins;         // Số ván thắng
        public int    losses;       // Số ván thua
        public int    draws;        // Số ván hòa

        /** Constructor khởi tạo bản ghi mới với tất cả thống kê = 0 */
        public Entry(String playerName, String difficulty, String boardSize) {
            this.playerName = playerName;
            this.difficulty = difficulty;
            this.boardSize  = boardSize;
            this.wins       = 0;
            this.losses     = 0;
            this.draws      = 0;
        }

        /**
         * 5.1.4: Tổng số ván dùng làm tiêu chí phụ khi sắp xếp BXH
         *        (phân biệt người cùng wins → ai nhiều totalGames hơn đứng trên).
         */
        public int totalGames() {
            return wins + losses + draws;
        }

        /**
         * 5.1.1: Khóa tìm kiếm theo Tên + Độ khó + Cỡ bàn cờ.
         */
        public String key() {
            return playerName + "|" + difficulty + "|" + boardSize;
        }
    }

    // ─── API công khai ─────────────────────────────────────────

    /**
     * 5.1.1 - 5.1.3, AF-03: Ghi nhận và cộng dồn kết quả vào leaderboard.json.
     *
     * Bước 5.1.1: Tìm/tạo bản ghi theo tổ hợp (playerName + difficulty + boardSize).
     * Bước 5.1.2: Xác định loại kết quả và cộng dồn chỉ số tương ứng.
     * Bước 5.1.3: Lưu lại toàn bộ danh sách xuống file sau khi cập nhật.
     *
     * @param playerName    Tên người chơi (lấy từ GameView)
     * @param difficulty    Độ khó: "Dễ" hoặc "Khó"
     * @param boardSize     Chuỗi kích thước: "3x3" hoặc "5x5"
     * @param resultMessage Kết quả ván đấu từ ResultController,
     *                      ví dụ: "Lộc thắng!", "Máy thắng!", "Hòa!"
     */
    public static void record(String playerName, String difficulty,
                              String boardSize, String resultMessage) {
        try {
            List<Entry> entries = loadAll();

            // 5.1.1: Tìm bản ghi theo tổ hợp Tên + Độ khó + Cỡ bàn cờ.
            //        Nếu chưa tồn tại (lần đầu tiên / file mới) thì tạo mới (AF-01: 5.2.2 - 5.2.3).
            Entry target = null;
            for (Entry e : entries) {
                if (e.key().equals(playerName + "|" + difficulty + "|" + boardSize)) {
                    target = e;
                    break;
                }
            }

            if (target == null) {
                // AF-01 / 5.2.3: Bản ghi của người chơi được tạo mới với số liệu ban đầu bằng 0.
                target = new Entry(playerName, difficulty, boardSize);
                entries.add(target);
            }

            // 5.1.2: Cộng dồn Thua / Hòa / Thắng theo chuỗi kết quả.
            if (resultMessage.contains("Máy thắng")) {
                target.losses++;
            } else if (resultMessage.contains("Hòa")) {
                target.draws++;
            } else {
                target.wins++;
            }

            // 5.1.3: Lưu lại toàn bộ dữ liệu BXH sau khi đã cập nhật.
            //        AF-01 / 5.2.4: Tạo mới file nếu chưa tồn tại.
            saveAll(entries);

        } catch (IOException ex) {
            // AF-03 / 5.4.2: Ghi lỗi ra console, không làm sập ứng dụng.
            System.err.println("[LeaderboardService] Không thể ghi leaderboard: " + ex.getMessage());
        }
    }

    /**
     * 5.1.4, AF-03: Truy vấn Top 5, sắp xếp theo wins giảm dần rồi totalGames giảm dần.
     *               Nếu lỗi I/O, trả danh sách rỗng để ResultView vẫn hiển thị được (→ AF-04).
     */
    public static List<Entry> getTopEntries() {
        try {
            List<Entry> entries = loadAll();

            // 5.1.4: Sắp xếp: wins giảm dần → totalGames giảm dần (tiêu chí phụ).
            entries.sort((a, b) -> {
                if (b.wins != a.wins) return b.wins - a.wins;
                return b.totalGames() - a.totalGames();
            });

            return entries.subList(0, Math.min(TOP_N, entries.size()));

        } catch (IOException ex) {
            // AF-03 / 5.4.4: Lỗi đọc → coi như BXH rỗng → chuyển sang AF-04.
            System.err.println("[LeaderboardService] Không thể đọc leaderboard: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    // ─── Phương thức nội bộ (I/O & JSON thủ công) ─────────────

    /**
     * 5.1.1 / AF-01 / 5.2.1 - 5.2.2: Đọc toàn bộ bản ghi;
     * file chưa tồn tại (lần chạy đầu tiên) thì xem như danh sách rỗng.
     */
    private static List<Entry> loadAll() throws IOException {
        Path path = getFilePath();
        if (!Files.exists(path)) {
            // AF-01 / 5.2.1 - 5.2.2: File chưa tồn tại → khởi tạo danh sách trống.
            return new ArrayList<>();
        }

        String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();

        // File rỗng hoặc chỉ có mảng rỗng
        if (json.isEmpty() || json.equals("[]")) {
            return new ArrayList<>();
        }

        return parseJsonArray(json);
    }

    /**
     * 5.1.3 / AF-01 / 5.2.4: Ghi lại leaderboard.json sau khi cộng dồn kết quả.
     * Tạo mới file nếu chưa tồn tại.
     */
    private static void saveAll(List<Entry> entries) throws IOException {
        Path path = getFilePath();
        String json = serializeJsonArray(entries);
        Files.write(path, json.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * UC-005: File leaderboard.json nằm trong thư mục chạy ứng dụng (user.dir),
     * tương thích cả môi trường IDE lẫn file .jar.
     */
    private static Path getFilePath() {
        return Paths.get(System.getProperty("user.dir"), FILE_NAME);
    }

    // ─── JSON thủ công (không dùng thư viện ngoài) ─────────────

    /**
     * 5.1.1 / 5.1.4: Parse JSON cục bộ theo đúng cấu trúc leaderboard.json.
     */
    private static List<Entry> parseJsonArray(String json) {
        List<Entry> result = new ArrayList<>();

        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]"))   json = json.substring(0, json.length() - 1);
        json = json.trim();

        if (json.isEmpty()) return result;

        List<String> objects = splitObjects(json);
        for (String obj : objects) {
            Entry e = parseObject(obj.trim());
            if (e != null) result.add(e);
        }
        return result;
    }

    /**
     * 5.1.1 / 5.1.4: Tách từng object JSON trong mảng leaderboard.
     */
    private static List<String> splitObjects(String s) {
        List<String> list  = new ArrayList<>();
        int depth          = 0;
        int start          = -1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    list.add(s.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return list;
    }

    /**
     * 5.1.1 / 5.1.4 / AF-03: Chuyển một object JSON thành Entry,
     * bỏ qua bản ghi lỗi (không crash ứng dụng).
     */
    private static Entry parseObject(String obj) {
        try {
            String playerName = extractString(obj, "playerName");
            String difficulty = extractString(obj, "difficulty");
            String boardSize  = extractString(obj, "boardSize");
            int wins          = extractInt(obj, "wins");
            int losses        = extractInt(obj, "losses");
            int draws         = extractInt(obj, "draws");

            Entry e    = new Entry(playerName, difficulty, boardSize);
            e.wins     = wins;
            e.losses   = losses;
            e.draws    = draws;
            return e;
        } catch (Exception ex) {
            System.err.println("[LeaderboardService] Bỏ qua bản ghi lỗi: " + obj);
            return null;
        }
    }

    /** 5.1.1 / 5.1.4: Trích xuất giá trị chuỗi từ JSON object. */
    private static String extractString(String obj, String key) {
        String search = "\"" + key + "\"";
        int idx = obj.indexOf(search);
        if (idx < 0) return "";
        int colon  = obj.indexOf(':', idx + search.length());
        int q1     = obj.indexOf('"', colon + 1);
        int q2     = obj.indexOf('"', q1 + 1);
        return obj.substring(q1 + 1, q2);
    }

    /** 5.1.1 / 5.1.4: Trích xuất giá trị số nguyên từ JSON object. */
    private static int extractInt(String obj, String key) {
        String search = "\"" + key + "\"";
        int idx = obj.indexOf(search);
        if (idx < 0) return 0;
        int colon = obj.indexOf(':', idx + search.length());
        StringBuilder sb = new StringBuilder();
        for (int i = colon + 1; i < obj.length(); i++) {
            char c = obj.charAt(i);
            if (Character.isDigit(c)) sb.append(c);
            else if (sb.length() > 0) break;
        }
        return sb.length() > 0 ? Integer.parseInt(sb.toString()) : 0;
    }

    /**
     * 5.1.3: Serialize danh sách Entry để ghi lại leaderboard.json.
     */
    private static String serializeJsonArray(List<Entry> entries) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            sb.append("  {\n");
            sb.append("    \"playerName\": \"").append(escapeName(e.playerName)).append("\",\n");
            sb.append("    \"difficulty\": \"").append(e.difficulty).append("\",\n");
            sb.append("    \"boardSize\": \"").append(e.boardSize).append("\",\n");
            sb.append("    \"wins\": ").append(e.wins).append(",\n");
            sb.append("    \"losses\": ").append(e.losses).append(",\n");
            sb.append("    \"draws\": ").append(e.draws).append("\n");
            sb.append("  }");
            if (i < entries.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /** 5.1.3: Escape tên người chơi trước khi ghi JSON. */
    private static String escapeName(String name) {
        return name.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
