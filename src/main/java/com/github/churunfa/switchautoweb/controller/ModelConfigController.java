package com.github.churunfa.switchautoweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.churunfa.switchautoweb.entity.ModelConfig;
import com.github.churunfa.switchautoweb.mapper.ModelConfigMapper;
import com.github.churunfa.switchautoweb.service.ModelConfigService;
import com.github.churunfa.switchautoweb.vo.ModelInfoVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/model")
public class ModelConfigController {

    private final ModelConfigService modelConfigService;
    private final ModelConfigMapper modelConfigMapper;

    @GetMapping("/list")
    public Msg<List<ModelInfoVO>> listModels() {
        log.info("查询所有模型配置");
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ModelConfig::getCreateTime);
        
        List<ModelConfig> configs = modelConfigMapper.selectList(wrapper);
        List<ModelInfoVO> result = configs.stream()
                .map(config -> {
                    ModelInfoVO vo = ModelInfoVO.toVO(config);
                    vo.setId(config.getId());
                    return vo;
                })
                .collect(Collectors.toList());
        
        return Msg.success(result);
    }

    @PostMapping("/add-model")
    public Msg<Boolean> addModel(@RequestBody ModelInfoVO modelInfoVO) {
        log.info("收到模型接入请求：{}", modelInfoVO.getModelName());
        
        Msg<Boolean> validateResult = modelConfigService.validateModelInfo(modelInfoVO);
        if (validateResult != null) {
            return validateResult;
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

    @DeleteMapping("/delete/{id}")
    public Msg<Boolean> deleteModel(@PathVariable Long id) {
        log.info("删除模型配置：{}", id);
        
        int result = modelConfigMapper.deleteById(id);
        
        if (result > 0) {
            return Msg.success(true);
        } else {
            return Msg.fail("删除失败，模型配置不存在");
        }
    }

    @PutMapping("/update")
    public Msg<Boolean> updateModel(@RequestBody ModelInfoVO modelInfoVO) {
        log.info("更新模型配置：{}", modelInfoVO.getModelName());
        
        Msg<Boolean> validateResult = modelConfigService.validateModelInfo(modelInfoVO);
        if (validateResult != null) {
            return validateResult;
        }

        boolean connectionOk = modelConfigService.testConnection(modelInfoVO);
        if (!connectionOk) {
            return Msg.fail("模型连接测试失败，无法完成更新");
        }

        ModelConfig existingConfig = modelConfigMapper.selectById(modelInfoVO.getId());
        if (existingConfig == null) {
            return Msg.fail("模型配置不存在");
        }

        existingConfig.setModelId(modelInfoVO.getModelId());
        existingConfig.setModelName(modelInfoVO.getModelName());
        existingConfig.setApiKey(modelInfoVO.getApiKey());
        existingConfig.setBaseUrl(modelInfoVO.getBaseUrl());
        existingConfig.setModelType(modelInfoVO.getModelType());
        existingConfig.setUpdateTime(System.currentTimeMillis());

        int result = modelConfigMapper.updateById(existingConfig);
        
        if (result > 0) {
            return Msg.success(true);
        } else {
            return Msg.fail("更新失败");
        }
    }
}
