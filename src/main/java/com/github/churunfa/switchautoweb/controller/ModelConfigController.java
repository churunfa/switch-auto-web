package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.ModelConfigService;
import com.github.churunfa.switchautoweb.vo.ModelInfoVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/model")
public class ModelConfigController {

    private final ModelConfigService modelConfigService;

    @PostMapping("/add-model")
    public Msg<Boolean> addModel(@RequestBody ModelInfoVO modelInfoVO) {
        log.info("收到模型接入请求：{}", modelInfoVO.getModelName());
        
        if (modelInfoVO.getApiKey() == null || modelInfoVO.getApiKey().trim().isEmpty()) {
            return Msg.fail("API Key 不能为空");
        }
        
        if (modelInfoVO.getBaseUrl() == null || modelInfoVO.getBaseUrl().trim().isEmpty()) {
            return Msg.fail("Base URL 不能为空");
        }
        
        if (modelInfoVO.getModelId() == null || modelInfoVO.getModelId().trim().isEmpty()) {
            return Msg.fail("Model ID 不能为空");
        }

        boolean connectionOk = modelConfigService.testConnection(modelInfoVO);
        if (!connectionOk) {
            return Msg.fail("模型连接测试失败，无法完成接入");
        }

        boolean addOk = modelConfigService.addModelConfig(modelInfoVO);
        if (addOk) {
            return Msg.success(true);
        } else {
            return Msg.fail("模型配置写入数据库失败");
        }
    }
}
