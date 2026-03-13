package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.dto.request.RegisterRequest;
import com.backend_core.recruforce2.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for User entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  /**
   * Converts User entity to UserResponse DTO.
   */
  UserResponse toResponse(User user);

  /**
   * Converts RegisterRequest DTO to User entity.
   * Password will be encoded separately in the service.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", constant = "true")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "notificationPreferences", ignore = true)
//  @Mapping(target = "authorities", ignore = true)
  User toEntity(RegisterRequest request);
}
