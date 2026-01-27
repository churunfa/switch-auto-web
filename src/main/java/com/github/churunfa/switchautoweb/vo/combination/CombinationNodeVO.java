package com.github.churunfa.switchautoweb.vo.combination;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.churunfa.switchautoweb.combination.graph.CombinationNode;
import com.github.churunfa.switchautoweb.vo.BaseOperateVO;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
public class CombinationNodeVO {
    private Integer nodeId;
    private BaseOperateVO baseOperate;
    private List<String> params;
    private Integer holdTime;
    private Integer loopCnt;
    private Boolean autoReset;

    public static CombinationNodeVO toVO(CombinationNode proto) {
        CombinationNodeVO vo = new CombinationNodeVO();
        vo.setNodeId(proto.getNodeId());
        vo.setBaseOperate(BaseOperateVO.toVO(proto.getBaseOperate()));
        vo.setParams(JSONArray.parseArray(proto.getParams(), String.class));
        vo.setHoldTime(proto.getHoldTime());
        vo.setLoopCnt(proto.getLoopCnt());
        vo.setAutoReset(proto.getAutoReset());
        return vo;
    }

    public static CombinationNode toDTO(CombinationNodeVO vo) {
        CombinationNode.Builder builder = CombinationNode.newBuilder()
                .setBaseOperate(BaseOperateVO.toDTO(vo.getBaseOperate()))
                .setParams(JSONObject.toJSONString(vo.getParams()))
                .setHoldTime(vo.getHoldTime())
                .setLoopCnt(vo.getLoopCnt());
        if (vo.getNodeId() != null) {
            builder.setNodeId(vo.getNodeId());
        }
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
