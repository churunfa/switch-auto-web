package com.github.churunfa.switchautoweb.config;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class FastJsonConfiguration {

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        // 1. 创建转换器
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        // 2. 配置 Fastjson 规则
        FastJsonConfig config = new FastJsonConfig();
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setCharset(StandardCharsets.UTF_8);

        // 序列化特性
        config.setWriterFeatures(JSONWriter.Feature.WriteMapNullValue);

        converter.setFastJsonConfig(config);

        // 3. 设置支持的媒体类型
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));

        return converter;
    }
}