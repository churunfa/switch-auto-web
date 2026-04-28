package com.github.churunfa.switchautoweb.service.impl;

import com.github.churunfa.switchautoweb.service.IAutomationService;
import com.github.churunfa.switchautoweb.service.ModelConfigService;
import com.github.churunfa.switchautoweb.vo.AutomationTaskProgress;
import com.github.churunfa.switchautoweb.vo.ModelInfoVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

/**
 * 自动化任务服务实现 - 支持动态模型配置与视觉拓扑闭环
 */
@Slf4j
@Service
@AllArgsConstructor
public class AutomationServiceImpl implements IAutomationService {

    private final ModelConfigService modelConfigService;

    // 注入 AIConfig 中定义的 Function Bean
    private final Function<String, byte[]> captureGameScreen;

    public final Function<CombinationGraphVO, Boolean> graphExec;

    private static final String SYSTEM_PROMPT = """
                你是一个高级游戏自动化助手。
                你的操作流程：
                1. 首先调用 'captureGameScreen' 获取当前实时画面（传入设备名称）。
                2. 分析获取到的图片，识别游戏状态、目标和障碍。
                3. 根据分析结果，构造一个 CombinationGraphVO 拓扑图对象。
                4. 调用 'graphExec' 下发该拓扑图指令。
                
                限制条件：
                - 坐标系务必以 720x405 为基准。
                - 必须基于图片事实进行决策，不要凭空想象。
                """;

    @Override
    public Flux<AutomationTaskProgress> executeTask(String deviceName, Long modelConfigId, String taskDescription) {
        String taskId = UUID.randomUUID().toString();

        log.info("接收到自动化请求：taskId={}, deviceName={}", taskId, deviceName);

        return Flux.create(sink -> {
            try {
                ModelInfoVO modelInfo = modelConfigService.getById(modelConfigId);
                if (modelInfo == null) {
                    sink.error(new RuntimeException("模型配置不存在"));
                    return;
                }

                sink.next(createProgress(taskId, "INIT", 20, "正在初始化 AI 引擎...", true));

                // 执行 AI 周期
                runAiCycle(modelInfo, deviceName, taskDescription, taskId, sink);

                sink.complete();
            } catch (Exception e) {
                log.error("任务执行异常", e);
                sink.next(createProgress(taskId, "ERROR", 0, "失败: " + e.getMessage(), false));
                sink.complete();
            }
        });
    }

    private String getTopologyRules() {
        try {
            // 假设文件在 resources 下
            Resource resource = new ClassPathResource("prompts/topology-rules.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("加载规则文件失败", e);
            return "";
        }
    }

    private void runAiCycle(ModelInfoVO modelInfo, String deviceName,
                            String taskDescription, String taskId,
                            FluxSink<AutomationTaskProgress> sink) {

        // 1. 构建底层 API
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(modelInfo.getBaseUrl())
                .apiKey(modelInfo.getApiKey())
                .build();

        // 2. 编程式定义工具
        ToolCallback captureTool = FunctionToolCallback.builder("captureGameScreen",
                        () -> captureGameScreen.apply(deviceName))
                .description("获取当前游戏画面的最新截图，用于分析实时状况")
                .build();

        ToolCallback execTool = FunctionToolCallback.builder("graphExec", graphExec)
                .description("根据视觉分析结果，下发自动化执行拓扑图指令")
                .inputType(CombinationGraphVO.class)
                .build();

        // 3. 构建 ChatModel
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(modelInfo.getModelId())
                        .temperature(0.1)
                        .build())
                .build();

        // 4. 使用 ChatClient 封装
        ChatClient chatClient = ChatClient.create(chatModel);

        sink.next(createProgress(taskId, "AI_START", 40, "获取画面并请求 AI 决策...", true));

        // 5. 获取初始图片
        byte[] initialImage = captureGameScreen.apply(deviceName);

        // 6. 发起调用 (修正 toolCallbacks 传入方式)
        String rules = getTopologyRules();
        String dynamicSystemPrompt = SYSTEM_PROMPT + "\n\n### 拓扑构造规则：\n" + rules;

        String aiResponse = chatClient.prompt()
                .system(dynamicSystemPrompt)
                .user(u -> {
                    u.text(String.format("任务目标：%s。当前设备：%s", taskDescription, deviceName));
                    if (initialImage != null) {
                        // 确保 ByteArrayResource 被正确导入
                        u.media(MimeTypeUtils.IMAGE_JPEG, new ByteArrayResource(initialImage));
                    }
                })
                // 使用 .toolCallbacks() 传入 ToolCallback 对象
                .toolCallbacks(captureTool, execTool)
                .call()
                .content();

        log.info("AI 周期执行完成：{}", aiResponse);
        sink.next(createProgress(taskId, "AI_DONE", 100, "操作指令已执行", true));
    }

    private AutomationTaskProgress createProgress(String taskId, String stage, int progress, String msg, boolean success) {
        return AutomationTaskProgress.builder()
                .taskId(taskId)
                .stage(stage)
                .progress(progress)
                .message(msg)
                .success(success)
                .build();
    }
}