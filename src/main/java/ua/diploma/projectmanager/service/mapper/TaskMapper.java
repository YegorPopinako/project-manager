package ua.diploma.projectmanager.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.diploma.projectmanager.dto.task.TaskUpdateDto;
import ua.diploma.projectmanager.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapTaskFromTaskDto(TaskUpdateDto dto, @MappingTarget Task task);
}
