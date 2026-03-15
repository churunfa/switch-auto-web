package com.github.churunfa.switchautoweb.config;

import com.github.churunfa.switchautoweb.service.impl.CaptureCardServiceMacOS;
import com.github.churunfa.switchautoweb.service.impl.CaptureCardServiceWindows;
import com.github.churunfa.switchautoweb.service.ICaptureCardService;
import com.github.churunfa.switchautoweb.utils.PlatformUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 采集卡服务配置
 * 根据当前运行平台自动注入对应的实现
 */
@Configuration
public class CaptureCardServiceConfig {
    
    @Bean
    public ICaptureCardService captureCardService() {
        PlatformUtils.Platform platform = PlatformUtils.getCurrentPlatform();

        return switch (platform) {
            case MACOS -> new CaptureCardServiceMacOS();
            case WINDOWS -> new CaptureCardServiceWindows();
            default ->
                // Linux 或其他平台暂时使用 Windows 实现（基于 JavaCV）
                    new CaptureCardServiceWindows();
        };
    }
}
