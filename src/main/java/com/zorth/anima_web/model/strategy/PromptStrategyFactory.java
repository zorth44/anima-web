package com.zorth.anima_web.model.strategy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PromptStrategyFactory {
    private final Map<String, PromptStrategy> strategyMap;

    public PromptStrategyFactory(List<PromptStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PromptStrategy::getStrategyName, Function.identity()));
    }

    public PromptStrategy getStrategy(String strategyName) {
        return strategyMap.getOrDefault(strategyName, strategyMap.get("anime-recommendation"));
    }
} 