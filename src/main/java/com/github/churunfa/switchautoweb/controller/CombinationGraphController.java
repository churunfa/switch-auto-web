package com.github.churunfa.switchautoweb.controller;

import com.github.churunfa.switchautoweb.service.CombinationGraphService;
import com.github.churunfa.switchautoweb.vo.Msg;
import com.github.churunfa.switchautoweb.vo.combination.AsyncExecStatusVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationVO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/combination-graph")
public class CombinationGraphController {

    private final CombinationGraphService combinationGraphService;

    @GetMapping("all-project")
    public Msg<List<String>> getAllProject() {
        return Msg.success(combinationGraphService.getAllProject());
    }

    @GetMapping("all-combination")
    public Msg<List<CombinationVO>> getAllCombinations(@RequestParam("projectName") String projectName) {
        return Msg.success(combinationGraphService.getAllCombinations(projectName));
    }

    @GetMapping("graph/{id}")
    public Msg<CombinationGraphVO> getGraphById(@PathVariable int id) {
        return Msg.success(combinationGraphService.getGraphById(id));
    }

    @PostMapping("insert")
    public Msg<Void> insertGraph(@RequestBody CombinationGraphVO graphVO) {
        combinationGraphService.insertGraph(graphVO);
        return Msg.success(null);
    }

    @PostMapping("update")
    public Msg<Void> updateGraph(@RequestBody CombinationGraphVO graphVO) {
        combinationGraphService.updateGraph(graphVO);
        return Msg.success(null);
    }

    @DeleteMapping("graph/{id}")
    public Msg<Void> deleteGraphById(@PathVariable int id) {
        combinationGraphService.deleteGraphById(id);
        return Msg.success(null);
    }

    @PostMapping("exec")
    public Msg<Void> execGraph(@RequestBody CombinationGraphVO graphVO) {
        combinationGraphService.execGraph(graphVO);
        return Msg.success(null);
    }

    @PostMapping("exec/{id}")
    public Msg<Void> execGraphById(@PathVariable int id) {
        combinationGraphService.execGraphById(id);
        return Msg.success(null);
    }

    @PostMapping("async-exec/{id}")
    public Msg<Void> asyncExecGraph(@PathVariable int id) {
        combinationGraphService.asyncExecGraph(id);
        return Msg.success(null);
    }

    @PostMapping("stop-async-exec")
    public Msg<Void> stopAsyncExecGraph() {
        combinationGraphService.stopAsyncExecGraph();
        return Msg.success(null);
    }

    @GetMapping("async-exec-info")
    public Msg<AsyncExecStatusVO> getAsyncExecStatus() {
        return Msg.success(combinationGraphService.getAsyncExecStatus());
    }
}
