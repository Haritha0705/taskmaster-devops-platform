package com.taskmaster.service.impl;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.common.exception.custom.DuplicateResourceException;
import com.taskmaster.common.exception.custom.ResourceNotFoundException;
import com.taskmaster.dto.request.UserCreateRequest;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.entity.UserEntity;
import com.taskmaster.mapper.UserMapper;
import com.taskmaster.repository.UserRepository;
import com.taskmaster.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        UserEntity user = findUserById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        UserEntity user = findUserByEmail(email);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserEntityByEmail(String email) {
        return findUserByEmail(email);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with id: {}", id);

        UserEntity existingUser = findUserById(id);

        // Update fields
        userMapper.updateEntityFromRequest(request, existingUser);

        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", id);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        UserEntity user = findUserById(id);
        user.setIsActive(false); // Soft delete
        userRepository.save(user);

        log.info("User soft-deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination");

        Page<UserEntity> userPage = userRepository.findAll(pageable);
        Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);

        return PagedResponse.of(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);

        Page<UserEntity> userPage = userRepository.searchUsers(searchTerm, pageable);
        Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);

        return PagedResponse.of(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByEmail(email);
    }

    // ================ Helper Methods ================

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
