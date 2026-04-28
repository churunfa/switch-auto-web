package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.vo.ModelInfoVO;
import com.github.churunfa.switchautoweb.vo.Msg;

public interface ModelConfigService {
    
    boolean testConnection(ModelInfoVO modelInfoVO);
    
    boolean addModelConfig(ModelInfoVO modelInfoVO);
    
    /**
     * 验证模型信息
     * @param modelInfoVO 模型信息
     * @return 如果验证失败返回错误消息，验证通过返回 null
     */
    Msg<Boolean> validateModelInfo(ModelInfoVO modelInfoVO);
}
