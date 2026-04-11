package com.taskmaster.repository;

import com.taskmaster.dto.response.UserSummaryResponse;
import com.taskmaster.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndIsDeletedFalseAndIsActiveTrue(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT new com.taskmaster.dto.response.UserSummaryResponse(
                    u.id, u.firstName, u.lastName, u.email, u.role
                )
                FROM UserEntity u
                WHERE u.isDeleted = false
                AND (
                    u.firstName LIKE CONCAT(:term, '%')
                    OR u.lastName LIKE CONCAT(:term, '%')
                    OR u.email LIKE CONCAT(:term, '%')
                    )
    """)
    Page<UserSummaryResponse> searchUsers(@Param("term") String term, Pageable pageable);


    @Query("SELECT u FROM UserEntity u WHERE u.isDeleted = false")
    Page<UserEntity> findAllActiveUsers(Pageable pageable);

    @Query("SELECT u FROM UserEntity u")
    Page<UserEntity> findAllUsers(Pageable pageable);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.isDeleted = false AND
        u.firstName LIKE CONCAT(:term, '%')
        OR u.lastName LIKE CONCAT(:term, '%')
        OR u.email LIKE CONCAT(:term, '%')
    """)
    Page<UserEntity> searchActiveUsers(@Param("term") String term, Pageable pageable);
}



