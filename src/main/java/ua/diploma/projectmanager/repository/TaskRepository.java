package ua.diploma.projectmanager.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.diploma.projectmanager.model.Task;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE Task t SET t.user = null WHERE t.project.id = :projectId AND t.user.id = :userId")
    void unassignUserFromTasksInProject(@Param("projectId") Long projectId, @Param("userId") Long userId);

    List<Task> findByEndPoint(LocalDate endpoint);
}
