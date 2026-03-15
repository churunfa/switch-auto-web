package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.ICaptureCardService;
import com.github.churunfa.switchautoweb.service.BaseCaptureCardService;
import com.github.churunfa.switchautoweb.vo.CaptureCardDeviceVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 采集卡画面采集控制器
 */
@RestController
@RequestMapping("/api/capture-card")
@AllArgsConstructor
public class CaptureCardController {

    private final ICaptureCardService captureCardService;

    /**
     * 获取所有可用的采集卡设备列表
     */
    @GetMapping("/devices")
    public Msg<List<CaptureCardDeviceVO>> getAvailableDevices() {
        try {
            List<BaseCaptureCardService.CaptureDeviceInfo> devices = captureCardService.getAvailableDevices();
            List<CaptureCardDeviceVO> deviceVOs = devices.stream()
                    .map(CaptureCardDeviceVO::from)
                    .collect(Collectors.toList());
            return Msg.success(deviceVOs);
        } catch (Exception e) {
            return Msg.fail("获取设备列表失败：" + e.getMessage());
        }
    }

    /**
     * 连接指定的采集卡设备（通过设备名称）
     * 
     * @param deviceName 设备名称
     */
    @PostMapping("/connect")
    public Msg<Boolean> connectDevice(@RequestParam String deviceName) {
        try {
            boolean success = captureCardService.connectDeviceByName(deviceName);
            if (success) {
                return Msg.success(true);
            } else {
                return Msg.fail("连接设备失败");
            }
        } catch (Exception e) {
            return Msg.fail("连接设备异常：" + e.getMessage());
        }
    }
    /**
     * 断开指定的采集卡设备（通过设备名称）
     * 
     * @param deviceName 设备名称
     */
    @PostMapping("/disconnect")
    public Msg<Boolean> disconnectDevice(@RequestParam String deviceName) {
        try {
            boolean success = captureCardService.disconnectDevice(deviceName);
            if (success) {
                return Msg.success(true);
            } else {
                return Msg.fail("设备未连接");
            }
        } catch (Exception e) {
            return Msg.fail("断开设备异常：" + e.getMessage());
        }
    }

    /**
     * 获取采集卡当前画面帧（Base64 编码的 JPEG 图片）
     * 
     * @param deviceName 设备名称
     */
    @GetMapping("/frame")
    public Msg<String> captureFrame(@RequestParam String deviceName) {
        try {
            // 检查设备是否已连接
            if (!captureCardService.isDeviceConnected(deviceName)) {
                return Msg.fail("设备未连接，请先连接设备");
            }
            
            Frame frame = captureCardService.captureFrame(deviceName);
            if (frame == null) {
                return Msg.fail("采集画面失败");
            }
            
            // 使用 Java2DFrameConverter 将 Frame 转换为 BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            java.awt.image.BufferedImage image = converter.getBufferedImage(frame);
            
            // 转换为 Base64 编码的 JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            return Msg.success(base64Image);
        } catch (Exception e) {
            return Msg.fail("采集画面异常：" + e.getMessage());
        }
    }

    /**
     * 获取设备连接状态（通过设备名称）
     * 
     * @param deviceName 设备名称
     */
    @GetMapping("/status")
    public Msg<Boolean> getDeviceStatus(@RequestParam String deviceName) {
        boolean connected = captureCardService.isDeviceConnected(deviceName);
        return Msg.success(connected);
    }

    /**
     * 获取所有已连接的设备名称列表
     */
    @GetMapping("/connected")
    public Msg<List<String>> getConnectedDevices() {
        List<String> connectedNames = captureCardService.getConnectedDevices();
        return Msg.success(connectedNames);
    }

    /**
     * 采集固定分辨率（720x405）的画面帧
     */
    @PostMapping("/capture-frame")
    public Msg<String> captureFrameFixed(@RequestParam String deviceName) {
        try {
            if (deviceName == null || deviceName.isEmpty()) {
                return Msg.fail("设备名称不能为空");
            }
            
            // 检查设备是否已连接
            if (!captureCardService.isDeviceConnected(deviceName)) {
                return Msg.fail("设备未连接，请先连接设备");
            }
            
            Frame frame = captureCardService.captureFrame(deviceName);
            if (frame == null) {
                return Msg.fail("采集画面失败");
            }
            
            // 使用 Java2DFrameConverter 将 Frame 转换为 BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage originalImage = converter.getBufferedImage(frame);
            
            // 缩放到固定分辨率 720x405
            BufferedImage resizedImage = new BufferedImage(720, 405, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(
                    originalImage.getScaledInstance(720, 405, java.awt.Image.SCALE_SMOOTH), 
                    0, 0, null);
            
            // 转换为 Base64 编码的 JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            return Msg.success(base64Image);
        } catch (Exception e) {
            return Msg.fail("采集画面异常：" + e.getMessage());
        }
    }
}
