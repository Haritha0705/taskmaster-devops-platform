package com.taskmaster.repository;

import com.taskmaster.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 * Provides database operations for users
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    /**
     * Find user by email
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Simple search by name or email
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<UserEntity> searchUsers(@Param("term") String term, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.isDeleted = false ")
    Page<UserEntity> findAllActiveUsers(Pageable pageable);

    @Query("SELECT u FROM UserEntity u")
    Page<UserEntity> findAllUsers(Pageable pageable);

    @Query("""
SELECT u FROM UserEntity u
WHERE u.isDeleted = false AND
(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :term, '%'))
 OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :term, '%'))
 OR LOWER(u.email) LIKE LOWER(CONCAT('%', :term, '%')))
""")
    Page<UserEntity> searchActiveUsers(@Param("term") String term, Pageable pageable);

}



