package com.github.churunfa.switchautoweb.vo;

import com.alibaba.fastjson2.JSONArray;
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
    private Integer minExecTime;
    private Integer minResetTime;

    public static BaseOperateVO toVO(BaseOperate baseOperate) {
        BaseOperateVO vo = new BaseOperateVO();
        vo.setId(baseOperate.getId());
        vo.setEname(baseOperate.getEname());
        vo.setName(baseOperate.getName());
        vo.setParamSize(baseOperate.getParamSize());
        List<String> parseParamNames = JSONArray.parseArray(baseOperate.getParamNames(), String.class);
        vo.setParamNames(parseParamNames);
        vo.setMinExecTime(baseOperate.getMinExecTime());
        vo.setMinResetTime(baseOperate.getMinResetTime());
        return vo;
    }

    public static List<BaseOperateVO> toVO(List<BaseOperate> baseOperates) {
        if (CollectionUtils.isEmpty(baseOperates)) {
            return Collections.emptyList();
        }
        return baseOperates.stream().map(BaseOperateVO::toVO).toList();
    }
}
