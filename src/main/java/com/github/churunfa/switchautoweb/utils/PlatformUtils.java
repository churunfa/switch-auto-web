package com.github.churunfa.switchautoweb.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作系统平台工具类
 */
@Slf4j
public class PlatformUtils {
    
    public enum Platform {
        WINDOWS,
        MACOS,
        LINUX,
        UNKNOWN
    }
    
    private static final Platform CURRENT_PLATFORM;
    
    static {
        String osName = System.getProperty("os.name").toLowerCase();
        log.info("当前操作系统：{}", osName);
        
        if (osName.contains("mac") || osName.contains("darwin")) {
            CURRENT_PLATFORM = Platform.MACOS;
        } else if (osName.contains("win")) {
            CURRENT_PLATFORM = Platform.WINDOWS;
        } else if (osName.contains("linux")) {
            CURRENT_PLATFORM = Platform.LINUX;
        } else {
            CURRENT_PLATFORM = Platform.UNKNOWN;
        }
        
        log.info("识别为平台：{}", CURRENT_PLATFORM);
    }
    
    /**
     * 获取当前运行平台
     * @return 当前平台
     */
    public static Platform getCurrentPlatform() {
        return CURRENT_PLATFORM;
    }
    
    /**
     * 是否 Windows 平台
     * @return true/false
     */
    public static boolean isWindows() {
        return CURRENT_PLATFORM == Platform.WINDOWS;
    }
    
    /**
     * 是否 MacOS 平台
     * @return true/false
     */
    public static boolean isMacos() {
        return CURRENT_PLATFORM == Platform.MACOS;
    }
    
    /**
     * 是否 Linux 平台
     * @return true/false
     */
    public static boolean isLinux() {
        return CURRENT_PLATFORM == Platform.LINUX;
    }
}
