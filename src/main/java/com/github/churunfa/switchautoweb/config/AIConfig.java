package com.github.churunfa.switchautoweb.config;

import com.github.churunfa.switchautoweb.service.CombinationGraphService;
import com.github.churunfa.switchautoweb.service.ICaptureCardService;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import lombok.AllArgsConstructor;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.function.Function;

@Configuration
@AllArgsConstructor
public class AIConfig {

    private final ICaptureCardService captureCardService;

    private final CombinationGraphService combinationGraphService;

    @Bean
    @Description("获取当前游戏画面的最新截图，用于分析实时状况")
    public Function<String, byte[]> captureGameScreen() {
        return deviceName -> {
            try {
                // 1. 采集画面帧
                Frame frame = captureCardService.captureFrame(deviceName);
                if (frame == null) {
                    return null;
                }
                
                // 2. 将 Frame 转换为 BufferedImage
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage originalImage = converter.getBufferedImage(frame);
                    
                    // 3. 缩放到固定分辨率 720x405
                    BufferedImage resizedImage = new BufferedImage(720, 405, BufferedImage.TYPE_INT_RGB);
                    resizedImage.getGraphics().drawImage(
                            originalImage.getScaledInstance(720, 405, Image.SCALE_SMOOTH),
                            0, 0, null
                    );
                    
                    // 4. 转换为 JPEG 格式的 byte 数组
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ImageIO.write(resizedImage, "jpg", baos);
                        return baos.toByteArray();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("截图转换失败：" + e.getMessage(), e);
            }
        };
    }

    @Bean
    @Description("下发组合图的执行指令，对主机进行操作，返回下发是否成功")
    public Function<CombinationGraphVO, Boolean> graphExec() {
        return graph -> {
            try {
                combinationGraphService.execGraph(graph);
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }
}