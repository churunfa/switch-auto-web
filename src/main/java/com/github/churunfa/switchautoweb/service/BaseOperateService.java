package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.base.operate.BaseOperateServiceGrpc;
import com.github.churunfa.switchautoweb.base.operate.ExecBaseOperateRequest;
import com.github.churunfa.switchautoweb.base.operate.GetAllBaseOperatesResponse;
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

    public boolean execBaseOperate(int id) {
        ExecBaseOperateRequest request = ExecBaseOperateRequest.newBuilder().setId(id).build();
        return baseOperateServiceStub.execBaseOperate(request).getSuccess();
    }

}
