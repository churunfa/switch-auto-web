package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.vo.AutomationTaskProgress;
import reactor.core.publisher.Flux;

/**
 * 自动化任务服务接口
 */
public interface IAutomationService {
    
    /**
     * 执行自动化任务（流式返回进度）
     * @param deviceName 采集卡设备名称
     * @param modelConfigId 模型配置 ID
     * @param taskDescription 任务描述
     * @return 任务进度流
     */
    Flux<AutomationTaskProgress> executeTask(String deviceName, Long modelConfigId, String taskDescription);
}
