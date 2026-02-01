package com.github.churunfa.switchautoweb.vo.combination;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.churunfa.switchautoweb.combination.graph.CombinationNode;
import com.github.churunfa.switchautoweb.vo.BaseOperateVO;
import com.google.common.collect.Lists;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
public class CombinationNodeVO {
    private Integer nodeId;
    private String nodeName;
    private List<BaseOperateVO> baseOperates;
    private List<List<String>> params;
    private Integer execHoldTime;
    private Integer loopCnt;
    private List<Boolean> resets;
    private List<Boolean> autoResets;

    public static CombinationNodeVO toVO(CombinationNode proto) {
        CombinationNodeVO vo = new CombinationNodeVO();
        vo.setNodeId(proto.getNodeId());
        vo.setNodeName(proto.getNodeName());
        vo.setBaseOperates(BaseOperateVO.toVO(proto.getBaseOperatesList()));

        List<String> optParams = JSONArray.parseArray(proto.getParams(), String.class);
        vo.setParams(Lists.newArrayList());
        for (String optParam : optParams) {
            vo.getParams().add(JSONArray.parseArray(optParam, String.class));
        }
        vo.setExecHoldTime(proto.getExecHoldTime());
        vo.setLoopCnt(proto.getLoopCnt());
        vo.setResets(JSONArray.parseArray(proto.getResets(), Boolean.class));
        vo.setAutoResets(JSONArray.parseArray(proto.getAutoResets(), Boolean.class));
        return vo;
    }

    public static CombinationNode toDTO(CombinationNodeVO vo) {
        CombinationNode.Builder builder = CombinationNode.newBuilder()
                .setLoopCnt(vo.getLoopCnt());


        List<String> outParams = Lists.newArrayList();
        for (List<String> voParam : vo.getParams()) {
            outParams.add(JSONObject.toJSONString(voParam));
        }
        builder.setParams(JSONObject.toJSONString(outParams));

        builder.addAllBaseOperates(BaseOperateVO.toDTO(vo.getBaseOperates()));

        if (vo.getNodeId() != null) {
            builder.setNodeId(vo.getNodeId());
        }
        if (StringUtils.isNotBlank(vo.getNodeName())) {
            builder.setNodeName(vo.getNodeName());
        } else {
            builder.setNodeName("");
        }
        if (vo.getExecHoldTime() != null) {
            builder.setExecHoldTime(vo.getExecHoldTime());
        }
        builder.setResets(JSONObject.toJSONString(vo.getResets()));
        builder.setAutoResets(JSONObject.toJSONString(vo.getAutoResets()));
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
