package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.response.DashboardStatsResponse;
import com.backend_core.recruforce2.dto.response.UserResponse;
import com.backend_core.recruforce2.service.DashboardService;
import com.backend_core.recruforce2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Management endpoints for administrators")
public class AdminController {

    private final UserService userService;
  private final DashboardService dashboardService;

    @Operation(summary = "Get all system users")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

  @Operation(summary = "Get global system statistics for dashboard")
  @GetMapping("/dashboard/stats")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
    return ResponseEntity.ok(dashboardService.getGlobalStats());
  }
}
