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
