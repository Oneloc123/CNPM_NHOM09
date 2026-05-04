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

	public int getCell(int row, int col) {
		return matrix[row][col];
	}

	public boolean isXTurn() {
		return isXTurn;
	}
}