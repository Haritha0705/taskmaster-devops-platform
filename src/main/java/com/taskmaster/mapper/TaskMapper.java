package com.taskmaster.mapper;

import com.taskmaster.dto.request.TaskCreateRequest;
import com.taskmaster.dto.request.TaskUpdateRequest;
import com.taskmaster.dto.response.TaskResponse;
import com.taskmaster.dto.response.TaskSummaryResponse;
import com.taskmaster.entity.TaskEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

    /* =======================
       Entity → Response DTO
       ======================= */
    @Mapping(source = "createdBy.id", target = "createdBy")
    @Mapping(source = "updatedBy.id", target = "updatedBy")
    TaskResponse toResponse(TaskEntity entity);

    /* =======================
       Summary → Response DTO
       ======================= */
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TaskResponse toResponse(TaskSummaryResponse summary);

    /* =======================
       CreateRequest → Entity
       ======================= */
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TaskEntity toEntity(TaskCreateRequest request);

    /* =======================
       UpdateRequest → Entity
       ======================= */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(TaskUpdateRequest request, @MappingTarget TaskEntity entity);
}
