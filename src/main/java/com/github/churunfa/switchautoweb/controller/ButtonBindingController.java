package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.ButtonBindingService;
import com.github.churunfa.switchautoweb.vo.ButtonBindingVO;
import com.github.churunfa.switchautoweb.vo.GamepadInfoVO;
import com.github.churunfa.switchautoweb.vo.Msg;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/button-binding")
public class ButtonBindingController {

    private final ButtonBindingService buttonBindingService;

    @GetMapping("all-bindings")
    public Msg<List<ButtonBindingVO>> getAllButtonBindings() {
        return Msg.success(buttonBindingService.getAllButtonBindings());
    }

    @PostMapping("update")
    public Msg<Void> updateButtonBinding(@RequestBody ButtonBindingVO buttonBindingVO) {
        buttonBindingService.updateButtonBinding(buttonBindingVO);
        return Msg.success(null);
    }

    @PostMapping("set-function-key")
    public Msg<Void> setFunctionKey(@RequestParam Integer id, @RequestParam Boolean functionKey) {
        buttonBindingService.setFunctionKey(id, functionKey);
        return Msg.success(null);
    }

    @PostMapping("bind-graph")
    public Msg<Void> bindGraphToButton(@RequestParam Integer id, @RequestParam Integer graphId) {
        buttonBindingService.bindGraphToButton(id, graphId);
        return Msg.success(null);
    }

    @PostMapping("unbind-graph")
    public Msg<Void> unbindGraphFromButton(@RequestParam Integer id) {
        buttonBindingService.unbindGraphFromButton(id);
        return Msg.success(null);
    }

    @GetMapping("connected-gamepad")
    public Msg<GamepadInfoVO> getConnectedGamepadInfo() {
        return Msg.success(buttonBindingService.getConnectedGamepadInfo());
    }

    @GetMapping("all-gamepads")
    public Msg<List<GamepadInfoVO>> getAllGamepadsInfo() {
        return Msg.success(buttonBindingService.getAllGamepadsInfo());
    }
}