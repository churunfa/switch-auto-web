package com.github.churunfa.switchautoweb.utils;

import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConfigLength;
import boofcv.struct.image.GrayU8;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class SplatoonImageProcessor {

    public static final int TARGET_WIDTH = 320;
    public static final int TARGET_HEIGHT = 120;

    public static byte[] process(InputStream inputStream) throws Exception {
        // 1. 加载图片
        BufferedImage buffered = ImageIO.read(inputStream);
        if (buffered == null) throw new IllegalArgumentException("Image null");

        // 2. 预处理：缩放并填充居中 (使用 AWT 高质量缩放)
        BufferedImage canvas = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, TARGET_WIDTH, TARGET_HEIGHT);

        double scale = Math.min((double) TARGET_WIDTH / buffered.getWidth(), (double) TARGET_HEIGHT / buffered.getHeight());
        int w = (int) (buffered.getWidth() * scale);
        int h = (int) (buffered.getHeight() * scale);
        g.drawImage(buffered, (TARGET_WIDTH - w) / 2, (TARGET_HEIGHT - h) / 2, w, h, null);
        g.dispose();

        // 3. 转换为 BoofCV 格式
        GrayU8 gray = ConvertBufferedImage.convertFrom(canvas, (GrayU8) null);
        GrayU8 binary = new GrayU8(TARGET_WIDTH, TARGET_HEIGHT);

        // 4. 【核心】局部自适应阈值 (Local Block Thresholding)
        // 参数说明：
        // radius: 20 (检查周围 20 像素，值越大越平滑)
        // scale: 0.95 (阈值系数，调低这个值会让更多灰色变黑，防止线变点)
        // down: true (由于背景是白的，我们需要把低于阈值的设为黑)
        ConfigLength width = ConfigLength.fixed(20);
        GThresholdImageOps.localMean(gray, binary, width, 0.95, true, null, null, null);

        // 5. 【可选】形态学操作：加粗线条
        // 如果线还是太细，可以取消下面这行的注释，执行一次“腐蚀”
        // GrayU8 dilated = BinaryImageOps.erode8(binary, 1, null);
        // binary = dilated;

        // 6. 转换为 0/1 数组输出
        byte[] bitmap = new byte[TARGET_WIDTH * TARGET_HEIGHT];
        for (int y = 0; y < TARGET_HEIGHT; y++) {
            for (int x = 0; x < TARGET_WIDTH; x++) {
                // BoofCV 的 binary 图像：1 是物体(黑线), 0 是背景(白)
                // 这正好符合你的斯普拉遁 1/0 逻辑
                bitmap[y * TARGET_WIDTH + x] = (byte) binary.get(x, y);
            }
        }

        return bitmap;
    }
}