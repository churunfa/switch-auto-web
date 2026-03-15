package com.github.churunfa.switchautoweb.vo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.churunfa.switchautoweb.base.operate.BaseOperate;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
public class BaseOperateVO {
    @JsonPropertyDescription("基础操作id")
    private Integer id = 0;
    @JsonPropertyDescription("基础操作标识，请参考topology-rules.txt文件")
    private String ename;
    @JsonPropertyDescription("基础操作名称，执行时可以留空")
    private String name = "";
    @JsonPropertyDescription("基础操作参数个数，请参考topology-rules.txt文件")
    private Integer paramSize = 0;
    @JsonPropertyDescription("基础操作参数名称，请参考topology-rules.txt文件")
    private List<String> paramNames;
    @JsonPropertyDescription("基础操作初始参数，请参考topology-rules.txt文件")
    private List<String> initParams;
    @JsonPropertyDescription("基础操作最小执行时间")
    private Integer minExecTime = 17;
    @JsonPropertyDescription("基础操作最小重置时间")
    private Integer minResetTime = 17;
    @JsonPropertyDescription("基础操作是否需要重置，如：按下Y键后需要松开，则在这里放入true，表示这是一个需要重置的操作")
    private Boolean needReset = true;

    public static BaseOperateVO toVO(BaseOperate baseOperate) {
        BaseOperateVO vo = new BaseOperateVO();
        vo.setId(baseOperate.getId());
        vo.setEname(baseOperate.getEname());
        vo.setName(baseOperate.getName());
        vo.setParamSize(baseOperate.getParamSize());
        List<String> parseParamNames = JSONArray.parseArray(baseOperate.getParamNames(), String.class);
        vo.setParamNames(parseParamNames);
        List<String> parseInitParams = JSONArray.parseArray(baseOperate.getInitParams(), String.class);
        vo.setInitParams(parseInitParams);
        vo.setMinExecTime(baseOperate.getMinExecTime());
        vo.setMinResetTime(baseOperate.getMinResetTime());
        vo.setNeedReset(baseOperate.getNeedReset());
        return vo;
    }

    public static List<BaseOperateVO> toVO(List<BaseOperate> baseOperates) {
        if (CollectionUtils.isEmpty(baseOperates)) {
            return Collections.emptyList();
        }
        return baseOperates.stream().map(BaseOperateVO::toVO).toList();
    }

    public static BaseOperate toDTO(BaseOperateVO vo) {
        return BaseOperate.newBuilder()
                .setId(vo.getId())
                .setEname(vo.getEname())
                .setName(vo.getName())
                .setParamSize(vo.getParamSize())
                .setParamNames(JSONObject.toJSONString(vo.getParamNames()))
                .setInitParams(JSONObject.toJSONString(vo.getInitParams()))
                .setMinExecTime(vo.getMinExecTime())
                .setMinResetTime(vo.getMinResetTime())
                .setNeedReset(vo.getNeedReset())
                .build();
    }
    public static List<BaseOperate> toDTO(List<BaseOperateVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }
        return vos.stream().map(BaseOperateVO::toDTO).toList();
    }
}
