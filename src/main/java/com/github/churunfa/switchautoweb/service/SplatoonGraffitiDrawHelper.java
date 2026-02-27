package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.vo.SplatoonGraffitiDrawVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationEdgeVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationNodeVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationVO;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class SplatoonGraffitiDrawHelper {
    private static final int NORMAL_BTN_HOLD_TIME = 50;
    enum Direction {
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT;
    }

    @Data
    static class Line {
        private Direction direction;
        private int indexVal;
        private int stepCount;

        public Line(Direction direction, int indexVal, int stepCount) {
            this.direction = direction;
            this.indexVal = indexVal;
            this.stepCount = stepCount;
        }
    }

    private SplatoonGraffitiDrawVO splatoonGraffitiDrawVO;

    private List<Line> lines;

    public SplatoonGraffitiDrawHelper(SplatoonGraffitiDrawVO splatoonGraffitiDrawVO) {
        this.splatoonGraffitiDrawVO = splatoonGraffitiDrawVO;
        buildLines();
    }

    /**
     * 根据数字编号获取 X 坐标（对应矩阵的列，水平方向）
     * * @param index 数字编号 (0 到 m*n - 1)
     * @return X 坐标 (即列号)
     */
    private int getX(int index) {
        // 边界检查
        if (index < 0 || index >= splatoonGraffitiDrawVO.getGroupSize() * splatoonGraffitiDrawVO.getTotalGroups()) {
            throw new IndexOutOfBoundsException("编号 " + index + " 超出了矩阵范围");
        }

        int row = index / splatoonGraffitiDrawVO.getColCount(); // 先计算当前所在行
        int col = index % splatoonGraffitiDrawVO.getColCount(); // 计算常规的从左到右的列偏移量

        // 如果是偶数行 (0, 2, 4...)，正向排列，直接返回列偏移量
        if (row % 2 == 0) {
            return col;
        }
        // 如果是奇数行 (1, 3, 5...)，反向排列，需要反转列坐标
        else {
            return (splatoonGraffitiDrawVO.getColCount() - 1) - col;
        }
    }

    /**
     * 根据数字编号获取 Y 坐标（对应矩阵的行，垂直方向）
     * * @param index 数字编号 (0 到 m*n - 1)
     * @return Y 坐标 (即行号)
     */
    private int getY(int index) {
        // 边界检查
        if (index < 0 || index >= splatoonGraffitiDrawVO.getGroupSize() * splatoonGraffitiDrawVO.getTotalGroups()) {
            throw new IndexOutOfBoundsException("编号 " + index + " 超出了矩阵范围");
        }

        // Y坐标不受左右方向影响，直接通过整除列数即可得到
        return index / splatoonGraffitiDrawVO.getColCount();
    }

    private Direction getFirstDirection(int idx) {
        if (idx == 0) {
            return Direction.DPAD_RIGHT;
        }
        int x = getX(idx - 1);
        int y = getY(idx - 1);
        if (x == getX(idx)) {
            // 同列，向下
            return Direction.DPAD_DOWN;
        } else if (y == getY(idx) && y % 2 == 0) {
            // 同行，向右
            return Direction.DPAD_RIGHT;
        } else {
            // 否则，向左
            return Direction.DPAD_LEFT;
        }
    }

    private void buildLines() {
        lines = Lists.newArrayList();
        Byte[] pixelData = splatoonGraffitiDrawVO.getPixelData();
        int groupIndex = splatoonGraffitiDrawVO.getGroupIndex();
        int groupSize = splatoonGraffitiDrawVO.getGroupSize();

        // 判空保护
        if (pixelData == null || pixelData.length == 0) {
            return;
        }

        // 第一个像素的绝对编号
        int idx = groupSize * groupIndex;

        // --- 打印逻辑状态初始化 ---
        int prevX = getX(idx);
        int prevY = getY(idx);
        byte currentVal = pixelData[0];
        Direction currentDir = getFirstDirection(idx); // 初始状态还没移动，方向未知
        int stepCount = 1;        // 第1个像素本身算作1步


        // 从第2个像素开始遍历（索引1）
        for (int i = 1; i < pixelData.length; i++) {
            int curIdx = idx + i;

            // 当前像素的坐标
            int x = getX(curIdx);
            int y = getY(curIdx);
            byte val = pixelData[i];

            // 1. 计算当前的实际移动方向
            Direction moveDir;
            if (y > prevY) {
                moveDir = Direction.DPAD_DOWN;
            } else if (x > prevX) {
                moveDir = Direction.DPAD_RIGHT;
            } else {
                moveDir = Direction.DPAD_LEFT;
            }

            // 如果是循环的第一次移动，用实际移动方向初始化 currentDir
            if (currentDir == null) {
                currentDir = moveDir;
            }

            // 2. 检查状态是否发生变化（值改变 或 方向拐弯）
            if (val != currentVal || !moveDir.equals(currentDir)) {
                // 状态变了，输出前一段累计的数据
                lines.add(new Line(currentDir, currentVal, stepCount));

                // 重置状态，开启新的一段
                currentVal = val;
                currentDir = moveDir;
                stepCount = 1; // 当前这个触发变化的像素，作为新段落的第1步
            } else {
                // 状态不变，方向和值一样，步数累加
                stepCount++;
            }

            // 更新 prev 坐标，供下一次循环比较
            prevX = x;
            prevY = y;
        }

        // 3. 处理矩阵结尾的边界情况：打印最后一段未输出的数据
        if (currentDir == null) {
            // 特殊情况：如果当前组只分到了 1 个像素，没发生过任何移动
            // 则根据所在行的奇偶性给一个默认的画笔朝向
            currentDir = (prevY % 2 == 0) ? Direction.DPAD_RIGHT : Direction.DPAD_LEFT;
        }
        lines.add(new Line(currentDir, currentVal, stepCount));
    }

    public CombinationGraphVO buildGraph() {
        CombinationGraphVO combinationGraphVO = new CombinationGraphVO();
        // 基础信息
        combinationGraphVO.setCombination(new CombinationVO());
        List<CombinationNodeVO> nodes = buildNodes();
        combinationGraphVO.setCombinationNodes(nodes);

        List<CombinationEdgeVO> combinationEdges = Lists.newArrayList();
        for (int i = 1; i < nodes.size(); i++) {
            CombinationEdgeVO edge = new CombinationEdgeVO();
            edge.setFromNodeId(nodes.get(i - 1).getNodeId());
            edge.setNextNodeId(nodes.get(i).getNodeId());
            combinationEdges.add(edge);
        }
        combinationGraphVO.setCombinationEdges(combinationEdges);
        return combinationGraphVO;
    }

    private List<CombinationNodeVO> resetCursorNodes() {
        List<CombinationNodeVO> nodes = Lists.newArrayList();
        // 返回左上角
        nodes.add(new CombinationNodeVO("LEFT_STICK", List.of(-2047, 2047), false, false, 4000, 1));
        // 释放按键
        nodes.add(CombinationNodeVO.buildResetNode());

        // 移动到像素点
        int groupIndex = splatoonGraffitiDrawVO.getGroupIndex();
        int groupSize = splatoonGraffitiDrawVO.getGroupSize();
        int preIndex = (groupIndex * groupSize) - 1; // 前一个像素点
        preIndex = Math.max(0, preIndex); // 第一个像素点会越界，直接不要了
        int x = getX(preIndex);
        int y = getY(preIndex);
        // 移动到前一个像素点
        int diff = Math.min(x, y);
        // 1.向右下移动
        CombinationNodeVO moveNode = new CombinationNodeVO();
        moveNode.setExecHoldTime(50);
        moveNode.addBaseOperate(Direction.DPAD_DOWN.name(), List.of(), false, true);
        moveNode.addBaseOperate(Direction.DPAD_RIGHT.name(), List.of(), false, true);
        moveNode.setLoopCnt(diff);
        nodes.add(moveNode);
        // 2.补齐偏移
        CombinationNodeVO fix = new CombinationNodeVO();
        moveNode.setExecHoldTime(50);
        if (x > diff) {
            fix.addBaseOperate(Direction.DPAD_RIGHT.name(), List.of(), false, true);
            fix.setLoopCnt(x - diff);
        } else {
            fix.addBaseOperate(Direction.DPAD_DOWN.name(), List.of(), false, true);
            fix.setLoopCnt(y - diff);
        }
        nodes.add(fix);

        nodes.add(CombinationNodeVO.buildResetNode());
        return nodes;
    }

    private List<CombinationNodeVO> buildNodes() {
        List<CombinationNodeVO> nodes = Lists.newArrayList();
        nodes.add(CombinationNodeVO.buildStartNode());
        if (splatoonGraffitiDrawVO.isReset() && splatoonGraffitiDrawVO.getGroupIndex() > 0) {
            // 重置坐标
            nodes.addAll(resetCursorNodes());
        }
        nodes.addAll(process(splatoonGraffitiDrawVO.isFastMode()));
        nodes.add(CombinationNodeVO.buildResetNode());
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setNodeId(i + 1);
        }
        return nodes;
    }

    // 2047 -> 2659/320 = 8.309375 ms/像素点
    // 1024 -> 4850/320 = 15.15625 ms/像素点
    // 512 -> 11700/320 = 36.5625 ms/像素点
    private CombinationNodeVO buildFastNode(Line line) {
        CombinationNodeVO node = new CombinationNodeVO();

        int execHoldTime;
        int loopCnt;

        // 是否按下 A 键（indexVal > 0 代表黑色像素，需要按下 A 键）
        node.addBaseOperate("BUTTON_A", List.of(), line.getIndexVal() == 0, false);

        // 大于5启动遥感禁用方向键，小于等于10使用方向键，禁用遥感
        if (line.getStepCount() > 10) {
            node.addBaseOperate(Direction.DPAD_DOWN.name(), List.of(), true, false);
            node.addBaseOperate(Direction.DPAD_LEFT.name(), List.of(), true, false);
            node.addBaseOperate(Direction.DPAD_RIGHT.name(), List.of(), true, false);
        } else {
            node.addBaseOperate("LEFT_STICK", List.of(), true, false);
        }

        if (line.getStepCount() > 50) {
            node.addBaseOperate("LEFT_STICK", List.of(2047 * (line.getDirection() == Direction.DPAD_LEFT ? -1 : 1), 0), false, false);
            execHoldTime = (int) Math.round(line.getStepCount() * 8.309375);
            loopCnt = 1;
        } else if (line.getStepCount() > 10) {
            node.addBaseOperate("LEFT_STICK", List.of(1024 * (line.getDirection() == Direction.DPAD_LEFT ? -1 : 1), 0), false, false);
            execHoldTime = (int) Math.round(line.getStepCount() * 15.15625);
            loopCnt = 1;
        } else {
            node.addBaseOperate(line.direction.name(), List.of(), false, true);
            execHoldTime = NORMAL_BTN_HOLD_TIME;
            loopCnt = line.getStepCount();
        }

        node.setExecHoldTime(execHoldTime);
        node.setLoopCnt(loopCnt);
        return node;
    }

    private List<CombinationNodeVO> process(boolean fastMode) {
        List<CombinationNodeVO> nodes = Lists.newArrayList();
        for (Line line : lines) {
            if (fastMode) {
                nodes.add(buildFastNode(line));
            } else {
                nodes.add(buildNormalNode(line));
            }
        }
        return nodes;
    }

    private CombinationNodeVO buildNormalNode(Line line) {
        CombinationNodeVO node = new CombinationNodeVO();
        // 是否按下 A 键（indexVal > 0 代表黑色像素，需要按下 A 键）
        node.addBaseOperate("BUTTON_A", List.of(), line.getIndexVal() == 0, false);
        node.addBaseOperate(line.direction.name(), List.of(), false, true);
        node.setExecHoldTime(NORMAL_BTN_HOLD_TIME);
        node.setLoopCnt(line.getStepCount());
        return node;
    }

}
