package ua.diploma.projectmanager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.diploma.projectmanager.model.Task;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    boolean existsByIdAndProject_Users_Email(Long taskId, String email);
}
