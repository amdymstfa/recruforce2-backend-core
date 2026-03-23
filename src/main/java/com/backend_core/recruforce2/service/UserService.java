package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.dto.response.UserResponse;
import com.backend_core.recruforce2.mapper.UserMapper;
import com.backend_core.recruforce2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, User updateData) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(updateData.getFirstName());
        user.setLastName(updateData.getLastName());
        user.setEmail(updateData.getEmail());
        user.setPhone(updateData.getPhone());
        user.setRole(updateData.getRole());

      if (updateData.getIsActive() != null) {
        user.setIsActive(updateData.getIsActive());
      }

      return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false); // Utilisation du setter généré par Lombok pour isActive
        userRepository.save(user);
    }
}
