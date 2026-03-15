package com.github.churunfa.switchautoweb.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 采集卡服务抽象基类
 * 提供通用的设备管理和连接管理功能
 */
@Slf4j
public abstract class BaseCaptureCardService implements ICaptureCardService {
    
    /**
     * 已连接的设备实例缓存
     * key: deviceName
     * value: CaptureDeviceInstance
     */
    protected final Map<String, CaptureDeviceInstance> connectedDevices = new ConcurrentHashMap<>();
    
    @Override
    public boolean disconnectDevice(String deviceName) {
        log.info("开始断开采集卡设备：deviceName={}", deviceName);
        
        CaptureDeviceInstance instance = connectedDevices.remove(deviceName);
        if (instance == null) {
            log.warn("设备未连接：deviceName={}", deviceName);
            return false;
        }
        
        try {
            if (instance.getGrabber() != null) {
                instance.getGrabber().stop();
                instance.getGrabber().release();
            }
            log.info("成功断开采集卡设备：deviceName={}", deviceName);
            return true;
        } catch (Exception e) {
            log.error("断开采集卡设备失败：deviceName={}", deviceName, e);
            return false;
        }
    }
    
    @Override
    public boolean isDeviceConnected(String deviceName) {
        return connectedDevices.containsKey(deviceName);
    }
    
    @Override
    public List<String> getConnectedDevices() {
        return new ArrayList<>(connectedDevices.keySet());
    }
    
    /**
     * 应用关闭时清理所有连接
     */
    @PreDestroy
    public void destroy() {
        log.info("正在关闭所有采集卡连接...");
        for (String deviceName : connectedDevices.keySet()) {
            disconnectDevice(deviceName);
        }
        log.info("所有采集卡连接已关闭");
    }
    
    /**
     * 注册设备连接
     * @param deviceName 设备名称
     * @param grabber 帧捕获器
     */
    protected void registerDevice(String deviceName, FFmpegFrameGrabber grabber) {
        CaptureDeviceInstance instance = new CaptureDeviceInstance();
        instance.setDeviceId(-1); // 不再使用 ID，设置为 -1
        instance.setDeviceName(deviceName); // 保存设备名称
        instance.setGrabber(grabber);
        instance.setConnected(true);
        instance.setConnectTime(System.currentTimeMillis());
        
        connectedDevices.put(deviceName, instance);
        log.info("设备已注册：deviceName={}, 分辨率={}x{}, 帧率={}", 
                deviceName, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getFrameRate());
    }
    
    /**
     * 获取设备实例
     * @param deviceName 设备名称
     * @return 设备实例
     */
    protected CaptureDeviceInstance getDeviceInstance(String deviceName) {
        return connectedDevices.get(deviceName);
    }
    
    /**
     * 采集卡设备信息
     */
    @Data
    public static class CaptureDeviceInfo {
        /**
         * 设备 ID
         */
        private Integer deviceId;
        
        /**
         * 设备名称
         */
        private String deviceName;
        
        /**
         * 是否可用
         */
        private Boolean available;
        
        /**
         * 画面宽度
         */
        private Integer width;
        
        /**
         * 画面高度
         */
        private Integer height;
        
        /**
         * 帧率
         */
        private Double framerate;
    }
    
    /**
     * 采集卡设备实例
     */
    @Data
    public static class CaptureDeviceInstance {
        /**
         * 设备 ID（已废弃，仅保留兼容性）
         */
        private Integer deviceId;
        
        /**
         * 设备名称
         */
        private String deviceName;
        
        /**
         * 帧捕获器
         */
        private FFmpegFrameGrabber grabber;
        
        /**
         * 是否已连接
         */
        private Boolean connected;
        
        /**
         * 连接时间戳
         */
        private Long connectTime;
    }
}
