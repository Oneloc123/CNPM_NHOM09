package test;


import controller.GameController;

public class Test {
    /*
    UC1.1.1: Người chơi ấn vào file exe
     */
    // Hàm main - điểm bắt đầu của chương trình
    public static void main(String[] args) {

        // Khởi tạo GameController để điều khiển luồng game
        new GameController();
    }

}
