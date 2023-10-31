package com.knocknock.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

//@OpenAPIDefinition(
//        servers = {
//                @Server(url = "http://a508.co.kr/", description = "Default Server URL")
//                , @Server(url = "http://localhost:8083/", description = "Develop URL")},
//        info = @Info(title = "PetMeeting API 명세서",
//                description = "PetMeeting WebService API 명세서",
//                version = "v1"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

//    @Bean
//    public GroupedOpenApi chatOpenApi() {
//        String[] paths = {"/**"};
//
//        return GroupedOpenApi.builder()
//                .group("Knocknock Mobile Service API v1")
//                .pathsToMatch(paths)
//                .build();
//    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.knocknock"))
                .paths(PathSelectors.ant("/api/**")).build();

    }

    // JWT SecurityContext 구성
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("Authorization", authorizationScopes));
    }

    // ApiKey 정의
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

}
