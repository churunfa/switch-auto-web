package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.BaseOperateService;
import com.github.churunfa.switchautoweb.vo.BaseOperateExecVO;
import com.github.churunfa.switchautoweb.vo.BaseOperateVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/base-operate")
public class BaseOperateController {

    private final BaseOperateService baseOperateService;

    @GetMapping("all-base-operates")
    public Msg<List<BaseOperateVO>> allBaseOperates() {
        return Msg.success(baseOperateService.allBaseOperates());
    }

    @PostMapping("exec-base-operate")
    public Msg<Boolean> execBaseOperate(@RequestBody BaseOperateExecVO baseOperateExecVO) {
        boolean success = baseOperateService.execBaseOperate(baseOperateExecVO);
        if (success) {
            return Msg.success(true);
        } else {
            return Msg.fail("执行失败");
        }
    }
}
