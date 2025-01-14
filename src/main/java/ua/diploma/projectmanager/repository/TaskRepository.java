package ua.diploma.projectmanager.repository;

import org.springframework.data.repository.CrudRepository;
import ua.diploma.projectmanager.model.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {

}
