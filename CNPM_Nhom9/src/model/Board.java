package model;


public class Board {
    private int size;
    // Dùng số nguyên để đại diện: 0 = Trống, 1 = X, 2 = O
    private int[][] matrix; 
    private boolean isXTurn;

    public Board(int size) {
        this.size = size;
        this.matrix = new int[size][size];
        this.isXTurn = true; // X đi trước
    }

    // Thực hiện nước đi
    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size || matrix[row][col] != 0) {
            return false; // Nước đi không hợp lệ
        }
        matrix[row][col] = isXTurn ? 1 : 2;
        isXTurn = !isXTurn; // Đổi lượt
        return true;
    }
    public int getCell(int row, int col) {
        return matrix[row][col];
    }

    public boolean isXTurn() {
        return isXTurn;
    }

    public int getSize() {
        return size;
    }

}
