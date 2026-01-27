package com.github.churunfa.switchautoweb.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.churunfa.switchautoweb.base.operate.BaseOperateServiceGrpc;
import com.github.churunfa.switchautoweb.base.operate.ExecBaseOperateRequest;
import com.github.churunfa.switchautoweb.base.operate.GetAllBaseOperatesResponse;
import com.github.churunfa.switchautoweb.vo.BaseOperateExecVO;
import com.github.churunfa.switchautoweb.vo.BaseOperateVO;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseOperateService {
    @GrpcClient("cplusplus-server")
    private BaseOperateServiceGrpc.BaseOperateServiceBlockingStub baseOperateServiceStub;

    public List<BaseOperateVO> allBaseOperates() {
        GetAllBaseOperatesResponse allBaseOperates = baseOperateServiceStub.getAllBaseOperates(Empty.getDefaultInstance());
        return BaseOperateVO.toVO(allBaseOperates.getOperatesList());
    }

    public boolean execBaseOperate(BaseOperateExecVO vo) {
        ExecBaseOperateRequest request = ExecBaseOperateRequest.newBuilder()
                .setId(vo.getId())
                .setReset(vo.getReset())
                .setParams(JSONObject.toJSONString(vo.getParams()))
                .build();
        return baseOperateServiceStub.execBaseOperate(request).getSuccess();
    }

}
