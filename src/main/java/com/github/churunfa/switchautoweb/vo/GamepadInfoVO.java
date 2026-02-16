package com.github.churunfa.switchautoweb.vo;

import com.github.churunfa.switchautoweb.base.button.GamepadInfo;
import lombok.Data;

@Data
public class GamepadInfoVO {
    private Boolean connected;
    private String name;
    private String vendorId;
    private String productId;
    private String serialNumber;

    public static GamepadInfoVO toVO(GamepadInfo proto) {
        GamepadInfoVO vo = new GamepadInfoVO();
        vo.setConnected(proto.getConnected());
        vo.setName(proto.getName());
        vo.setVendorId(proto.getVendorId());
        vo.setProductId(proto.getProductId());
        vo.setSerialNumber(proto.getSerialNumber());
        return vo;
    }

    public static GamepadInfo toDTO(GamepadInfoVO vo) {
        return GamepadInfo.newBuilder()
                .setConnected(vo.getConnected())
                .setName(vo.getName())
                .setVendorId(vo.getVendorId())
                .setProductId(vo.getProductId())
                .setSerialNumber(vo.getSerialNumber())
                .build();
    }
}