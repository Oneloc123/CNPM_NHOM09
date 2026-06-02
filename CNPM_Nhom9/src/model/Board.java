package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
	public static final int SIZE = 3;
	public static final int WIN_CONDITION = 3;

	private final int[][] matrix;
	private boolean isXTurn;

	/*
	UC1.1.6.4: Hệ thống khởi tạo lớp Board cho bàn cờ
	 */
	public Board() {

		// Khởi tạo ma trận bàn cờ
		this.matrix = new int[SIZE][SIZE];

		// Thiết lập lượt đi đầu tiên là X
		this.isXTurn = true;
	}


	public synchronized int makeMove(int row, int col) {
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || matrix[row][col] != 0) {
			return 0;
		}
		int player = isXTurn ? 1 : 2;
		matrix[row][col] = player;
		isXTurn = !isXTurn;
		return player;
	}

	public synchronized void undoMove(int row, int col) {
		matrix[row][col] = 0;
		isXTurn = !isXTurn;
	}

	public int[][] getWinLine(int row, int col, int player) {
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };

		for (int[] dir : directions) {
			List<int[]> forward = collectDirection(row, col, dir[0], dir[1], player);
			List<int[]> backward = collectDirection(row, col, -dir[0], -dir[1], player);

			if (forward.size() + backward.size() + 1 >= WIN_CONDITION) {
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
		while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && matrix[r][c] == player) {
			cells.add(new int[] { r, c });
			r += dr;
			c += dc;
		}
		return cells;
	}

	public boolean isBoardFull() {
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
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
