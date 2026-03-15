package com.github.churunfa.switchautoweb.service.impl;

import com.github.churunfa.switchautoweb.service.BaseCaptureCardService;
import com.github.churunfa.switchautoweb.service.ICaptureCardService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Windows 平台采集卡服务实现
 * 使用 DirectShow 作为视频捕获后端
 */
@Slf4j
@Service
public class CaptureCardServiceWindows extends BaseCaptureCardService implements ICaptureCardService {
    
    @Override
    public List<BaseCaptureCardService.CaptureDeviceInfo> getAvailableDevices() {
        log.info("开始获取采集卡设备列表 [Windows]");
        List<BaseCaptureCardService.CaptureDeviceInfo> deviceList = new ArrayList<>();
        
        try {
            // Windows DirectShow 枚举所有视频输入设备
            log.info("枚举 DirectShow 视频设备...");
            
            // 尝试通过索引 0-9 遍历所有可能的设备
            for (int i = 0; i < 10; i++) {
                FFmpegFrameGrabber grabber = null;
                try {
                    grabber = FFmpegFrameGrabber.createDefault(i);
                    grabber.setFormat("dshow");
                    // 设置固定帧率，避免自动协商问题
                    grabber.setFrameRate(60.0);
                    grabber.start();
                    
                    // 获取实际帧率
                    double framerate = grabber.getFrameRate();
                    // 如果帧率异常，使用设置值
                    if (framerate <= 0 || framerate > 120) {
                        framerate = 60.0;
                    }
                    
                    BaseCaptureCardService.CaptureDeviceInfo info = new BaseCaptureCardService.CaptureDeviceInfo();
                    info.setDeviceId(i);
                    // 使用索引作为设备名称（FFmpeg 不直接提供设备名称）
                    info.setDeviceName("Video Device " + i);
                    info.setAvailable(true);
                    info.setWidth(grabber.getImageWidth());
                    info.setHeight(grabber.getImageHeight());
                    info.setFramerate(framerate);
                    
                    deviceList.add(info);
                    log.info("发现视频设备 [{}]: {}x{}@{}fps", 
                            i, grabber.getImageWidth(), grabber.getImageHeight(), framerate);
                    grabber.stop();
                    grabber.release();
                    
                } catch (Exception e) {
                    // 设备不存在或无法打开，跳过
                    log.debug("设备索引 {} 不可用：{}", i, e.getMessage());
                }
            }
            
            if (!deviceList.isEmpty()) {
                log.info("共发现 {} 个视频设备", deviceList.size());
            } else {
                log.warn("未找到任何视频设备，请检查：\n" +
                        "1. 采集卡是否正确连接到 USB 端口\n" +
                        "2. 设备管理器中是否能看到采集卡设备\n" +
                        "3. 尝试重新插拔采集卡或重启应用");
            }
            
        } catch (Exception e) {
            log.error("获取采集卡设备列表失败", e);
        }
        
        return deviceList;
    }
    
    @Override
    public boolean connectDeviceByName(String deviceName) {
        log.info("开始连接采集卡设备：deviceName={}", deviceName);
        
        // 检查是否已经连接
        if (getConnectedDevices().contains(deviceName)) {
            log.warn("设备已连接：deviceName={}", deviceName);
            return true;
        }
        
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(deviceName);
            grabber.setFormat("dshow"); // Windows DirectShow
            grabber.start();
            
            registerDevice(deviceName, grabber);
            return true;
            
        } catch (Exception e) {
            log.error("连接采集卡设备失败：deviceName={}", deviceName, e);
            return false;
        }
    }
    
    @Override
    public Frame captureFrame(String deviceName) {
        log.debug("采集画面帧：deviceName={}", deviceName);
        
        CaptureDeviceInstance instance = getDeviceInstance(deviceName);
        if (instance == null) {
            log.error("设备未连接：deviceName={}", deviceName);
            return null;
        }
        
        try {
            Frame frame = instance.getGrabber().grabImage();
            if (frame != null) {
                log.debug("成功采集画面帧：deviceName={}, 尺寸={}x{}", 
                        deviceName, frame.imageWidth, frame.imageHeight);
            } else {
                log.warn("采集到空帧：deviceName={}", deviceName);
            }
            return frame;
        } catch (Exception e) {
            log.error("采集画面帧失败：deviceName={}", deviceName, e);
            return null;
        }
    }
}
