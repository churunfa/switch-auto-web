package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.SplatoonGraffitiService;
import com.github.churunfa.switchautoweb.vo.SplatoonGraffitiDrawVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/splatoon-graffiti")
public class SplatoonGraffitiController {

    @Autowired
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
}