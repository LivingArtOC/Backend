package livart.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi shopApi() {
        return GroupedOpenApi.builder()
                .group("shop-api")
                .pathsToMatch("/api/shop/**")
                .build();
    }

    @Bean
    public GroupedOpenApi erpApi() {
        return GroupedOpenApi.builder()
                .group("erp-api")
                .pathsToMatch("/api/erp/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ArtLiving Project")
                        .version("1.0")
                        .description("가구 쇼핑몰 플랫폼 API 문서"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 환경"),
                        new Server().url("http://13.209.88.181:8080").description("개발 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}
