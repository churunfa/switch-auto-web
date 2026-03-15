package com.github.churunfa.switchautoweb.vo;

import com.github.churunfa.switchautoweb.entity.ModelConfig;
import lombok.Data;

@Data
public class ModelInfoVO {
    private Long id;
    private String modelId;
    private String modelName;
    private String apiKey;
    private String baseUrl;
    private String modelType;
    private Integer status;
    private Long createTime;
    private Long updateTime;

    public static ModelInfoVO toVO(ModelConfig entity) {
        ModelInfoVO vo = new ModelInfoVO();
        vo.setId(entity.getId());
        vo.setModelId(entity.getModelId());
        vo.setModelName(entity.getModelName());
        vo.setApiKey(entity.getApiKey());
        vo.setBaseUrl(entity.getBaseUrl());
        vo.setModelType(entity.getModelType());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    public static ModelConfig toEntity(ModelInfoVO vo) {
        ModelConfig entity = new ModelConfig();
        if (vo.getId() != null) {
            entity.setId(vo.getId());
        }
        entity.setModelId(vo.getModelId());
        entity.setModelName(vo.getModelName());
        entity.setApiKey(vo.getApiKey());
        entity.setBaseUrl(vo.getBaseUrl());
        entity.setModelType(vo.getModelType());
        if (vo.getStatus() != null) {
            entity.setStatus(vo.getStatus());
        }
        return entity;
    }
}
