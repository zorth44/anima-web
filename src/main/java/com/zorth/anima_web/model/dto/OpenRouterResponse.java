package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenRouterResponse {
    private String id;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    public static class Choice {
        private Message message;
        private String finishReason;
    }
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
    
    @Data
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
} 