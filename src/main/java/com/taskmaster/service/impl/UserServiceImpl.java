package com.taskmaster.service.impl;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.common.exception.custom.ResourceNotFoundException;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserDetailResponse;
import com.taskmaster.dto.response.UserSummaryResponse;
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
    public UserDetailResponse getUserById(Long id) {
        return userMapper.toDetailResponse(findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserByEmail(String email) {
        return userMapper.toDetailResponse(findUserByEmail(email));
    }

    @Override
    public UserDetailResponse updateUser(Long id, UserUpdateRequest request) {
        UserEntity user = findUserById(id);
        userMapper.updateEntityFromRequest(request, user);
        return userMapper.toDetailResponse(userRepository.save(user));
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
    public PagedResponse<UserSummaryResponse> getAllActiveUsers(Pageable pageable) {
        return PagedResponse.of(
                userRepository.findAllActiveUsers(pageable)
                        .map(userMapper::toSummaryResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserSummaryResponse> getAllUsers(Pageable pageable) {
        return PagedResponse.of(
                userRepository.findAllUsers(pageable)
                        .map(userMapper::toSummaryResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserSummaryResponse> searchUsers(String term, Pageable pageable) {
        return PagedResponse.of(
                userRepository.searchActiveUsers(term, pageable)
                        .map(userMapper::toSummaryResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getCurrentUser() {
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
        return userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(email)
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
