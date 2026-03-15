package com.github.churunfa.switchautoweb.service.impl;

import com.github.churunfa.switchautoweb.service.BaseCaptureCardService;
import com.github.churunfa.switchautoweb.service.ICaptureCardService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MacOS 平台采集卡服务实现
 * 使用 AVFoundation 作为视频捕获后端
 */
@Slf4j
@Service
public class CaptureCardServiceMacOS extends BaseCaptureCardService implements ICaptureCardService {
    
    /**
     * 使用 FFmpeg 命令获取 macOS 上的视频设备名称列表
     */
    private Map<Integer, String> getAVFoundationDeviceNames() {
        Map<Integer, String> deviceNames = new HashMap<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-f", "avfoundation", "-list_devices", "true", "-i", "");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            
            process.waitFor();
            String fullOutput = outputBuilder.toString();
            log.debug("FFmpeg 输出:\n{}", fullOutput);
            
            // 解析设备名称
            String[] lines = fullOutput.split("\n");
            boolean inVideoDevices = false;
            int idx = 0;
            
            for (String outputLine : lines) {
                outputLine = outputLine.trim();
                
                if (outputLine.contains("video devices")) {
                    inVideoDevices = true;
                    continue;
                }
                
                if (inVideoDevices) {
                    if (outputLine.contains("audio devices")) {
                        break;
                    }
                    
                    // 匹配 [0] Device Name 格式
                    if (outputLine.matches(".*\\[\\d+\\].*")) {
                        int bracketEnd = outputLine.indexOf("]");
                        if (bracketEnd > 0) {
                            String deviceName = outputLine.substring(bracketEnd + 1).trim();
                            // 去掉可能的前缀 "[0] "
                            deviceName = deviceName.replaceAll("^\\[\\d+\\]\\s*", "").trim();
                            if (!deviceName.isEmpty() && !deviceName.equalsIgnoreCase("None")) {
                                deviceNames.put(idx++, deviceName);
                                log.info("发现设备 [{}]: {}", idx - 1, deviceName);
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("获取 FFmpeg 设备列表失败", e);
        }
        
        return deviceNames;
    }
    
    @Override
    public List<BaseCaptureCardService.CaptureDeviceInfo> getAvailableDevices() {
        log.info("开始获取采集卡设备列表 [macOS]");
        List<BaseCaptureCardService.CaptureDeviceInfo> deviceList = new ArrayList<>();
        
        try {
            // 先通过 FFmpeg 命令获取设备名称
            Map<Integer, String> deviceNameMap = getAVFoundationDeviceNames();
            log.info("枚举 AVFoundation 视频设备...");
            
            // 尝试通过索引 0-9 遍历所有可能的设备
            for (int i = 0; i < 10; i++) {
                FFmpegFrameGrabber grabber = null;
                try {
                    String deviceIndex = Integer.toString(i);
                    grabber = new FFmpegFrameGrabber(deviceIndex);
                    grabber.setFormat("avfoundation");
                    grabber.setFrameRate(60.0);
                    grabber.start();
                    
                    double framerate = grabber.getFrameRate();
                    if (framerate <= 0 || framerate > 120) {
                        framerate = 60.0;
                    }
                    
                    BaseCaptureCardService.CaptureDeviceInfo info = new BaseCaptureCardService.CaptureDeviceInfo();
                    info.setDeviceId(i);
                    // 使用 FFmpeg 提供的真实设备名称
                    info.setDeviceName(deviceNameMap.getOrDefault(i, "Video Device " + i));
                    info.setAvailable(true);
                    info.setWidth(grabber.getImageWidth());
                    info.setHeight(grabber.getImageHeight());
                    info.setFramerate(framerate);
                    
                    deviceList.add(info);
                    log.info("发现视频设备 [{}]: {} - {}x{}@{}fps", 
                            i, info.getDeviceName(), grabber.getImageWidth(), grabber.getImageHeight(), framerate);
                    grabber.stop();
                    grabber.release();
                    
                } catch (Exception e) {
                    log.debug("设备索引 {} 不可用：{}", i, e.getMessage());
                }
            }
            
            if (!deviceList.isEmpty()) {
                log.info("共发现 {} 个视频设备", deviceList.size());
            } else {
                log.warn("未找到任何视频设备，请检查：\n" +
                        "1. 采集卡是否正确连接到 USB/Thunderbolt 端口\n" +
                        "2. 系统偏好设置 -> 安全性与隐私 -> 隐私 -> 相机 -> 允许终端/Java 访问\n" +
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
            grabber.setFormat("avfoundation"); // macOS AVFoundation
            grabber.setFrameRate(60.0); // 设置帧率

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
