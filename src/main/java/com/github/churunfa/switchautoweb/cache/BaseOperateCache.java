package com.github.churunfa.switchautoweb.cache;

import com.github.churunfa.switchautoweb.base.operate.BaseOperateServiceGrpc;
import com.github.churunfa.switchautoweb.base.operate.GetAllBaseOperatesResponse;
import com.github.churunfa.switchautoweb.vo.BaseOperateVO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class BaseOperateCache implements InitializingBean {

    @GrpcClient("cplusplus-server")
    private BaseOperateServiceGrpc.BaseOperateServiceBlockingStub baseOperateServiceStub;

    static private BaseOperateServiceGrpc.BaseOperateServiceBlockingStub staticPaseOperateServiceStub;

    @Override
    public void afterPropertiesSet() throws Exception {
        staticPaseOperateServiceStub = baseOperateServiceStub;
    }

    static Cache<String, Map<String, BaseOperateVO>> CACHE_MAP = CacheBuilder.newBuilder()
            .maximumSize(1000) // 设置最大容量
            .expireAfterWrite(600, TimeUnit.MINUTES) // 写入10分钟后过期
            .recordStats() // 开启统计
            .build();

    public static BaseOperateVO getBaseOperate(String ename) {
        try {
            // 使用get方法，第二个参数是Callable，当缓存不存在时会被调用
            Map<String, BaseOperateVO> cache = CACHE_MAP.get("BASE_OPERATE", () -> {
                GetAllBaseOperatesResponse allBaseOperates = staticPaseOperateServiceStub.getAllBaseOperates(Empty.getDefaultInstance());
                List<BaseOperateVO> vos = BaseOperateVO.toVO(allBaseOperates.getOperatesList());
                return vos.stream().collect(Collectors.toMap(BaseOperateVO::getEname, vo -> vo));
            });
            return cache.get(ename);
        } catch (Exception e) {
            // 处理异常情况，例如加载数据失败
            // 可以根据需要记录日志或抛出运行时异常
            throw new RuntimeException("Failed to get base operate: " + ename, e);
        }
    }
}
