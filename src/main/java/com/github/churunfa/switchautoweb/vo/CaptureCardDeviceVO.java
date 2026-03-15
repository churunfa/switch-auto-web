package com.github.churunfa.switchautoweb.vo;

import lombok.Data;

/**
 * 采集卡设备信息 VO
 */
@Data
public class CaptureCardDeviceVO {
    
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
    
    /**
     * 从 BaseCaptureCardService.CaptureDeviceInfo 转换
     */
    public static CaptureCardDeviceVO from(
            com.github.churunfa.switchautoweb.service.BaseCaptureCardService.CaptureDeviceInfo info) {
        if (info == null) {
            return null;
        }
        
        CaptureCardDeviceVO vo = new CaptureCardDeviceVO();
        vo.setDeviceId(info.getDeviceId());
        vo.setDeviceName(info.getDeviceName());
        vo.setAvailable(info.getAvailable());
        vo.setWidth(info.getWidth());
        vo.setHeight(info.getHeight());
        vo.setFramerate(info.getFramerate());
        
        return vo;
    }
}
