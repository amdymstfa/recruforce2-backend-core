package com.backend_core.recruforce2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger UI configuration.
 * <p>
 * Accessible at: http://localhost:8080/swagger-ui.html
 * <p>
 * Provides interactive API documentation with JWT authentication support.
 */
@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name}")
  private String applicationName;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(apiInfo())
      .servers(apiServers())
      .addSecurityItem(securityRequirement())
      .components(securityComponents());
  }

  /**
   * API metadata — title, description, version, contact, license
   */
  private Info apiInfo() {
    return new Info()
      .title("RecruForce2 API")
      .description(
        "Intelligent recruitment platform API providing endpoints for:\n" +
          "- User authentication (JWT)\n" +
          "- Job offer management\n" +
          "- Candidate profile management\n" +
          "- Application tracking with AI matching scores\n" +
          "- Interview scheduling and feedback\n" +
          "- Notifications and alerts\n" +
          "- AI-powered prediction module"
      )
      .version("1.0.0")
      .contact(apiContact())
      .license(apiLicense());
  }

  /**
   * Contact information
   */
  private Contact apiContact() {
    return new Contact()
      .name("RecruForce2 Team")
      .email("support@recruforce2.com")
      .url("https://recruforce2.com");
  }

  /**
   * License information
   */
  private License apiLicense() {
    return new License()
      .name("Proprietary")
      .url("https://recruforce2.com/license");
  }

  /**
   * API server URLs (dev, prod)
   */
  private List<Server> apiServers() {
    Server devServer = new Server()
      .url("http://localhost:8080")
      .description("Development server");

    Server prodServer = new Server()
      .url("https://api.recruforce2.com")
      .description("Production server");

    return List.of(devServer, prodServer);
  }

  /**
   * Security requirement — all endpoints require JWT authentication
   */
  private SecurityRequirement securityRequirement() {
    return new SecurityRequirement().addList("Bearer Authentication");
  }

  /**
   * Security components — JWT Bearer token scheme
   */
  private Components securityComponents() {
    return new Components()
      .addSecuritySchemes("Bearer Authentication", securityScheme());
  }

  /**
   * JWT Bearer token security scheme
   */
  private SecurityScheme securityScheme() {
    return new SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")
      .bearerFormat("JWT")
      .in(SecurityScheme.In.HEADER)
      .name("Authorization")
      .description("Enter JWT token in the format: Bearer {token}");
  }
}
