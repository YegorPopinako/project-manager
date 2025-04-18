package ua.diploma.projectmanager.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.diploma.projectmanager.model.UserProject;
import ua.diploma.projectmanager.model.UserProjectId;
import ua.diploma.projectmanager.security.enums.Role;

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

    void deleteByUserIdAndProjectId(Long userId, Long projectId);

    @Modifying
    @Query(value = "DELETE FROM project WHERE id = :id", nativeQuery = true)
    void deleteProjectById(@Param("id") Long id);
}
