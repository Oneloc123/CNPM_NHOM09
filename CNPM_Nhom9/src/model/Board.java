package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
	private final int size;
	private final int winCondition;
	private final int[][] matrix;
	private boolean isXTurn;
	private int moveCount;

	// Khởi tạo bàn cờ với kích thước động
	public Board(int size) {
		this.size = size;
		// Nếu bàn 5x5 thì cần 4 hoặc 5 quân để thắng (ở đây ví dụ là 4, bạn có thể sửa thành 5)
		this.winCondition = (size == 5) ? 4 : 3; 
		this.matrix = new int[size][size];
		this.isXTurn = true;
		this.moveCount = 0;
	}

	/** Trả về điều kiện thắng (số quân liên tiếp cần thiết). */
	public int getWinCondition() {
		return winCondition;
	}

	public int getSize() {
		return size;
	}

	public int getMoveCount() {
		return this.moveCount;
	}


    // UC 2.1.4 Hệ thống kiểm tra tính hợp lệ của nước đi
    // Mô tả:
    // Kiểm tra:
    // tọa độ có nằm trong phạm vi bàn cờ.
    // Ô cờ đã được đánh dấu trước đó hay chưa.
	public synchronized int makeMove(int row, int col) {
        //        Kết quả trả về
        //
        //        Giá trị	Ý nghĩa
        //        0	        Nước đi không hợp lệ
        //        1	        Người chơi X
        //        2	        Người chơi O


		if (row < 0 || row >= size || col < 0 || col >= size || matrix[row][col] != 0) {
            // UC 2.2.1 nước đi không hợp lệ như các trường hợp đã nêu trên trả về giá trị 0
			return 0;
		}

        // UC 2.1.5 Hệ thống ghi nhận nước đi hợp lệ
        // ( nếu kết quả kiểm tra của bước 2.1.4 khác 0)
        //          Mô tả:
        //                  Đánh dấu quân cờ vào ma trận bàn cờ.
        //                  Cập nhật lượt chơi.
        //                  Tăng số lượng nước đi.

		int player = isXTurn ? 1 : 2; // kiểm tra lượt của người hoặc AI
		matrix[row][col] = player; // đánh dấu trên bàn cờ ma trận 2 chiều lượt của người hoặc AI đã đi
		isXTurn = !isXTurn; // đổi lượt giữa X và O
		this.moveCount++; // tăng số lượng nước đi
		return player;
	}

	public synchronized void undoMove(int row, int col) {
		matrix[row][col] = 0;
		isXTurn = !isXTurn;
		this.moveCount--;
	}

	public int[][] getWinLine(int row, int col, int player) {
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };

		for (int[] dir : directions) {
			List<int[]> forward = collectDirection(row, col, dir[0], dir[1], player);
			List<int[]> backward = collectDirection(row, col, -dir[0], -dir[1], player);

			if (forward.size() + backward.size() + 1 >= winCondition) {
				List<int[]> line = new ArrayList<>(backward);
				Collections.reverse(line);
				line.add(new int[] { row, col });
				line.addAll(forward);
				return line.toArray(new int[0][]);
			}
		}
		return null;
	}

	private List<int[]> collectDirection(int row, int col, int dr, int dc, int player) {
		List<int[]> cells = new ArrayList<>();
		int r = row + dr;
		int c = col + dc;
		while (r >= 0 && r < size && c >= 0 && c < size && matrix[r][c] == player) {
			cells.add(new int[] { r, c });
			r += dr;
			c += dc;
		}
		return cells;
	}

	public boolean isBoardFull() {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (matrix[i][j] == 0)
					return false;
		return true;
	}

	public synchronized int getCell(int row, int col) {
		return matrix[row][col];
	}

	public synchronized boolean isXTurn() {
		return isXTurn;
	}
}
