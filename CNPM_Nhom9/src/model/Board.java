package model;

public class Board {
	public static final int SIZE = 3;
	public static final int WIN_CONDITION = 3;

	private int[][] matrix;
	private boolean isXTurn; // true = X (người chơi), false = O (AI)

	public Board() {
		this.matrix = new int[SIZE][SIZE];
		this.isXTurn = true;
	}

	public boolean makeMove(int row, int col) {
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || matrix[row][col] != 0) {
			return false;
		}
		matrix[row][col] = isXTurn ? 1 : 2;
		isXTurn = !isXTurn;
		return true;
	}

	public boolean checkWin(int row, int col) {
		int player = isXTurn ? 2 : 1;
		return getWinLine(row, col, player) != null;
	}
	public int[][] getWinLine(int row, int col) {
		int player = isXTurn ? 2 : 1;
		return findWinLineOnBoard(player);
	}
	
	private int[][] findWinLineOnBoard(int player) {
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };

		for (int r = 0; r < SIZE; r++) {
			for (int c = 0; c < SIZE; c++) {
				if (matrix[r][c] != player)
					continue;

				for (int[] dir : directions) {
					java.util.List<int[]> line = new java.util.ArrayList<>();
					line.add(new int[] { r, c });

					int nr = r + dir[0], nc = c + dir[1];
					while (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && matrix[nr][nc] == player) {
						line.add(new int[] { nr, nc });
						nr += dir[0];
						nc += dir[1];
					}

					if (line.size() >= WIN_CONDITION) {
						return line.toArray(new int[0][]);
					}
				}
			}
		}
		return null;
	}
	public int[][] getWinLine(int row, int col, int player) {
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };
		for (int[] dir : directions) {
			java.util.List<int[]> forward = collectDirection(row, col, dir[0], dir[1], player);
			java.util.List<int[]> backward = collectDirection(row, col, -dir[0], -dir[1], player);

			if (forward.size() + backward.size() + 1 >= WIN_CONDITION) {
				java.util.List<int[]> line = new java.util.ArrayList<>();
				java.util.Collections.reverse(backward);
				line.addAll(backward);
				line.add(new int[] { row, col });
				line.addAll(forward);
				return line.toArray(new int[0][]);
			}
		}
		return null;
	}
	
	public boolean isBoardFull() {
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				if (matrix[i][j] == 0)
					return false;
		return true;
	}
	
	public void undoMove(int row, int col) {
		matrix[row][col] = 0;
		isXTurn = !isXTurn;
	}
	
	public int getCell(int row, int col) {
		return matrix[row][col];
	}

	public boolean isXTurn() {
		return isXTurn;
	}
}
