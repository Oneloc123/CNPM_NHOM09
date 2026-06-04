package model;

/**
 * Lưu thông tin một nước đi: tọa độ, người đi, điểm nước đi, tổng điểm tích lũy.
 */
public class MoveRecord {
    private final int moveNumber;   // Thứ tự nước đi (1, 2, 3, ...)
    private final int player;       // 1 = X, 2 = O
    private final int row;
    private final int col;

    public MoveRecord(int moveNumber, int player, int row, int col) {
        this.moveNumber  = moveNumber;
        this.player      = player;
        this.row         = row;
        this.col         = col;
    }

    public int getMoveNumber()   { return moveNumber; }
    public int getPlayer()       { return player; }
    public int getRow()          { return row; }
    public int getCol()          { return col; }

    /** Ký hiệu người chơi: "X" hoặc "O" */
    public String getPlayerSymbol() { return player == 1 ? "X" : "O"; }

    /** Tọa độ dạng hiển thị: "(hàng, cột)" với index bắt đầu từ 1 */
    public String getCoordDisplay() { return "(" + (row + 1) + ", " + (col + 1) + ")"; }
}