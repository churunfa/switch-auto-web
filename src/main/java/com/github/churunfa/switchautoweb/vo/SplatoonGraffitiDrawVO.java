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

    // 是否重置绘制（true=重置，false=继续当前绘制）
    private boolean reset = false;

    private boolean fastMode = false;
    
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
}