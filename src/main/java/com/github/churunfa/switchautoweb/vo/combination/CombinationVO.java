package com.github.churunfa.switchautoweb.vo.combination;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.churunfa.switchautoweb.combination.graph.Combination;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CombinationVO {
    @JsonPropertyDescription("组合拓扑图的id，执行时可以留空")
    private Integer id;
    @JsonPropertyDescription("项目名，执行时可以留空")
    private String projectName = "";
    @JsonPropertyDescription("组合拓扑图名称，执行时可以留空")
    private String combinationName = "";
    @JsonPropertyDescription("组合拓扑图描述，执行时可以留空")
    private String desc = "";
    @JsonPropertyDescription("组合拓扑图最小执行时间，执行时可以留空")
    private Integer minTime;
    @JsonPropertyDescription("组合拓扑图是否异步执行中，执行时可以留空")
    private Boolean asyncRunning;

    public static CombinationVO toVO(Combination proto) {
        CombinationVO vo = new CombinationVO();
        vo.setId(proto.getId());
        vo.setProjectName(proto.getProjectName());
        vo.setCombinationName(proto.getCombinationName());
        vo.setDesc(proto.getDesc());
        vo.setMinTime(proto.getMinTime());
        return vo;
    }
    public static List<CombinationVO> toVO(List<Combination> proto) {
        if (CollectionUtils.isEmpty(proto)) {
            return Collections.emptyList();
        }
        return proto.stream().map(CombinationVO::toVO).collect(Collectors.toList());
    }
    public static Combination toDTO(CombinationVO vo) {
        Combination.Builder builder = Combination.newBuilder()
                .setProjectName(vo.getProjectName())
                .setCombinationName(vo.getCombinationName())
                .setDesc(vo.getDesc())
                .setMinTime(vo.getMinTime());
        if (vo.id != null) {
            builder.setId(vo.id);
        }
        return builder.build();
    }
    public static List<Combination> toDTO(List<CombinationVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }
        return vos.stream().map(CombinationVO::toDTO).toList();
    }
}
