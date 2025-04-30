package ua.diploma.projectmanager.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.model.UserProject;
import ua.diploma.projectmanager.model.UserProjectId;
import ua.diploma.projectmanager.security.enums.Role;

import java.util.List;

@Repository
public interface UserProjectRepository extends CrudRepository<UserProject, UserProjectId> {

    @Query("SELECT COUNT(up) > 0 FROM UserProject up " +
            "WHERE up.id.userId = :userId AND up.id.projectId = :projectId")
    boolean existsByUserIdAndProjectId(@Param("userId") Long userId,
                                       @Param("projectId") Long projectId);

    @Query("SELECT COUNT(up) > 0 FROM UserProject up " +
            "WHERE up.id.userId = :userId AND up.id.projectId = :projectId AND up.role = :role")
    boolean existsByUserIdAndProjectIdAndRole(@Param("userId") Long userId,
                                              @Param("projectId") Long projectId,
                                              @Param("role") Role role);

    @Query("select u.project from UserProject u where u.id.userId = ?1")
    List<Project> findProjectsByUserId(Long userId);

    void deleteByUserIdAndProjectId(Long userId, Long projectId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_project WHERE project_id = :projectId", nativeQuery = true)
    void deleteByProjectId(@Param("projectId") Long projectId);

    @Query("select u.user from UserProject u where u.id.projectId = ?1")
    List<User> getUsersByProjectId(Long id);
}
