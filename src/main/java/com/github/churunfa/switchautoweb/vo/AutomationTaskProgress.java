package com.github.churunfa.switchautoweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自动化任务进度 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationTaskProgress {
    
    /**
     * 任务 ID
     */
    private String taskId;
    
    /**
     * 当前阶段（初始化、采集画面、模型推理、执行操作、完成）
     */
    private String stage;
    
    /**
     * 进度百分比（0-100）
     */
    private Integer progress;
    
    /**
     * 状态信息
     */
    private String message;
    
    /**
     * 是否成功
     */
    private Boolean success;
}
