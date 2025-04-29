package com.zorth.anima_web.model.strategy;

import com.zorth.anima_web.model.dto.OpenRouterRequest;
import com.zorth.anima_web.model.dto.AnimeRecommendation;

import java.util.List;

public interface PromptStrategy {
    /**
     * 处理提示词，返回处理后的消息列表
     * @param originalMessages 原始消息列表
     * @return 处理后的消息列表
     */
    List<OpenRouterRequest.Message> processMessages(List<OpenRouterRequest.Message> originalMessages);
    
    /**
     * 处理AI返回的响应，提取结构化的推荐信息
     * @param response AI返回的原始响应
     * @return 结构化的推荐列表
     */
    List<AnimeRecommendation> processResponse(String response);
    
    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getStrategyName();
} 