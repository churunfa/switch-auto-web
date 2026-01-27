package com.github.churunfa.switchautoweb.vo.combination;

import com.github.churunfa.switchautoweb.combination.graph.CombinationGraph;
import lombok.Data;

import java.util.List;

@Data
public class CombinationGraphVO {
    private CombinationVO combination;
    private List<CombinationNodeVO> combinationNodes;
    private List<CombinationEdgeVO> combinationEdges;

    public static CombinationGraphVO toVO(CombinationGraph proto) {
        CombinationGraphVO vo = new CombinationGraphVO();
        vo.setCombination(CombinationVO.toVO(proto.getCombination()));
        vo.setCombinationNodes(CombinationNodeVO.toVO(proto.getCombinationNodeList()));
        vo.setCombinationEdges(CombinationEdgeVO.toVO(proto.getCombinationEdgesList()));
        return vo;
    }

    public static CombinationGraph toDTO(CombinationGraphVO vo) {
        return CombinationGraph.newBuilder()
                .setCombination(CombinationVO.toDTO(vo.getCombination()))
                .addAllCombinationNode(CombinationNodeVO.toDTO(vo.getCombinationNodes()))
                .addAllCombinationEdges(CombinationEdgeVO.toDTO(vo.getCombinationEdges()))
                .build();
    }

}
