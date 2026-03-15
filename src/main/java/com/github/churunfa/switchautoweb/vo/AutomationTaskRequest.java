package com.github.churunfa.switchautoweb.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自动化任务请求 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationTaskRequest {
    
    /**
     * 采集卡设备名称
     */
    private String deviceName;
    
    /**
     * 模型配置 ID
     */
    private Long modelConfigId;
    
    /**
     * 任务描述
     */
    private String taskDescription;
}
