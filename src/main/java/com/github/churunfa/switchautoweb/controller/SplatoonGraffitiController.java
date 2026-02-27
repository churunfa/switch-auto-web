package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.SplatoonGraffitiService;
import com.github.churunfa.switchautoweb.utils.SplatoonImageProcessor;
import com.github.churunfa.switchautoweb.vo.SplatoonGraffitiDrawVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Base64;

@RestController
@RequestMapping("/api/splatoon-graffiti")
@AllArgsConstructor
public class SplatoonGraffitiController {

    private SplatoonGraffitiService splatoonGraffitiService;

    @PostMapping("/draw")
    public Msg<String> draw(@RequestBody SplatoonGraffitiDrawVO request) {
        try {
            // 调用服务层处理绘制逻辑
            splatoonGraffitiService.draw(request);
            return Msg.success("绘制成功");
        } catch (Exception e) {
            return Msg.fail(e.getMessage());
        }
    }

    @PostMapping("/img/process")
    public Msg<String> processImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Msg.fail("文件不能为空");
        }

        try (InputStream is = file.getInputStream()) {
            byte[] bitmap = SplatoonImageProcessor.process(is);

            // 将 0/1 数组转换为 Base64 字符串
            String bitmapBase64 = Base64.getEncoder().encodeToString(bitmap);

            return Msg.success(bitmapBase64);
        } catch (Exception e) {
            e.printStackTrace();
            return Msg.fail("处理失败: " + e.getMessage());
        }
    }
}