package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenRouterRequest {
    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer maxTokens;
    
    @Data
    public static class Message {
        private String role;
        private List<Content> content;
    }
    
    @Data
    public static class Content {
        private String type;
        private String text;
        private ImageUrl imageUrl;
    }
    
    @Data
    public static class ImageUrl {
        private String url;
    }
} 