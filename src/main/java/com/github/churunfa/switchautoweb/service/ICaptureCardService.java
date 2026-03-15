package com.github.churunfa.switchautoweb.service;

import java.util.List;

/**
 * 采集卡服务接口
 * 定义采集卡设备管理的基本操作
 */
public interface ICaptureCardService {
    
    /**
     * 获取所有可用的采集卡设备列表
     * @return 设备信息列表
     */
    List<BaseCaptureCardService.CaptureDeviceInfo> getAvailableDevices();

    /**
     * 通过设备名称连接采集卡设备
     * @param deviceName 设备名称
     * @return 是否连接成功
     */
    boolean connectDeviceByName(String deviceName);
    
    /**
     * 断开指定的采集卡设备
     * @param deviceName 设备名称
     * @return 是否断开成功
     */
    boolean disconnectDevice(String deviceName);
    
    /**
     * 获取采集卡当前画面帧
     * @param deviceName 设备名称
     * @return 画面帧数据，如果获取失败则返回 null
     */
    org.bytedeco.javacv.Frame captureFrame(String deviceName);
    
    /**
     * 检查设备是否已连接
     * @param deviceName 设备名称
     * @return 是否已连接
     */
    boolean isDeviceConnected(String deviceName);
    
    /**
     * 获取所有已连接的设备
     * @return 已连接的设备名称列表
     */
    List<String> getConnectedDevices();
}
