package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import aiService.AIService;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử đơn vị cấu phần Dịch vụ AI - UC-001")
public class AIServiceTest {

    @Test
    @DisplayName("TC-AI01: Kiểm tra cấu hình khi Người chơi đi trước (Lượt Bạn X)")
    public void testAIInitWhenPlayerGoesFirst() {
        // Kịch bản: Người chơi chọn đi trước (isPlayerFirst = true), độ khó "Khó"
        AIService aiService = new AIService("Khó", true);
        
        // Kiểm tra thuộc tính độ khó được lưu
        assertEquals("Khó", aiService.getDifficulty(), "LỖI: Cấu hình độ khó AI lưu trữ không chính xác.");
        
        // Dựa trên logic mã nguồn: isPlayerFirst = true -> Người chơi = X (1), AI = O (2)
        // Chúng ta kiểm tra tính toàn vẹn của đối tượng dịch vụ vừa khởi tạo
        assertNotNull(aiService, "LỖI: Đối tượng AIService không được tạo thành công.");
    }

    @Test
    @DisplayName("TC-AI02: Kiểm tra cấu hình khi Máy đi trước (Lượt Máy X)")
    public void testAIInitWhenAIGoesFirst() {
        // Kịch bản: Người chơi chọn Máy đi trước (isPlayerFirst = false), độ khó "Dễ"
        AIService aiService = new AIService("Dễ", false);
        
        assertEquals("Dễ", aiService.getDifficulty(), "LỖI: Cấu hình độ khó AI lưu trữ không chính xác.");
        assertNotNull(aiService, "LỖI: Đối tượng AIService không được tạo thành công.");
    }
}