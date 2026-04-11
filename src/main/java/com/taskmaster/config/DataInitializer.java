package com.taskmaster.config;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import com.taskmaster.common.enums.UserRole;
import com.taskmaster.entity.TaskEntity;
import com.taskmaster.entity.UserEntity;
import com.taskmaster.repository.TaskRepository;
import com.taskmaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * Data Initializer Configuration
 * Loads dummy data for all test scenarios when the application starts
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository, TaskRepository taskRepository) {
        return args -> {
            log.info("========================================");
            log.info("Starting Database Initialization...");
            log.info("========================================");

            // Check if data already exists
            if (userRepository.count() > 0) {
                log.info("Database already populated. Skipping initialization.");
                return;
            }

            // Initialize Users
            log.info("Initializing Users...");
            UserEntity adminUser = createAdminUser(userRepository);
            UserEntity user1 = createUser1(userRepository);
            UserEntity user2 = createUser2(userRepository);
            UserEntity user3 = createUser3(userRepository);
            UserEntity inactiveUser = createInactiveUser(userRepository);

            log.info("Created {} users successfully", userRepository.count());

            // Initialize Tasks
            log.info("Initializing Tasks...");
            createTasksForAdminUser(taskRepository, adminUser);
            createTasksForUser1(taskRepository, user1);
            createTasksForUser2(taskRepository, user2);
            createTasksForUser3(taskRepository, user3);
            createDeletedTasks(taskRepository, user1);

            log.info("Created {} tasks successfully", taskRepository.count());

            log.info("========================================");
            log.info("Database Initialization Completed!");
            log.info("========================================");
            logInitializedData(userRepository, taskRepository);
        };
    }

    /**
     * Create Admin User
     */
    private UserEntity createAdminUser(UserRepository userRepository) {
        UserEntity admin = UserEntity.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@taskmaster.com")
                .password(passwordEncoder.encode("Admin@123"))
                .phone("+91-9876543210")
                .address("New Delhi, India")
                .role(UserRole.ROLE_ADMIN)
                .isEmailVerified(true)
                .isAccountLocked(false)
                .isActive(true)
                .isDeleted(false)
                .build();

        UserEntity saved = userRepository.save(admin);
        log.info("✓ Created Admin User: {}", saved.getEmail());
        return saved;
    }

    /**
     * Create Regular User 1 - Active with multiple tasks
     */
    private UserEntity createUser1(UserRepository userRepository) {
        UserEntity user = UserEntity.builder()
                .firstName("John")
                .lastName("Developer")
                .email("john.dev@taskmaster.com")
                .password(passwordEncoder.encode("User@123"))
                .phone("+91-9876543211")
                .address("Bangalore, India")
                .role(UserRole.ROLE_USER)
                .isEmailVerified(true)
                .isAccountLocked(false)
                .isActive(true)
                .isDeleted(false)
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("✓ Created User 1: {}", saved.getEmail());
        return saved;
    }

    /**
     * Create Regular User 2 - Active with different tasks
     */
    private UserEntity createUser2(UserRepository userRepository) {
        UserEntity user = UserEntity.builder()
                .firstName("Sarah")
                .lastName("Manager")
                .email("sarah.manager@taskmaster.com")
                .password(passwordEncoder.encode("User@123"))
                .phone("+91-9876543212")
                .address("Mumbai, India")
                .role(UserRole.ROLE_USER)
                .isEmailVerified(true)
                .isAccountLocked(false)
                .isActive(true)
                .isDeleted(false)
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("✓ Created User 2: {}", saved.getEmail());
        return saved;
    }

    /**
     * Create Regular User 3 - Active with minimal tasks
     */
    private UserEntity createUser3(UserRepository userRepository) {
        UserEntity user = UserEntity.builder()
                .firstName("Michael")
                .lastName("Designer")
                .email("michael.designer@taskmaster.com")
                .password(passwordEncoder.encode("User@123"))
                .phone("+91-9876543213")
                .address("Pune, India")
                .role(UserRole.ROLE_USER)
                .isEmailVerified(false)
                .isAccountLocked(false)
                .isActive(true)
                .isDeleted(false)
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("✓ Created User 3: {}", saved.getEmail());
        return saved;
    }

    /**
     * Create Inactive User - For testing disabled account scenarios
     */
    private UserEntity createInactiveUser(UserRepository userRepository) {
        UserEntity user = UserEntity.builder()
                .firstName("Inactive")
                .lastName("User")
                .email("inactive@taskmaster.com")
                .password(passwordEncoder.encode("User@123"))
                .phone("+91-9876543214")
                .address("Delhi, India")
                .role(UserRole.ROLE_USER)
                .isEmailVerified(false)
                .isAccountLocked(true)
                .isActive(false)
                .isDeleted(false)
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("✓ Created Inactive User: {}", saved.getEmail());
        return saved;
    }

    /**
     * Create Tasks for Admin User
     */
    private void createTasksForAdminUser(TaskRepository taskRepository, UserEntity adminUser) {
        // Task 1: High Priority - In Progress
        TaskEntity task1 = TaskEntity.builder()
                .title("System Setup and Configuration")
                .description("Set up the TaskMaster system infrastructure, configure servers, install dependencies, and validate all configurations.")
                .priority(TaskPriority.TASK_HIGH)
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(3))
                .location("Server Room")
                .remarks("Critical for deployment")
                .isActive(true)
                .isDeleted(false)
                .createdBy(adminUser)
                .updatedBy(adminUser)
                .build();
        taskRepository.save(task1);
        log.info("  ✓ Admin Task 1: {}", task1.getTitle());

        // Task 2: Medium Priority - Completed
        TaskEntity task2 = TaskEntity.builder()
                .title("Database Migration")
                .description("Migrate existing user data from legacy system to new TaskMaster database.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.COMPLETED)
                .dueDate(LocalDateTime.now().minusDays(2))
                .location("Database Server")
                .remarks("Completed successfully with zero data loss")
                .isActive(true)
                .isDeleted(false)
                .createdBy(adminUser)
                .updatedBy(adminUser)
                .build();
        taskRepository.save(task2);
        log.info("  ✓ Admin Task 2: {}", task2.getTitle());

        // Task 3: Low Priority - To Do
        TaskEntity task3 = TaskEntity.builder()
                .title("Documentation Update")
                .description("Update system documentation with new features and API changes.")
                .priority(TaskPriority.TASK_LOW)
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(10))
                .location("Documentation Server")
                .remarks("Can be done in parallel with other tasks")
                .isActive(true)
                .isDeleted(false)
                .createdBy(adminUser)
                .updatedBy(adminUser)
                .build();
        taskRepository.save(task3);
        log.info("  ✓ Admin Task 3: {}", task3.getTitle());
    }

    /**
     * Create Tasks for User 1 (John Developer)
     */
    private void createTasksForUser1(TaskRepository taskRepository, UserEntity user1) {
        // Task 1: Backend API Development
        TaskEntity task1 = TaskEntity.builder()
                .title("Develop REST API Endpoints")
                .description("Create REST API endpoints for task management including CRUD operations, filtering, and pagination.")
                .priority(TaskPriority.TASK_HIGH)
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(2))
                .location("Development Environment")
                .remarks("Using Spring Boot 3.2.5 and MySQL")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(task1);
        log.info("  ✓ User1 Task 1: {}", task1.getTitle());

        // Task 2: Unit Testing
        TaskEntity task2 = TaskEntity.builder()
                .title("Write Unit Tests for Services")
                .description("Write comprehensive unit tests for all service classes with at least 80% code coverage.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(4))
                .location("Development Environment")
                .remarks("Using JUnit 5 and Mockito")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(task2);
        log.info("  ✓ User1 Task 2: {}", task2.getTitle());

        // Task 3: Bug Fix
        TaskEntity task3 = TaskEntity.builder()
                .title("Fix Authentication Bug")
                .description("Fix the JWT token expiration bug that's causing users to be logged out prematurely.")
                .priority(TaskPriority.TASK_HIGH)
                .status(TaskStatus.BLOCKED)
                .dueDate(LocalDateTime.now().plusDays(1))
                .location("Bug Tracker")
                .remarks("Blocked waiting for security team review")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(task3);
        log.info("  ✓ User1 Task 3: {}", task3.getTitle());

        // Task 4: Code Review - Completed
        TaskEntity task4 = TaskEntity.builder()
                .title("Review Pull Requests")
                .description("Review and approve pull requests from team members.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.COMPLETED)
                .dueDate(LocalDateTime.now().minusDays(1))
                .location("GitHub")
                .remarks("4 PRs reviewed and approved")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(task4);
        log.info("  ✓ User1 Task 4: {}", task4.getTitle());

        // Task 5: To Do - Low Priority
        TaskEntity task5 = TaskEntity.builder()
                .title("Setup CI/CD Pipeline")
                .description("Configure GitHub Actions for automated testing and deployment.")
                .priority(TaskPriority.TASK_LOW)
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(7))
                .location("GitHub Actions")
                .remarks("Nice to have for automation")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(task5);
        log.info("  ✓ User1 Task 5: {}", task5.getTitle());
    }

    /**
     * Create Tasks for User 2 (Sarah Manager)
     */
    private void createTasksForUser2(TaskRepository taskRepository, UserEntity user2) {
        // Task 1: Project Planning
        TaskEntity task1 = TaskEntity.builder()
                .title("Q2 Project Planning")
                .description("Plan and organize Q2 projects, allocate resources, and set milestones.")
                .priority(TaskPriority.TASK_HIGH)
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(5))
                .location("Conference Room A")
                .remarks("Meeting scheduled for Friday")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user2)
                .updatedBy(user2)
                .build();
        taskRepository.save(task1);
        log.info("  ✓ User2 Task 1: {}", task1.getTitle());

        // Task 2: Team Meeting
        TaskEntity task2 = TaskEntity.builder()
                .title("Weekly Team Standup")
                .description("Conduct weekly standup meeting with the team to track progress and discuss blockers.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.COMPLETED)
                .dueDate(LocalDateTime.now().minusHours(2))
                .location("Virtual - Zoom")
                .remarks("All team members attended")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user2)
                .updatedBy(user2)
                .build();
        taskRepository.save(task2);
        log.info("  ✓ User2 Task 2: {}", task2.getTitle());

        // Task 3: Performance Review
        TaskEntity task3 = TaskEntity.builder()
                .title("Conduct Performance Reviews")
                .description("Complete performance review for all team members and provide feedback.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(8))
                .location("Manager Office")
                .remarks("15 employees to review")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user2)
                .updatedBy(user2)
                .build();
        taskRepository.save(task3);
        log.info("  ✓ User2 Task 3: {}", task3.getTitle());

        // Task 4: Budget Review
        TaskEntity task4 = TaskEntity.builder()
                .title("Review Department Budget")
                .description("Review and finalize the department budget for next quarter.")
                .priority(TaskPriority.TASK_HIGH)
                .status(TaskStatus.BLOCKED)
                .dueDate(LocalDateTime.now().plusDays(2))
                .location("Finance Department")
                .remarks("Waiting for finance team data")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user2)
                .updatedBy(user2)
                .build();
        taskRepository.save(task4);
        log.info("  ✓ User2 Task 4: {}", task4.getTitle());
    }

    /**
     * Create Tasks for User 3 (Michael Designer)
     */
    private void createTasksForUser3(TaskRepository taskRepository, UserEntity user3) {
        // Task 1: UI Design
        TaskEntity task1 = TaskEntity.builder()
                .title("Design Dashboard UI")
                .description("Create wireframes and high-fidelity mockups for the admin dashboard.")
                .priority(TaskPriority.TASK_HIGH)
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().plusDays(6))
                .location("Design Tool - Figma")
                .remarks("Need client approval before development")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user3)
                .updatedBy(user3)
                .build();
        taskRepository.save(task1);
        log.info("  ✓ User3 Task 1: {}", task1.getTitle());

        // Task 2: Branding Guidelines
        TaskEntity task2 = TaskEntity.builder()
                .title("Update Branding Guidelines")
                .description("Update the company branding guidelines with new logo and color palette.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDateTime.now().plusDays(4))
                .location("Brand Portal")
                .remarks("Marketing team waiting for this")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user3)
                .updatedBy(user3)
                .build();
        taskRepository.save(task2);
        log.info("  ✓ User3 Task 2: {}", task2.getTitle());

        // Task 3: Logo Design - Completed
        TaskEntity task3 = TaskEntity.builder()
                .title("Design New Logo Variations")
                .description("Create multiple variations of the new company logo.")
                .priority(TaskPriority.TASK_LOW)
                .status(TaskStatus.COMPLETED)
                .dueDate(LocalDateTime.now().minusDays(5))
                .location("Design Tool - Adobe Illustrator")
                .remarks("5 variations created and approved")
                .isActive(true)
                .isDeleted(false)
                .createdBy(user3)
                .updatedBy(user3)
                .build();
        taskRepository.save(task3);
        log.info("  ✓ User3 Task 3: {}", task3.getTitle());
    }

    /**
     * Create Deleted Tasks (for testing soft delete functionality)
     */
    private void createDeletedTasks(TaskRepository taskRepository, UserEntity user1) {
        // Deleted Task 1
        TaskEntity deletedTask1 = TaskEntity.builder()
                .title("Deprecated Task 1")
                .description("This task has been marked as deleted and should not appear in normal queries.")
                .priority(TaskPriority.TASK_LOW)
                .status(TaskStatus.TODO)
                .dueDate(LocalDateTime.now().minusDays(30))
                .location("Archive")
                .remarks("Deleted for testing purposes")
                .isActive(false)
                .isDeleted(true)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(deletedTask1);
        log.info("  ✓ Deleted Task 1: {}", deletedTask1.getTitle());

        // Deleted Task 2
        TaskEntity deletedTask2 = TaskEntity.builder()
                .title("Deprecated Task 2")
                .description("Another deleted task for testing soft delete functionality.")
                .priority(TaskPriority.TASK_MEDIUM)
                .status(TaskStatus.COMPLETED)
                .dueDate(LocalDateTime.now().minusDays(45))
                .location("Archive")
                .remarks("Soft deleted")
                .isActive(false)
                .isDeleted(true)
                .createdBy(user1)
                .updatedBy(user1)
                .build();
        taskRepository.save(deletedTask2);
        log.info("  ✓ Deleted Task 2: {}", deletedTask2.getTitle());
    }

    /**
     * Log summary of initialized data
     */
    private void logInitializedData(UserRepository userRepository, TaskRepository taskRepository) {
        log.info("\n========== DATABASE SUMMARY ==========");
        log.info("Total Users: {}", userRepository.count());
        log.info("Total Tasks: {}", taskRepository.count());
        log.info("======================================\n");

        log.info("TEST CREDENTIALS:");
        log.info("────────────────────────────────────────");
        log.info("Admin User:");
        log.info("  Email: admin@taskmaster.com");
        log.info("  Password: Admin@123");
        log.info("\nRegular Users:");
        log.info("  Email: john.dev@taskmaster.com | Password: User@123");
        log.info("  Email: sarah.manager@taskmaster.com | Password: User@123");
        log.info("  Email: michael.designer@taskmaster.com | Password: User@123");
        log.info("  Email: inactive@taskmaster.com | Password: User@123 (Account Locked)");
        log.info("────────────────────────────────────────\n");
    }
}

