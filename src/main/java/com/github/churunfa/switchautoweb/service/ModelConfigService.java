package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.vo.ModelInfoVO;
import com.github.churunfa.switchautoweb.vo.Msg;

public interface ModelConfigService {
    
    boolean testConnection(ModelInfoVO modelInfoVO);
    
    boolean addModelConfig(ModelInfoVO modelInfoVO);
    
    /**
     * 根据 ID 获取模型配置
     * @param id 模型配置 ID
     * @return 模型配置信息，不存在返回 null
     */
    ModelInfoVO getById(Long id);
    
    /**
     * 验证模型信息
     * @param modelInfoVO 模型信息
     * @return 如果验证失败返回错误消息，验证通过返回 null
     */
    Msg<Boolean> validateModelInfo(ModelInfoVO modelInfoVO);
}
