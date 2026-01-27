package com.github.churunfa.switchautoweb.vo.combination;

import com.github.churunfa.switchautoweb.combination.graph.CombinationEdge;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
public class CombinationEdgeVO {
    private Integer edgeId;
    private Integer fromNodeId;
    private Integer nextNodeId;

    public static CombinationEdgeVO toVO(CombinationEdge proto) {
        CombinationEdgeVO vo = new CombinationEdgeVO();
        vo.setEdgeId(proto.getEdgeId());
        vo.setFromNodeId(proto.getFromNodeId());
        vo.setNextNodeId(proto.getNextNodeId());
        return vo;
    }

    public static CombinationEdge toDTO(CombinationEdgeVO vo) {
        CombinationEdge.Builder builder = CombinationEdge.newBuilder()
                .setFromNodeId(vo.getFromNodeId())
                .setNextNodeId(vo.getNextNodeId());
        if (vo.getEdgeId() != null) {
            builder.setEdgeId(vo.getEdgeId());
        }
        return builder.build();
    }

    public static List<CombinationEdgeVO> toVO(List<CombinationEdge> protos) {
        if (CollectionUtils.isEmpty(protos)) {
            return Collections.emptyList();
        }
        return protos.stream().map(CombinationEdgeVO::toVO).toList();
    }

    public static List<CombinationEdge> toDTO(List<CombinationEdgeVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }
        return vos.stream().map(CombinationEdgeVO::toDTO).toList();
    }
}
