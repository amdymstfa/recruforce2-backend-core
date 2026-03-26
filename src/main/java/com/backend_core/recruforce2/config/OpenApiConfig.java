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

@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name:RecruForce2}")
  private String applicationName;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(apiInfo())
      .servers(apiServers())
      .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
      .components(new Components()
        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
  }

  private Info apiInfo() {
    return new Info()
      .title("RecruForce2 - API Gateway & Core Service")
      .description(
        "### Système de Gestion de Recrutement Intelligent\n\n" +
          "Cette API gère le cœur du système RecruForce2, incluant :\n" +
          "- **Authentification sécurisée** via JWT.\n" +
          "- **Gestion du pipeline** : Offres d'emploi, Candidatures, Entretiens.\n" +
          "- **Module de Feedback** : Évaluation post-entretien par les recruteurs.\n" +
          "- **Intégration IA** : Scoring de matching et prédictions de succès (via FastAPI AI Service).\n" +
          "- **Administration** : Audit logs, statistiques dashboard et gestion des utilisateurs.")
      .version("1.1.0")
      .contact(new Contact()
        .name("RecruForce2 Support")
        .email("tech@recruforce2.com")
        .url("https://recruforce2.com"))
      .license(new License()
        .name("Proprietary License")
        .url("https://recruforce2.com/license"));
  }

  private List<Server> apiServers() {
    Server devServer = new Server()
      .url("http://localhost:8080")
      .description("Serveur de Développement (Local)");

    Server aiServer = new Server()
      .url("http://localhost:8000")
      .description("Service IA (FastAPI)");

    Server prodServer = new Server()
      .url("https://api.recruforce2.com")
      .description("Serveur de Production");

    return List.of(devServer, aiServer, prodServer);
  }

  private SecurityScheme createSecurityScheme() {
    return new SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")
      .bearerFormat("JWT")
      .in(SecurityScheme.In.HEADER)
      .name("Authorization")
      .description("Collez votre token JWT ici (sans le préfixe 'Bearer ')");
  }
}
