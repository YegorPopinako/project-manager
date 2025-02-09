package ua.diploma.projectmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Project API",
                version = "1.0",
                //TODO: add valid credentials
                description = """
                        <a href='http://localhost:8080'>Project Management API</a><br>
                        <p><b>Test credentials:</b><br>
                        - admin@gmail.com / admin (use `/auth/sign-in` for receiving a token)</p>
                        """
        ),
        servers = @Server(url = "http://localhost:8080"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi projectApi() {
        return GroupedOpenApi.builder()
                .group("Projects API")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Auth API")
                .pathsToMatch("/auth/**")
                .build();
    }
}
