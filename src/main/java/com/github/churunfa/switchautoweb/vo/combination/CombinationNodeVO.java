package com.github.churunfa.switchautoweb.vo.combination;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.churunfa.switchautoweb.combination.graph.CombinationNode;
import com.github.churunfa.switchautoweb.vo.BaseOperateVO;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
public class CombinationNodeVO {
    private Integer nodeId;
    private String nodeName;
    private BaseOperateVO baseOperate;
    private List<String> params;
    private Integer execHoldTime;
    private Integer resetHoldTime;
    private Integer loopCnt;
    private Boolean exec;
    private Boolean reset;

    public static CombinationNodeVO toVO(CombinationNode proto) {
        CombinationNodeVO vo = new CombinationNodeVO();
        vo.setNodeId(proto.getNodeId());
        vo.setNodeName(proto.getNodeName());
        vo.setBaseOperate(BaseOperateVO.toVO(proto.getBaseOperate()));
        vo.setParams(JSONArray.parseArray(proto.getParams(), String.class));
        vo.setExecHoldTime(proto.getExecHoldTime());
        vo.setResetHoldTime(proto.getResetHoldTime());
        vo.setLoopCnt(proto.getLoopCnt());
        vo.setExec(proto.getExec());
        vo.setReset(proto.getReset());
        return vo;
    }

    private String defaultName() {
        String name = getBaseOperate().getName();
        boolean exec = Boolean.TRUE.equals(getExec());
        boolean reset = Boolean.TRUE.equals(getReset());
        if (!exec && !reset) {
            return name;
        }
        if (exec && !reset) {
            return name + "(EXEC)";
        }
        if (!exec) {
            return name + "(RESET)";
        }
        return name + "(EXEC/RESET)";
    }

    public static CombinationNode toDTO(CombinationNodeVO vo) {
        CombinationNode.Builder builder = CombinationNode.newBuilder()
                .setBaseOperate(BaseOperateVO.toDTO(vo.getBaseOperate()))
                .setParams(JSONObject.toJSONString(vo.getParams()))
                .setLoopCnt(vo.getLoopCnt());
        if (vo.getNodeId() != null) {
            builder.setNodeId(vo.getNodeId());
        }
        if (StringUtils.isNotBlank(vo.getNodeName())) {
            builder.setNodeName(vo.getNodeName());
        } else {
            builder.setNodeName(vo.defaultName());
        }
        if (vo.getExecHoldTime() != null) {
            builder.setExecHoldTime(vo.getExecHoldTime());
        }
        if (vo.getResetHoldTime() != null) {
            builder.setResetHoldTime(vo.getResetHoldTime());
        } else {
            builder.setResetHoldTime(vo.getBaseOperate().getMinResetTime());
        }
        builder.setExec(vo.getExec() != null && vo.getExec());
        builder.setReset(vo.getReset() != null && vo.getReset());
        return builder.build();
    }

    public static List<CombinationNodeVO> toVO(List<CombinationNode> protos) {
        if (CollectionUtils.isEmpty(protos)) {
            return Collections.emptyList();
        }
        return protos.stream().map(CombinationNodeVO::toVO).toList();
    }

    public static List<CombinationNode> toDTO(List<CombinationNodeVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }
        return vos.stream().map(CombinationNodeVO::toDTO).toList();
    }
}
