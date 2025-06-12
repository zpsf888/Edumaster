package com.ai.llmservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Value("${ark.apiKey:}")
    private String arkApiKey;

    @Value("${ark.baseUrl:https://ark.cn-beijing.volces.com/api/v3}")
    private String arkBaseUrl;

    // 例如：ep-20241231000422-gtccc，只是演示
    @Value("${ark.model:ep-20241231000422-gtccc}")
    private String arkModel;

    public String getArkApiKey() {
        return arkApiKey;
    }

    public String getArkBaseUrl() {
        return arkBaseUrl;
    }

    public String getArkModel() {
        return arkModel;
    }
}
