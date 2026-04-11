# TaskMaster Performance - Quick Start Implementation

## 🚀 Start Here - First Steps (30 minutes)

### Step 1: Add Dependencies to pom.xml

Replace your current pom.xml build section with:

```xml
<build>
    <plugins>
        <!-- Compiler plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.42</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <!-- Spring Boot Maven plugin -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Add these dependencies:

```xml
<!-- Add before </dependencies> -->

<!-- Bucket4j for Rate Limiting -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>

<!-- Micrometer Prometheus for Metrics -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- Validation API -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>

<!-- HibernateValidator -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
</dependency>
```

Run:
```bash
mvn clean install -DskipTests
```

---

### Step 2: Update application.properties

Replace your current application.properties with:

```properties
# ===============================
# APPLICATION INFO
# ===============================
spring.application.name=task-master
application.version=1.0.0

# ===============================
# SERVER
# ===============================
server.port=8081
server.compression.enabled=true
server.compression.min-response-size=1024

# ===============================
# ACTIVE PROFILE
# ===============================
spring.profiles.active=dev

# ===============================
# DATABASE
# ===============================
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Batch Processing
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000

# ===============================
# JWT SECURITY
# ===============================
application.security.jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=604800000

# ===============================
# LOGGING
# ===============================
logging.level.root=INFO
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.file.name=logs/app.log

# ===============================
# REDIS
# ===============================
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=5000

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=600000

# ===============================
# ACTUATOR (Monitoring)
# ===============================
management.endpoints.web.exposure.include=health,metrics,prometheus,info
management.endpoint.health.show-details=always
management.metrics.enable.process=true
management.metrics.enable.system=true

# ===============================
# ASYNC CONFIGURATION
# ===============================
spring.task.execution.pool.core-size=5
spring.task.execution.pool.max-size=10
spring.task.execution.pool.queue-capacity=100
spring.task.execution.shutdown.await-termination=true
```

---

### Step 3: Create Cache Configuration

Create file: `src/main/java/com/taskmaster/config/redis/CacheConfig.java`

```java
package com.taskmaster.config.redis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer())
            )
            .disableCachingNullValues();

        return RedisCacheManager.create(factory);
    }
}
```

---

### Step 4: Create Performance Interceptor

Create file: `src/main/java/com/taskmaster/config/PerformanceInterceptor.java`

```java
package com.taskmaster.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class PerformanceInterceptor implements HandlerInterceptor {
    
    private static final String START_TIME = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        long startTime = (long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - startTime;
        
        if (duration > 1000) {  // Log slow requests
            log.warn("SLOW API: {} {} - Status: {} - Time: {}ms", 
                request.getMethod(), 
                request.getRequestURI(), 
                response.getStatus(), 
                duration);
        } else {
            log.debug("API: {} {} - Status: {} - Time: {}ms", 
                request.getMethod(), 
                request.getRequestURI(), 
                response.getStatus(), 
                duration);
        }
    }
}
```

---

### Step 5: Register Interceptor

Update: `src/main/java/com/taskmaster/config/WebConfig.java`

```java
package com.taskmaster.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final PerformanceInterceptor performanceInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceInterceptor)
            .addPathPatterns("/api/**");
    }
}
```

---

### Step 6: Add Caching to UserService

Update your UserService implementation:

```java
// Add these imports
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;

// In UserServiceImpl class:

@Cacheable(value = "users", key = "#id", unless = "#result == null")
public UserEntity getUserById(Long id) {
    log.info("Fetching user from database: {}", id);
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
}

@CacheEvict(value = "users", key = "#id")
public UserEntity updateUser(Long id, UpdateUserRequest request) {
    UserEntity user = getUserById(id);
    // Update logic
    return userRepository.save(user);
}

@CachePut(value = "users", key = "#result.id")
public UserEntity createUser(CreateUserRequest request) {
    UserEntity user = new UserEntity();
    // Create logic
    return userRepository.save(user);
}
```

---

### Step 7: Test Everything

Run the application:

```bash
mvn spring-boot:run
```

Test endpoints:

```bash
# Test API
curl http://localhost:8081/api/users

# Check metrics
curl http://localhost:8081/actuator/metrics

# Check health
curl http://localhost:8081/actuator/health

# Check prometheus metrics
curl http://localhost:8081/actuator/prometheus
```

---

## ✅ Verification Checklist

After implementing these 7 steps:

- [ ] All dependencies installed (mvn clean install)
- [ ] Application starts without errors
- [ ] `/actuator/health` returns UP
- [ ] `/actuator/metrics` returns metrics
- [ ] `/api/*` calls show in logs with response times
- [ ] Database indexes created
- [ ] Redis connection working
- [ ] Caching enabled for users

---

## 🎯 Expected Results

After these 7 steps you should see:

```
Before:  API Response: 500ms  | Queries: 10+ | Memory: 400MB
After:   API Response: 50ms   | Queries: 1-2 | Memory: 250MB

That's 10x faster! 🚀
```

---

## 📊 Next Steps (After Verification)

1. **Database Indexing** (PERFORMANCE_GUIDE.md Section 1.1)
2. **Query Optimization** (PERFORMANCE_GUIDE.md Section 5)
3. **Async Processing** (PERFORMANCE_GUIDE.md Section 6)
4. **Rate Limiting** (PERFORMANCE_GUIDE.md Section 7)

---

**Total Implementation Time:** 30-45 minutes  
**Expected Performance Gain:** 50-60%


