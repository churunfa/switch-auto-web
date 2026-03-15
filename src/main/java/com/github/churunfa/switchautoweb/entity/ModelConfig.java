package com.github.churunfa.switchautoweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("model_config")
public class ModelConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String modelId;
    
    private String modelName;
    
    private String apiKey;
    
    private String baseUrl;
    
    private String modelType;
    
    private Integer status;
    
    private Long createTime;
    
    private Long updateTime;
}
