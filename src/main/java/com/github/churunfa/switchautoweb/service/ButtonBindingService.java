package com.github.churunfa.switchautoweb.service;

import com.github.churunfa.switchautoweb.base.SimpleResponse;
import com.github.churunfa.switchautoweb.base.button.*;
import com.github.churunfa.switchautoweb.vo.ButtonBindingVO;
import com.github.churunfa.switchautoweb.vo.GamepadInfoVO;
import com.google.common.base.Preconditions;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ButtonBindingService {

    @GrpcClient("cplusplus-server")
    private ButtonBindingServiceGrpc.ButtonBindingServiceBlockingStub buttonBindingServiceStub;

    public List<ButtonBindingVO> getAllButtonBindings() {
        GetAllButtonBindingsResponse response = buttonBindingServiceStub.getAllButtonBindings(Empty.getDefaultInstance());
        return ButtonBindingVO.toVO(response.getBindingsList());
    }

    public void updateButtonBinding(ButtonBindingVO vo) {
        UpdateButtonBindingRequest request = UpdateButtonBindingRequest.newBuilder()
                .setId(vo.getId())
                .setSdlBtn(vo.getSdlBtn())
                .setButtonType(vo.getButtonType())
                .build();
        SimpleResponse response = buttonBindingServiceStub.updateButtonBinding(request);
        Preconditions.checkArgument(response.getSuccess(), "更新按钮绑定失败");
    }

    public void setFunctionKey(Integer id, Boolean functionKey) {
        SetFunctionKeyRequest request = SetFunctionKeyRequest.newBuilder()
                .setId(id)
                .setFunctionKey(functionKey)
                .build();
        SimpleResponse response = buttonBindingServiceStub.setFunctionKey(request);
        Preconditions.checkArgument(response.getSuccess(), "设置功能键失败");
    }

    public void bindGraphToButton(Integer id, Integer graphId) {
        BindGraphToButtonRequest request = BindGraphToButtonRequest.newBuilder()
                .setId(id)
                .setGraphId(graphId)
                .build();
        SimpleResponse response = buttonBindingServiceStub.bindGraphToButton(request);
        Preconditions.checkArgument(response.getSuccess(), "绑定组合图到按钮失败");
    }

    public void unbindGraphFromButton(Integer id) {
        UnbindGraphFromButtonRequest request = UnbindGraphFromButtonRequest.newBuilder()
                .setId(id)
                .build();
        SimpleResponse response = buttonBindingServiceStub.unbindGraphFromButton(request);
        Preconditions.checkArgument(response.getSuccess(), "解除按钮绑定失败");
    }

    public GamepadInfoVO getConnectedGamepadInfo() {
        GamepadInfoResponse response = buttonBindingServiceStub.getConnectedGamepadInfo(Empty.getDefaultInstance());
        Preconditions.checkArgument(response.getSuccess(), "获取连接的手柄信息失败: " + response.getErrorMessage());
        return GamepadInfoVO.toVO(response.getGamepadInfo());
    }

    public List<GamepadInfoVO> getAllGamepadsInfo() {
        AllGamepadsResponse response = buttonBindingServiceStub.getAllGamepadsInfo(Empty.getDefaultInstance());
        Preconditions.checkArgument(response.getSuccess(), "获取所有手柄信息失败: " + response.getErrorMessage());
        return response.getGamepadsList().stream()
                .map(GamepadInfoVO::toVO)
                .collect(Collectors.toList());
    }
}