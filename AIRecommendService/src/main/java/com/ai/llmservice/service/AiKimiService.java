package com.ai.llmservice.service;

import com.ai.llmservice.config.ApiConfig;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AiKimiService {

    private final ArkService arkService;
    private final ApiConfig apiConfig;

    @Autowired
    public AiKimiService(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        // 初始化 ArkService
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();

        this.arkService = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(apiConfig.getArkBaseUrl())
                .apiKey("2e1e6bb7-1903-4316-a3be-19f983954041")
                .build();
    }

    /**
     * 接收拼好的 Prompt，调用豆包大模型返回结果
     */
    public String completions(String prompt) throws Exception {
        // 构造对话
        List<ChatMessage> messages = new ArrayList<>();
        // system
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content("你是豆包，是由字节跳动开发的 AI 人工智能助手。")
                .build());
        // user
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(prompt)
                .build());

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(apiConfig.getArkModel())
                .messages(messages)
                .build();

        // 标准请求
        StringBuilder finalResult = new StringBuilder();
        arkService.createChatCompletion(request).getChoices().forEach(choice -> {
            finalResult.append(choice.getMessage().getContent());
        });

        // 如果需要流式，可以写 arkService.streamChatCompletion(...)
        return finalResult.toString();
    }
}
