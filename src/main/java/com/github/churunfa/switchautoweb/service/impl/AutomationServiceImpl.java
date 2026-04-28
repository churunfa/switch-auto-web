package com.github.churunfa.switchautoweb.service.impl;

import com.github.churunfa.switchautoweb.service.IAutomationService;
import com.github.churunfa.switchautoweb.vo.AutomationTaskProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

/**
 * 自动化任务服务实现
 */
@Slf4j
@Service
public class AutomationServiceImpl implements IAutomationService {
    
    @Override
    public Flux<AutomationTaskProgress> executeTask(String deviceName, Long modelConfigId, String taskDescription) {
        String taskId = UUID.randomUUID().toString();
        
        log.info("开始执行自动化任务：taskId={}, deviceName={}, modelConfigId={}", 
                taskId, deviceName, modelConfigId);
        
        // 模拟流式进度推送
        return Flux.just(
                // 阶段 1: 初始化
                AutomationTaskProgress.builder()
                        .taskId(taskId)
                        .stage("INITIALIZING")
                        .progress(10)
                        .message("正在初始化任务...")
                        .success(true)
                        .build(),
                
                // 阶段 2: 连接采集卡
                AutomationTaskProgress.builder()
                        .taskId(taskId)
                        .stage("CONNECTING_DEVICE")
                        .progress(20)
                        .message("正在连接采集卡设备：" + deviceName)
                        .success(true)
                        .build(),
                
                // 阶段 3: 采集画面
                AutomationTaskProgress.builder()
                        .taskId(taskId)
                        .stage("CAPTURING_FRAME")
                        .progress(40)
                        .message("正在采集画面...")
                        .success(true)
                        .build(),
                
                // 阶段 4: 加载模型
                AutomationTaskProgress.builder()
                        .taskId(taskId)
                        .stage("LOADING_MODEL")
                        .progress(60)
                        .message("正在加载模型配置，ID: " + modelConfigId)
                        .success(true)
                        .build(),
                
                // 阶段 5: 模型推理
                AutomationTaskProgress.builder()
                        .taskId(taskId)
                        .stage("MODEL_INFERENCE")
                        .progress(80)
                        .message("正在执行模型推理：" + taskDescription)
                        .success(true)
                        .build(),
                
                // 阶段 6: 完成任务
                AutomationTaskProgress.builder()
                        .taskId(taskId)
                        .stage("COMPLETED")
                        .progress(100)
                        .message("任务执行完成")
                        .success(true)
                        .build()
        )
        // 模拟每个阶段之间的延迟（实际使用时可以删除）
        .delayElements(Duration.ofSeconds(1));
    }
}
