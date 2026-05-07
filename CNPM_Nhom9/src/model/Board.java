package model;

public class Board {
	public static final int SIZE = 3;
	public static final int WIN_CONDITION = 3;

	private final int[][] matrix;
	private boolean isXTurn;

	public Board() {
		this.matrix = new int[SIZE][SIZE];
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

	public synchronized int getCell(int row, int col) {
		return matrix[row][col];
	}

	public synchronized boolean isXTurn() {
		return isXTurn;
	}
}
