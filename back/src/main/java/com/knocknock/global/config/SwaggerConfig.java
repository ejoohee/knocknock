package com.knocknock.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {@Server(url = "http://a508.co.kr/", description = "Default Server URL")
                , @Server(url = "http://localhost:8083/", description = "Develop URL")},
        info = @Info(title = "PetMeeting API 명세서",
                description = "PetMeeting WebService API 명세서",
                version = "v1"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("Knocknock Mobile Service API v1")
                .pathsToMatch(paths)
                .build();
    }

}
