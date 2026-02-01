package com.github.churunfa.switchautoweb.vo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.churunfa.switchautoweb.base.operate.BaseOperate;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
public class BaseOperateVO {
    private Integer id;
    private String ename;
    private String name;
    private Integer paramSize;
    private List<String> paramNames;
    private List<String> initParams;
    private Integer minExecTime;
    private Integer minResetTime;
    private Boolean needReset;

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
