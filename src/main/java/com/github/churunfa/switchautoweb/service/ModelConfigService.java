package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.vo.ModelInfoVO;

public interface ModelConfigService {
    
    boolean testConnection(ModelInfoVO modelInfoVO);
    
    boolean addModelConfig(ModelInfoVO modelInfoVO);
}
