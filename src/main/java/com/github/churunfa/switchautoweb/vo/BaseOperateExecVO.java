package com.github.churunfa.switchautoweb.vo;

import lombok.Data;

import java.util.List;

@Data
public class BaseOperateExecVO {
    private Integer id;
    private Boolean reset;
    private List<Integer> params;
}
