package com.taskmaster.service.impl;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.common.exception.custom.ResourceNotFoundException;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.entity.UserEntity;
import com.taskmaster.mapper.UserMapper;
import com.taskmaster.repository.UserRepository;
import com.taskmaster.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return userMapper.toResponse(findUserByEmail(email));
    }

    /**
     * REQUIRED by UserService interface
     */

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        UserEntity user = findUserById(id);
        userMapper.updateEntityFromRequest(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = findUserById(id);

        user.setIsActive(false);
        user.setIsDeleted(true);
        user.softDelete(getCurrentUserId());

        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        UserEntity user = findUserById(id);

        user.restore();
        user.setIsActive(true);
        user.setIsDeleted(false);

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllActiveUsers(Pageable pageable) {
        return PagedResponse.of(
                userRepository.findAllActiveUsers(pageable)
                        .map(userMapper::toResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        return PagedResponse.of(
                userRepository.findAllUsers(pageable)
                        .map(userMapper::toResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> searchUsers(String term, Pageable pageable) {
        return PagedResponse.of(
                userRepository.searchActiveUsers(term, pageable)
                        .map(userMapper::toResponse)
        );
    }

    /**
     * REQUIRED by UserService interface
     */

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return getUserByEmail(getCurrentUserEmail());
    }

    // ================= Helpers =================

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", id)
                );
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "email", email)
                );
    }

    private String getCurrentUserEmail() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Long getCurrentUserId() {
        return findUserByEmail(getCurrentUserEmail()).getId();
    }
}
