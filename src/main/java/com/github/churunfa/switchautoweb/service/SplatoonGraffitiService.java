package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.vo.SplatoonGraffitiDrawVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import org.springframework.stereotype.Service;

@Service
public class SplatoonGraffitiService {

    /**
     * 处理绘制请求
     * @param drawVO 绘制请求对象
     */
    public void draw(SplatoonGraffitiDrawVO drawVO) {
        CombinationGraphVO graph = drawVO.toGraph();
        // 模拟处理时间
        try {
            Thread.sleep(50); // 模拟处理延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 实际项目中这里应该调用硬件控制逻辑
        // 例如：通过串口发送指令到 Switch 控制器
    }
}