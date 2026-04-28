package com.github.churunfa.switchautoweb.service.impl;

import com.github.churunfa.switchautoweb.entity.ModelConfig;
import com.github.churunfa.switchautoweb.mapper.ModelConfigMapper;
import com.github.churunfa.switchautoweb.service.ModelConfigService;
import com.github.churunfa.switchautoweb.vo.ModelInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class ModelConfigServiceImpl implements ModelConfigService {

    private final ModelConfigMapper modelConfigMapper;

    @Override
    public boolean testConnection(ModelInfoVO modelInfoVO) {
        try {
            log.info("开始测试模型连接：{}", modelInfoVO.getModelName());
            
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .apiKey(modelInfoVO.getApiKey())
                    .baseUrl(modelInfoVO.getBaseUrl())
                    .build();

            ChatModel chatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model(modelInfoVO.getModelId())
                            .build())
                    .build();

            String response = Objects.requireNonNull(chatModel.call(new Prompt("Hello"))
                            .getResult())
                    .getOutput()
                    .getText();

            if (response != null && !response.trim().isEmpty()) {
                log.info("模型连接测试成功：{}", modelInfoVO.getModelName());
                return true;
            } else {
                log.error("模型返回空响应：{}", modelInfoVO.getModelName());
                return false;
            }
        } catch (Exception e) {
            log.error("模型连接测试失败：{} - {}", modelInfoVO.getModelName(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean addModelConfig(ModelInfoVO modelInfoVO) {
        try {
            ModelConfig modelConfig = ModelInfoVO.toEntity(modelInfoVO);
            modelConfig.setStatus(1);
            modelConfig.setCreateTime(Instant.now().toEpochMilli());
            modelConfig.setUpdateTime(Instant.now().toEpochMilli());

            int result = modelConfigMapper.insert(modelConfig);
            log.info("模型配置写入数据库：{}, 影响行数：{}", modelInfoVO.getModelName(), result);
            return result > 0;
        } catch (Exception e) {
            log.error("模型配置写入数据库失败：{} - {}", modelInfoVO.getModelName(), e.getMessage(), e);
            return false;
        }
    }
}
