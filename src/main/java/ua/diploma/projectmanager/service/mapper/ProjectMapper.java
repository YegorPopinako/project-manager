package ua.diploma.projectmanager.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.diploma.projectmanager.dto.project.ProjectUpdateDto;
import ua.diploma.projectmanager.model.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapProjectFromProjectDto(ProjectUpdateDto dto, @MappingTarget Project project);
}
