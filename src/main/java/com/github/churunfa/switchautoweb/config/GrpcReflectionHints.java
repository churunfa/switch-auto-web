package com.github.churunfa.switchautoweb.config;

import com.github.churunfa.switchautoweb.base.button.ButtonBindingServiceGrpc;
import com.github.churunfa.switchautoweb.combination.graph.CombinationGraphServiceGrpc;
import com.github.churunfa.switchautoweb.service.BaseOperateService;
import com.github.churunfa.switchautoweb.service.ButtonBindingService;
import com.github.churunfa.switchautoweb.service.CombinationGraphService;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import com.github.churunfa.switchautoweb.base.operate.BaseOperateServiceGrpc;

public class GrpcReflectionHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(BaseOperateServiceGrpc.class, MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(ButtonBindingServiceGrpc.class, MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(CombinationGraphServiceGrpc.class, MemberCategory.INVOKE_PUBLIC_METHODS);


        hints.reflection().registerType(BaseOperateService.class, typeHint ->
                typeHint.withField("baseOperateServiceStub")
        );
        hints.reflection().registerType(ButtonBindingService.class, typeHint ->
                typeHint.withField("buttonBindingServiceStub")
        );
        hints.reflection().registerType(CombinationGraphService.class, typeHint ->
                typeHint.withField("combinationGraphServiceStub")
        );
    }
}