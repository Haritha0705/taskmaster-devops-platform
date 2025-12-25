package com.taskmaster.mapper;

import com.taskmaster.dto.request.TaskCreateRequest;
import com.taskmaster.dto.request.TaskUpdateRequest;
import com.taskmaster.dto.response.TaskResponse;
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
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "assignee.fullName", target = "assigneeName")
    @Mapping(source = "createdBy.id", target = "createdBy")
    @Mapping(source = "updatedBy.id", target = "updatedBy")
    TaskResponse toResponse(TaskEntity entity);

    /* =======================
       CreateRequest → Entity
       ======================= */
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TaskEntity toEntity(TaskCreateRequest request);

    /* =======================
       UpdateRequest → Entity
       ======================= */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(TaskUpdateRequest request, @MappingTarget TaskEntity entity);
}
