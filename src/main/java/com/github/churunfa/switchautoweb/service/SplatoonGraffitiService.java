package com.github.churunfa.switchautoweb.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.churunfa.switchautoweb.base.SimpleResponse;
import com.github.churunfa.switchautoweb.combination.graph.CombinationGraphServiceGrpc;
import com.github.churunfa.switchautoweb.vo.SplatoonGraffitiDrawVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import com.google.common.base.Preconditions;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class SplatoonGraffitiService {

    @GrpcClient("cplusplus-server")
    private CombinationGraphServiceGrpc.CombinationGraphServiceBlockingStub combinationGraphServiceStub;

    /**
     * 处理绘制请求
     * @param drawVO 绘制请求对象
     */
    public void draw(SplatoonGraffitiDrawVO drawVO) {
        CombinationGraphVO graph = drawVO.toGraph();
        SimpleResponse simpleResponse = combinationGraphServiceStub.execGraph(CombinationGraphVO.toDTO(graph));
        Preconditions.checkArgument(simpleResponse.getSuccess(), "绘制失败");
    }
}