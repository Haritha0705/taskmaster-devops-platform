package com.taskmaster.repository;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import com.taskmaster.entity.TaskEntity;
import com.taskmaster.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Page<TaskEntity> findByCreatedBy(UserEntity user, Pageable pageable);

    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id")
    Optional<TaskEntity> findByIdIncludingDeleted(@Param("id") Long id);


    @Query("""
        SELECT t FROM TaskEntity t
        WHERE (:status IS NULL OR t.status = :status)
          AND (:priority IS NULL OR t.priority = :priority)
    """)
    Page<TaskEntity> filterTasks(@Param("status") TaskStatus status,
                                 @Param("priority") TaskPriority priority,
                                 Pageable pageable);

    @Query("""
        SELECT t FROM TaskEntity t
        WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(t.description) LIKE LOWER(CONCAT('%', :term, '%')))
    """)
    Page<TaskEntity> searchTasks(@Param("term") String term, Pageable pageable);

    // For current user
    @Query("""
        SELECT t FROM TaskEntity t
        WHERE t.createdBy.id = :userId
          AND (:status IS NULL OR t.status = :status)
          AND (:priority IS NULL OR t.priority = :priority)
    """)
    Page<TaskEntity> filterTasksForUser(@Param("status") TaskStatus status,
                                        @Param("priority") TaskPriority priority,
                                        @Param("userId") Long userId,
                                        Pageable pageable);

    @Query("""
        SELECT t FROM TaskEntity t
        WHERE t.createdBy.id = :userId
          AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(t.description) LIKE LOWER(CONCAT('%', :term, '%')))
    """)
    Page<TaskEntity> searchTasksForUser(@Param("term") String term,
                                        @Param("userId") Long userId,
                                        Pageable pageable);
}
