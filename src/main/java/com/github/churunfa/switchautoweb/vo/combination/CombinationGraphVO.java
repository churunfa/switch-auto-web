package com.github.churunfa.switchautoweb.vo.combination;

import com.github.churunfa.switchautoweb.combination.graph.Combination;
import com.github.churunfa.switchautoweb.combination.graph.CombinationGraph;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.*;

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

    private static int calcMinTime(CombinationGraphVO vo) {
        Map<Integer, Integer> inCount = Maps.newHashMap();
        Map<Integer, List<Integer>> nextNodeMap = Maps.newHashMap();
        for (CombinationEdgeVO edge : vo.getCombinationEdges()) {
            inCount.put(edge.getNextNodeId(), inCount.getOrDefault(edge.getNextNodeId(), 0) + 1);
            nextNodeMap.computeIfAbsent(edge.getFromNodeId(), k -> Lists.newArrayList()).add(edge.getNextNodeId());
        }

        Queue<CombinationNodeVO> queue = new LinkedList<>();
        Map<Integer, CombinationNodeVO> nodeMap = Maps.newHashMap();
        Map<Integer, Integer> nodeMinRunTimeMap = Maps.newHashMap();
        for (CombinationNodeVO node : vo.getCombinationNodes()) {
            nodeMap.put(node.getNodeId(), node);
            if (node.getBaseOperates().size() == 1 && node.getBaseOperates().get(0).getEname().equals("START_EMPTY")) {
                queue.add(node);
                nodeMinRunTimeMap.put(node.getNodeId(), 0);
            }
        }
        int minTime = 0;
        while (!queue.isEmpty()) {
            CombinationNodeVO curNode = queue.poll();
            minTime = Math.max(minTime, nodeMinRunTimeMap.get(curNode.getNodeId()));
            for (Integer nextNodeId : nextNodeMap.getOrDefault(curNode.getNodeId(), Collections.emptyList())) {
                Integer curNodeTime = nodeMinRunTimeMap.get(curNode.getNodeId());
                if (nodeMinRunTimeMap.containsKey(nextNodeId)) {
                    Integer nextMin = nodeMinRunTimeMap.get(nextNodeId);
                    nextMin = Math.max(nextMin, curNodeTime + curNode.getExecHoldTime() * curNode.getLoopCnt());
                    nodeMinRunTimeMap.put(nextNodeId, nextMin);
                } else {
                    nodeMinRunTimeMap.put(nextNodeId, curNodeTime + curNode.getExecHoldTime() * curNode.getLoopCnt());
                }
                queue.add(nodeMap.get(nextNodeId));
            }
        }
        return minTime;
    }

    public static CombinationGraph toDTO(CombinationGraphVO vo) {
        vo.getCombination().setMinTime(calcMinTime(vo));
        return CombinationGraph.newBuilder()
                .setCombination(CombinationVO.toDTO(vo.getCombination()))
                .addAllCombinationNode(CombinationNodeVO.toDTO(vo.getCombinationNodes()))
                .addAllCombinationEdges(CombinationEdgeVO.toDTO(vo.getCombinationEdges()))
                .build();
    }

}
