package com.github.churunfa.switchautoweb.vo.combination;

import com.github.churunfa.switchautoweb.combination.graph.Combination;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CombinationVO {
    private Integer id;
    private String projectName;
    private String combinationName;
    private String desc;
    private Integer minTime;
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
