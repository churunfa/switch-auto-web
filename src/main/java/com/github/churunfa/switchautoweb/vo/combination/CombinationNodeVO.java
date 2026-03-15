package com.github.churunfa.switchautoweb.vo.combination;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.churunfa.switchautoweb.cache.BaseOperateCache;
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
    @JsonPropertyDescription("节点id，执行时需要保证id唯一且不变")
    private Integer nodeId = 0;
    @JsonPropertyDescription("节点名称，执行时可以留空")
    private String nodeName = "";
    @JsonPropertyDescription("节点对应的操作列表，里面所有的操作会同时执行")
    private List<BaseOperateVO> baseOperates;
    @JsonPropertyDescription("节点每个操作对应的参数列表，参数列表长度必须与操作列表长度一致")
    private List<List<Integer>> params;
    @JsonPropertyDescription("节点执行时间，单位为毫秒。执行完操作后需要等待一段时间，等待操作生效")
    private Integer execHoldTime = 50;
    @JsonPropertyDescription("节点循环执行的次数")
    private Integer loopCnt = 1;
    @JsonPropertyDescription("节点对应的操作是否是重置操作。如：想松开Y键，需要在baseOperates中放入Y键对应的操作，并在resets中放入true，表示这是一个重置操作")
    private List<Boolean> resets;
    @JsonPropertyDescription("节点对应的操作是否需要自动重置。如：按下Y键后需要自动松开，则在autoResets中放入true，表示需要自动重置；执行时，会在execHoldTime时间执行一半时自动执行一次重置操作")
    private List<Boolean> autoResets;

    public static CombinationNodeVO toVO(CombinationNode proto) {
        CombinationNodeVO vo = new CombinationNodeVO();
        vo.setNodeId(proto.getNodeId());
        vo.setNodeName(proto.getNodeName());
        vo.setBaseOperates(BaseOperateVO.toVO(proto.getBaseOperatesList()));

        List<String> optParams = JSONArray.parseArray(proto.getParams(), String.class);
        vo.setParams(Lists.newArrayList());
        for (String optParam : optParams) {
            vo.getParams().add(JSONArray.parseArray(optParam, Integer.class));
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

        if (vo.getParams().size() != vo.getBaseOperates().size()) {
            throw new IllegalArgumentException("参数列表长度必须与基础操作列表长度一致");
        }
        if (vo.getParams().size() != vo.getResets().size() || vo.getParams().size() != vo.getAutoResets().size()) {
            throw new IllegalArgumentException("参数列表长度必须与重置列表长度一致");
        }

        List<String> outParams = Lists.newArrayList();
        for (List<Integer> voParam : vo.getParams()) {
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

    public static CombinationNodeVO buildStartNode() {
        CombinationNodeVO combinationNodeVO = new CombinationNodeVO();
        combinationNodeVO.setNodeName("开始");
        combinationNodeVO.addBaseOperate("START_EMPTY", List.of(), false, false);
        combinationNodeVO.setExecHoldTime(0);
        return combinationNodeVO;
    }

    public static CombinationNodeVO buildResetNode() {
        CombinationNodeVO combinationNodeVO = new CombinationNodeVO();
        combinationNodeVO.setNodeName("重置");
        combinationNodeVO.addBaseOperate("RESET_ALL", List.of(), false, false);
        combinationNodeVO.setExecHoldTime(0);
        return combinationNodeVO;
    }

    public void addBaseOperate(String ename, List<Integer> param, boolean reset, boolean autoReset) {
        if (baseOperates == null) {
            baseOperates = Lists.newArrayList();
        }
        if (params == null) {
            params = Lists.newArrayList();
        }
        if (resets == null) {
            resets = Lists.newArrayList();
        }
        if (autoResets == null) {
            autoResets = Lists.newArrayList();
        }
        baseOperates.add(BaseOperateCache.getBaseOperate(ename));
        params.add(param);
        resets.add(reset);
        autoResets.add(autoReset);
    }

    public CombinationNodeVO() {
    }

    public CombinationNodeVO(String ename, List<Integer> param, boolean reset, boolean autoReset, int execHoldTime, int loopCnt) {
        addBaseOperate(ename, param, reset, autoReset);
        this.execHoldTime = execHoldTime;
        this.loopCnt = loopCnt;
    }
}
