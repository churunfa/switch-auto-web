package com.github.churunfa.switchautoweb.vo;

import com.github.churunfa.switchautoweb.entity.ModelConfig;
import lombok.Data;

@Data
public class ModelInfoVO {
    private String modelId;
    private String modelName;
    private String apiKey;
    private String baseUrl;
    private String modelType;

    public static ModelInfoVO toVO(ModelConfig entity) {
        ModelInfoVO vo = new ModelInfoVO();
        vo.setModelId(entity.getModelId());
        vo.setModelName(entity.getModelName());
        vo.setApiKey(entity.getApiKey());
        vo.setBaseUrl(entity.getBaseUrl());
        vo.setModelType(entity.getModelType());
        return vo;
    }

    public static ModelConfig toEntity(ModelInfoVO vo) {
        ModelConfig entity = new ModelConfig();
        entity.setModelId(vo.getModelId());
        entity.setModelName(vo.getModelName());
        entity.setApiKey(vo.getApiKey());
        entity.setBaseUrl(vo.getBaseUrl());
        entity.setModelType(vo.getModelType());
        return entity;
    }
}
