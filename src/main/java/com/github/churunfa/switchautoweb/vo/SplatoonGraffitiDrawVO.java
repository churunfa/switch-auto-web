package com.github.churunfa.switchautoweb.vo;

import com.github.churunfa.switchautoweb.vo.combination.CombinationEdgeVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationGraphVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationNodeVO;
import com.github.churunfa.switchautoweb.vo.combination.CombinationVO;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class SplatoonGraffitiDrawVO {

    private final int holdTime = 20;
    
    // 是否重置绘制（true=重置，false=继续当前绘制）
    private boolean reset = false;
    
    // 当前组索引（从0开始）
    private int groupIndex;
    
    // 组大小
    private int groupSize;
    
    // 像素数据数组（0=白色，1=黑色）
    private Byte[] pixelData;
    
    // 总组数
    private int totalGroups;

    // 列数
    private int colCount;
    // 行列数
    private int rowCount;

    /**
     * 根据数字编号获取 X 坐标（对应矩阵的列，水平方向）
     * * @param index 数字编号 (0 到 m*n - 1)
     * @return X 坐标 (即列号)
     */
    private int getX(int index) {
        // 边界检查
        if (index < 0 || index >= groupSize * totalGroups) {
            throw new IndexOutOfBoundsException("编号 " + index + " 超出了矩阵范围");
        }

        int row = index / colCount; // 先计算当前所在行
        int col = index % colCount; // 计算常规的从左到右的列偏移量

        // 如果是偶数行 (0, 2, 4...)，正向排列，直接返回列偏移量
        if (row % 2 == 0) {
            return col;
        }
        // 如果是奇数行 (1, 3, 5...)，反向排列，需要反转列坐标
        else {
            return (colCount - 1) - col;
        }
    }

    /**
     * 根据数字编号获取 Y 坐标（对应矩阵的行，垂直方向）
     * * @param index 数字编号 (0 到 m*n - 1)
     * @return Y 坐标 (即行号)
     */
    private int getY(int index) {
        // 边界检查
        if (index < 0 || index >= groupSize * totalGroups) {
            throw new IndexOutOfBoundsException("编号 " + index + " 超出了矩阵范围");
        }

        // Y坐标不受左右方向影响，直接通过整除列数即可得到
        return index / colCount;
    }

    enum Direction {
        DPAD_UP(14),
        DPAD_DOWN(13),
        DPAD_LEFT(16),
        DPAD_RIGHT(15);
        final int id;

        Direction(int id) {
            this.id = id;
        }
    }

    // 2047 -> 2659/320 = 8.309375 ms/像素点
    // 1024 -> 4850/320 = 15.15625 ms/像素点
    // 512 -> 11700/320 = 36.5625 ms/像素点
    private CombinationNodeVO buildNode(int nodeId, Direction direction, int indexVal, int stepCount) {
        CombinationNodeVO node = new CombinationNodeVO();
        node.setNodeId(nodeId);
        List<BaseOperateVO> baseOperates = Lists.newArrayList();
        List<List<Integer>> params = Lists.newArrayList();
        List<Boolean> resets = Lists.newArrayList();
        List<Boolean> autoResets = Lists.newArrayList();
        int execHoldTime;
        int loopCnt;

        // 是否按下 A 键（indexVal > 0 代表黑色像素，需要按下 A 键）
        BaseOperateVO buttonA = new BaseOperateVO();
        buttonA.setId(4);
        buttonA.setEname("BUTTON_A");
        baseOperates.add(buttonA);
        resets.add(indexVal > 0);
        autoResets.add(false);
        params.add(List.of());

        // 大于5启动遥感禁用方向键，小于等于10使用方向键，禁用遥感
        if (stepCount > 5) {
            BaseOperateVO downBtn = new BaseOperateVO();
            downBtn.setId(Direction.DPAD_DOWN.id);
            downBtn.setEname(Direction.DPAD_DOWN.name());
            baseOperates.add(downBtn);
            resets.add(true);
            autoResets.add(false);
            params.add(List.of());

            BaseOperateVO leftBtn = new BaseOperateVO();
            leftBtn.setId(Direction.DPAD_LEFT.id);
            leftBtn.setEname(Direction.DPAD_LEFT.name());
            baseOperates.add(leftBtn);
            resets.add(true);
            autoResets.add(false);
            params.add(List.of());


            BaseOperateVO rightBtn = new BaseOperateVO();
            rightBtn.setId(Direction.DPAD_RIGHT.id);
            rightBtn.setEname(Direction.DPAD_RIGHT.name());
            baseOperates.add(rightBtn);
            resets.add(true);
            autoResets.add(false);
            params.add(List.of());
        } else {
            BaseOperateVO stick = new BaseOperateVO();
            stick.setId(19);
            stick.setEname("LEFT_STICK");
            baseOperates.add(stick);
            resets.add(true);
            autoResets.add(false);
            params.add(List.of());
        }

        if (stepCount > 50) {
            BaseOperateVO baseOperateVO = new BaseOperateVO();
            baseOperateVO.setId(19);
            baseOperateVO.setEname("LEFT_STICK");
            baseOperates.add(baseOperateVO);
            baseOperateVO.setParamSize(2);
            params.add(Lists.newArrayList(2047 * (direction == Direction.DPAD_LEFT ? -1 : 1), 0));
            resets.add(false);
            autoResets.add(false);
            execHoldTime = (int) Math.round(stepCount * 8.309375);
            loopCnt = 1;
        } else if (stepCount > 5) {
            BaseOperateVO baseOperateVO = new BaseOperateVO();
            baseOperateVO.setId(19);
            baseOperateVO.setEname("LEFT_STICK");
            baseOperates.add(baseOperateVO);
            baseOperateVO.setParamSize(2);
            params.add(Lists.newArrayList(1024 * (direction == Direction.DPAD_LEFT ? -1 : 1), 0));
            resets.add(false);
            autoResets.add(false);
            execHoldTime = (int) Math.round(stepCount * 15.15625);
            loopCnt = 1;
        } else {
            BaseOperateVO baseOperateVO = new BaseOperateVO();
            baseOperateVO.setId(direction.id);
            baseOperateVO.setEname(direction.name());
            baseOperates.add(baseOperateVO);
            resets.add(false);
            autoResets.add(false);
            execHoldTime = 40;
            loopCnt = stepCount;
            params.add(List.of());
        }
        node.setBaseOperates(baseOperates);
        node.setParams(params);
        node.setExecHoldTime(execHoldTime);
        node.setLoopCnt(loopCnt);
        node.setResets(resets);
        node.setAutoResets(autoResets);
        return node;
    }

    public CombinationGraphVO toGraph() {
        CombinationGraphVO combinationGraphVO = new CombinationGraphVO();
        // 基础信息
        combinationGraphVO.setCombination(new CombinationVO());

        List<CombinationNodeVO> combinationNodes = Lists.newArrayList();
        // 判空保护
        if (pixelData == null || pixelData.length == 0) {
            return combinationGraphVO;
        }

        // 第一个像素的绝对编号
        int idx = groupSize * groupIndex;

        // --- 打印逻辑状态初始化 ---
        int prevX = getX(idx);
        int prevY = getY(idx);
        byte currentVal = pixelData[0];
        Direction currentDir = null; // 初始状态还没移动，方向未知
        int stepCount = 1;        // 第1个像素本身算作1步
        int nodeId = 1;
        combinationNodes.add(CombinationNodeVO.buildStartNode());
        nodeId++;

        // 从第2个像素开始遍历（索引1）
        for (int i = 1; i < pixelData.length; i++) {
            int curIdx = idx + i;

            // 当前像素的坐标
            int x = getX(curIdx);
            int y = getY(curIdx);
            byte val = pixelData[i];

            // 1. 计算当前的实际移动方向
            Direction moveDir;
            if (x > prevX) {
                moveDir = Direction.DPAD_RIGHT;
            } else if (x < prevX) {
                moveDir = Direction.DPAD_LEFT;
            } else {
                // x 没变，y 变大了，说明跨行了（蛇形矩阵不会往上走）
                moveDir = Direction.DPAD_DOWN;
            }

            // 如果是循环的第一次移动，用实际移动方向初始化 currentDir
            if (currentDir == null) {
                currentDir = moveDir;
            }

            // 2. 检查状态是否发生变化（值改变 或 方向拐弯）
            if (val != currentVal || !moveDir.equals(currentDir)) {
                // 状态变了，输出前一段累计的数据
                combinationNodes.add(buildNode(nodeId, currentDir, currentVal, stepCount));
                nodeId++;

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
        combinationNodes.add(buildNode(nodeId, currentDir, currentVal, stepCount));


        List<CombinationEdgeVO> combinationEdges = Lists.newArrayList();
        for (int i = 1; i < combinationNodes.size(); i++) {
            CombinationEdgeVO edge = new CombinationEdgeVO();
            edge.setFromNodeId(combinationNodes.get(i - 1).getNodeId());
            edge.setNextNodeId(combinationNodes.get(i).getNodeId());
            combinationEdges.add(edge);
        }

        combinationGraphVO.setCombinationNodes(combinationNodes);
        combinationGraphVO.setCombinationEdges(combinationEdges);
        return combinationGraphVO;
    }
}