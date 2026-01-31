package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.base.SimpleResponse;
import com.github.churunfa.switchautoweb.combination.graph.*;
import com.github.churunfa.switchautoweb.vo.combination.AsyncExecStatusVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationVO;
import com.google.common.base.Preconditions;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombinationGraphService {
    @GrpcClient("cplusplus-server")
    private CombinationGraphServiceGrpc.CombinationGraphServiceBlockingStub combinationGraphServiceStub;

    public List<String> getAllProject() {
        GetAllProjectResponse allProject = combinationGraphServiceStub.getAllProject(Empty.newBuilder().build());
        return allProject.getProjectNamesList();
    }

    public List<CombinationVO> getAllCombinations(String projectName) {
        GetAllGraphResponse response = combinationGraphServiceStub.getAllGraph(StringValue.newBuilder().setValue(projectName).build());
        return CombinationVO.toVO(response.getCombinationsList());
    }

    public CombinationGraphVO getGraphById(int id) {
        CombinationGraph combinationGraph = combinationGraphServiceStub.getGraphById(IntValue.newBuilder().setValue(id).build());
        return CombinationGraphVO.toVO(combinationGraph);
    }

    public void insertGraph(CombinationGraphVO combinationGraphVO) {
        CombinationGraph combinationGraph = CombinationGraphVO.toDTO(combinationGraphVO);
        SimpleResponse simpleResponse = combinationGraphServiceStub.insertGraph(combinationGraph);
        Preconditions.checkArgument(simpleResponse.getSuccess(), "保存失败");
    }

    public void updateGraph(CombinationGraphVO combinationGraphVO) {
        CombinationGraph combinationGraph = CombinationGraphVO.toDTO(combinationGraphVO);
        SimpleResponse simpleResponse = combinationGraphServiceStub.updateGraph(combinationGraph);
        Preconditions.checkArgument(simpleResponse.getSuccess(), "保存失败");
    }

    public void deleteGraphById(int id) {
        SimpleResponse simpleResponse = combinationGraphServiceStub.deleteGraph(IntValue.newBuilder().setValue(id).build());
        Preconditions.checkArgument(simpleResponse.getSuccess(), "删除失败");
    }

    public void execGraph(CombinationGraphVO combinationGraphVO) {
        SimpleResponse simpleResponse = combinationGraphServiceStub.execGraph(CombinationGraphVO.toDTO(combinationGraphVO));
        Preconditions.checkArgument(simpleResponse.getSuccess(), "执行失败");
    }
    public void execGraphById(int id) {
        SimpleResponse simpleResponse = combinationGraphServiceStub.execGraphById(IntValue.newBuilder().setValue(id).build());
        Preconditions.checkArgument(simpleResponse.getSuccess(), "执行失败");
    }

    public void asyncExecGraph(int id) {
        SimpleResponse simpleResponse = combinationGraphServiceStub.asyncExecGraph(IntValue.newBuilder().setValue(id).build());
        Preconditions.checkArgument(simpleResponse.getSuccess(), "执行失败");
    }

    public void stopAsyncExecGraph() {
        SimpleResponse simpleResponse = combinationGraphServiceStub.stopAsyncExecGraph(Empty.newBuilder().build());
        Preconditions.checkArgument(simpleResponse.getSuccess(), "执行失败");
    }

    public AsyncExecStatusVO getAsyncExecStatus() {
        GetAsyncExecStatusResponse asyncExecStatus = combinationGraphServiceStub.getAsyncExecStatus(Empty.newBuilder().build());
        return AsyncExecStatusVO.toVO(asyncExecStatus);
    }
}
