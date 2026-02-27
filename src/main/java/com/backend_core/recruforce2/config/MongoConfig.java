package com.backend_core.recruforce2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 * <p>
 * Enables:
 * - MongoDB repositories for the {@code mongo.repository} package
 * - Automatic auditing (@CreatedDate, @LastModifiedDate)
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.backend_core.recruforce2.mongo.repository")
@EnableMongoAuditing
public class MongoConfig {
  // MongoDB connection properties are configured in application.yml
  // This class just enables MongoDB features
}
