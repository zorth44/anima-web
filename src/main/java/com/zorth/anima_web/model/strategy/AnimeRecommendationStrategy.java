package com.zorth.anima_web.model.strategy;

import com.zorth.anima_web.model.dto.OpenRouterRequest;
import com.zorth.anima_web.model.dto.AnimeRecommendation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnimeRecommendationStrategy implements PromptStrategy {
    private static final String SYSTEM_PROMPT = """
            你是一个专业的动漫推荐助手。用户会向你描述他们想要观看的动漫类型或特点，你需要：
            1. 推荐1-5部符合用户要求的动漫
            2. 每部动漫的推荐格式必须严格遵循以下模板：
               [序号]. [动漫名称] (推荐原因：[原因])
            3. 动漫名称必须准确，不要添加任何额外的描述词
            4. 推荐原因要简洁明了，一句话说明为什么推荐这部动漫
            5. 只返回推荐列表，不要添加任何其他内容
            
            示例格式：
            1. 关于我转生变成史莱姆这档事 (推荐原因：主角转生到异世界成为史莱姆，通过吞噬和进化不断变强)
            2. 无职转生 (推荐原因：主角在异世界重新开始人生，通过努力和智慧逐渐成长)
            """;

    @Override
    public List<OpenRouterRequest.Message> processMessages(List<OpenRouterRequest.Message> originalMessages) {
        List<OpenRouterRequest.Message> processedMessages = new ArrayList<>();
        
        // 添加系统提示词
        OpenRouterRequest.Message systemMessage = new OpenRouterRequest.Message();
        systemMessage.setRole("system");
        OpenRouterRequest.Content systemContent = new OpenRouterRequest.Content();
        systemContent.setType("text");
        systemContent.setText(SYSTEM_PROMPT);
        systemMessage.setContent(List.of(systemContent));
        processedMessages.add(systemMessage);
        
        // 添加用户消息
        processedMessages.addAll(originalMessages);
        
        return processedMessages;
    }

    @Override
    public List<AnimeRecommendation> processResponse(String response) {
        List<AnimeRecommendation> recommendations = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+)\\.\\s*([^(]+)\\s*\\(推荐原因：([^)]+)\\)");
        Matcher matcher = pattern.matcher(response);

        while (matcher.find()) {
            AnimeRecommendation recommendation = new AnimeRecommendation();
            recommendation.setAnimeName(matcher.group(2).trim());
            recommendation.setRecommendationReason(matcher.group(3).trim());
            recommendations.add(recommendation);
        }

        return recommendations;
    }

    @Override
    public String getStrategyName() {
        return "anime-recommendation";
    }
} 