package com.github.churunfa.switchautoweb.vo.combination;

import com.github.churunfa.switchautoweb.combination.graph.GetAsyncExecStatusResponse;
import lombok.Data;

@Data
public class AsyncExecStatusVO {
    private Boolean asyncRunning;
    private Integer graphId;
    private Long asyncStartTime;
    private Long asyncExecCnt;

    public static AsyncExecStatusVO toVO(GetAsyncExecStatusResponse proto) {
        AsyncExecStatusVO vo = new AsyncExecStatusVO();
        vo.setAsyncRunning(proto.getAsyncRunning());
        vo.setGraphId(proto.getGraphId());
        vo.setAsyncStartTime(proto.getAsyncStartTime());
        vo.setAsyncExecCnt(proto.getAsyncExecCnt());
        return vo;
    }

    public static GetAsyncExecStatusResponse toDTO(AsyncExecStatusVO vo) {
        return GetAsyncExecStatusResponse.newBuilder()
                .setAsyncRunning(vo.getAsyncRunning())
                .setGraphId(vo.getGraphId())
                .setAsyncStartTime(vo.getAsyncStartTime())
                .setAsyncExecCnt(vo.getAsyncExecCnt())
                .build();
    }
}
