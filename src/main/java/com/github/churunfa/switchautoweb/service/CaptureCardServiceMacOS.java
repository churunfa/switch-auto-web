package com.github.churunfa.switchautoweb.service;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * MacOS 平台采集卡服务实现
 * 使用 AVFoundation 作为视频捕获后端
 */
@Slf4j
@Service
public class CaptureCardServiceMacOS extends BaseCaptureCardService implements ICaptureCardService {
    
    @Override
    public List<BaseCaptureCardService.CaptureDeviceInfo> getAvailableDevices() {
        log.info("开始获取采集卡设备列表 [macOS]");
        List<BaseCaptureCardService.CaptureDeviceInfo> deviceList = new ArrayList<>();
        
        try {
            // macOS AVFoundation 使用 "设备名：音频设备名" 格式
            // 常见采集卡和摄像头设备名称
            String[] devicePatterns = {
                "OBS Virtual Camera",
                "OBS Virtual Camera:"
            };
            
            int idx = 0;
            for (String devicePattern : devicePatterns) {
                try {
                    log.info("尝试检测设备：{}", devicePattern);
                    FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(devicePattern);
                    grabber.setFormat("avfoundation");
                    // 设置视频帧率，避免自动协商失败
                    grabber.setFrameRate(60.0);
                    grabber.start();
                    
                    BaseCaptureCardService.CaptureDeviceInfo info = new BaseCaptureCardService.CaptureDeviceInfo();
                    info.setDeviceId(idx++);
                    info.setDeviceName(devicePattern.split(":")[0]);
                    info.setAvailable(true);
                    info.setWidth(grabber.getImageWidth());
                    info.setHeight(grabber.getImageHeight());
                    info.setFramerate(grabber.getFrameRate());
                    
                    deviceList.add(info);
                    log.info("发现采集卡设备：{}", info);
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    log.debug("设备 {} 不存在或无法初始化：{}", devicePattern, e.getMessage());
                }
            }
            
            // 如果还是没有找到设备，尝试通过系统 API 列出所有设备
            if (deviceList.isEmpty()) {
                log.info("尝试枚举所有 AVFoundation 设备...");
                // 尝试常见的设备索引前缀
                for (int i = 0; i < 5; i++) {
                    try {
                        String deviceIndex = Integer.toString(i);
                        log.info("尝试设备索引：{}", deviceIndex);
                        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(deviceIndex);
                        grabber.setFormat("avfoundation");
                        grabber.setFrameRate(60.0);
                        grabber.start();
                        
                        BaseCaptureCardService.CaptureDeviceInfo info = new BaseCaptureCardService.CaptureDeviceInfo();
                        info.setDeviceId(idx++);
                        info.setDeviceName("Device " + i);
                        info.setAvailable(true);
                        info.setWidth(grabber.getImageWidth());
                        info.setHeight(grabber.getImageHeight());
                        info.setFramerate(grabber.getFrameRate());
                        
                        deviceList.add(info);
                        log.info("发现采集卡设备 [index]: {}", info);
                        grabber.stop();
                        grabber.release();
                    } catch (Exception e) {
                        log.debug("设备索引 {} 不可用：{}", i, e.getMessage());
                    }
                }
            }
            
            if (!deviceList.isEmpty()) {
                log.info("共发现 {} 个采集卡设备", deviceList.size());
            } else {
                log.warn("未找到任何采集卡设备，请检查：\n1. OBS Virtual Camera 是否已安装\n2. 系统权限设置 -> 安全性与隐私 -> 隐私 -> 相机\n3. 确认 Java 进程有相机访问权限");
            }
            
        } catch (Exception e) {
            log.error("获取采集卡设备列表失败", e);
        }
        
        return deviceList;
    }
    
    @Override
    public boolean connectDevice(int deviceId) {
        log.info("开始连接采集卡设备：deviceId={}", deviceId);
        
        // 检查是否已经连接
        if (getConnectedDevices().contains(deviceId)) {
            log.warn("设备已连接：deviceId={}", deviceId);
            return true;
        }
        
        try {
            // macOS 需要先尝试通过索引连接
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(Integer.toString(deviceId));
            grabber.setFormat("avfoundation");
            grabber.setFrameRate(60.0); // 设置帧率避免协商失败
            grabber.start();
            
            registerDevice(deviceId, grabber);
            return true;
            
        } catch (Exception e) {
            log.error("连接采集卡设备失败：deviceId={}", deviceId, e);
            return false;
        }
    }
    
    @Override
    public boolean connectDeviceByName(String deviceName) {
        log.info("开始连接采集卡设备：deviceName={}", deviceName);
        
        // 使用设备名称作为 key（负数 ID）
        int virtualId = -Math.abs(deviceName.hashCode());
        
        // 检查是否已经连接
        if (getConnectedDevices().contains(virtualId)) {
            log.warn("设备已连接：deviceName={}", deviceName);
            return true;
        }
        
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(deviceName);
            grabber.setFormat("avfoundation"); // macOS AVFoundation
            grabber.setFrameRate(60.0); // 设置帧率
            grabber.start();
            
            registerDevice(virtualId, grabber);
            return true;
            
        } catch (Exception e) {
            log.error("连接采集卡设备失败：deviceName={}", deviceName, e);
            return false;
        }
    }
    
    @Override
    public Frame captureFrame(int deviceId) {
        log.debug("采集画面帧：deviceId={}", deviceId);
        
        CaptureDeviceInstance instance = getDeviceInstance(deviceId);
        if (instance == null) {
            log.error("设备未连接：deviceId={}", deviceId);
            return null;
        }
        
        try {
            Frame frame = instance.getGrabber().grabImage();
            if (frame != null) {
                log.debug("成功采集画面帧：deviceId={}, 尺寸={}x{}", 
                        deviceId, frame.imageWidth, frame.imageHeight);
            } else {
                log.warn("采集到空帧：deviceId={}", deviceId);
            }
            return frame;
        } catch (Exception e) {
            log.error("采集画面帧失败：deviceId={}", deviceId, e);
            return null;
        }
    }
}
