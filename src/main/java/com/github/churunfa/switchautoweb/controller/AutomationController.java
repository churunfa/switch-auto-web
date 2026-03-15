package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.IAutomationService;
import com.github.churunfa.switchautoweb.vo.AutomationTaskProgress;
import com.github.churunfa.switchautoweb.vo.AutomationTaskRequest;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 自动化任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/automation")
@AllArgsConstructor
public class AutomationController {
    
    private final IAutomationService automationService;
    
    /**
     * 执行自动化任务（流式返回进度）
     * 
     * @param request 任务请求
     * @return 任务进度流
     */
    @PostMapping(value = "/execute", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AutomationTaskProgress> executeTask(@RequestBody AutomationTaskRequest request) {
        String deviceName = request.getDeviceName();
        Long modelConfigId = request.getModelConfigId();
        String taskDescription = request.getTaskDescription();

        log.info("收到自动化任务请求：deviceName={}, modelConfigId={}, taskDescription={}",
                deviceName, modelConfigId, taskDescription);

        // 参数校验
        if (deviceName == null || deviceName.isEmpty()) {
            throw new IllegalArgumentException("采集卡设备名称不能为空");
        }

        if (modelConfigId == null) {
            throw new IllegalArgumentException("模型配置 ID 不能为空");
        }

        if (taskDescription == null || taskDescription.isEmpty()) {
            throw new IllegalArgumentException("任务描述不能为空");
        }

        // 执行任务并返回流式进度
        return automationService.executeTask(
                deviceName,
                modelConfigId,
                taskDescription
        );
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Msg<String> health() {
        return Msg.success("自动化服务运行正常");
    }
}
