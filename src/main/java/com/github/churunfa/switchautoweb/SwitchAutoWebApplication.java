package com.github.churunfa.switchautoweb;

import com.github.churunfa.switchautoweb.config.GrpcReflectionHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

/*
x64 Native Tools Command Prompt
gradlew nativeCompile
 */
@SpringBootApplication
@ImportRuntimeHints(GrpcReflectionHints.class)
public class SwitchAutoWebApplication {

    static void main(String[] args) {
        SpringApplication.run(SwitchAutoWebApplication.class, args);
    }

}
