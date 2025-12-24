package com.taskmaster.repository;

import com.taskmaster.entity.UserEntity;
import com.taskmaster.common.enums.UserRole;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_works() {
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .email("exists@test.com")
                .password("123")
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@test.com"));
    }
}
