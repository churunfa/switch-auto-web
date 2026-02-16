package com.github.churunfa.switchautoweb.vo;

import com.github.churunfa.switchautoweb.base.button.ButtonBinding;
import com.github.churunfa.switchautoweb.base.button.SdlButton;
import com.github.churunfa.switchautoweb.base.button.SwitchButtonType;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ButtonBindingVO {
    private Integer id;
    private SdlButton sdlBtn;
    private String sdlBtnName;
    private SwitchButtonType buttonType;
    private String buttonName;
    private Boolean functionKey;
    private Integer graphId;

    public static ButtonBindingVO toVO(ButtonBinding proto) {
        ButtonBindingVO vo = new ButtonBindingVO();
        vo.setId(proto.getId());
        vo.setSdlBtn(proto.getSdlBtn());
        vo.setSdlBtnName(proto.getSdlBtnName());
        vo.setButtonType(proto.getButtonType());
        vo.setButtonName(proto.getButtonName());
        vo.setFunctionKey(proto.getFunctionKey());
        vo.setGraphId(proto.getGraphId());
        return vo;
    }

    public static List<ButtonBindingVO> toVO(List<ButtonBinding> protos) {
        if (protos == null || protos.isEmpty()) {
            return Collections.emptyList();
        }
        return protos.stream().map(ButtonBindingVO::toVO).collect(Collectors.toList());
    }

    public static ButtonBinding toDTO(ButtonBindingVO vo) {
        return ButtonBinding.newBuilder()
                .setId(vo.getId())
                .setSdlBtn(vo.getSdlBtn())
                .setSdlBtnName(vo.getSdlBtnName())
                .setButtonType(vo.getButtonType())
                .setButtonName(vo.getButtonName())
                .setFunctionKey(vo.getFunctionKey())
                .setGraphId(vo.getGraphId())
                .build();
    }

    public static List<ButtonBinding> toDTO(List<ButtonBindingVO> vos) {
        if (vos == null || vos.isEmpty()) {
            return Collections.emptyList();
        }
        return vos.stream().map(ButtonBindingVO::toDTO).collect(Collectors.toList());
    }
}