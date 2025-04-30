package ua.diploma.projectmanager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.diploma.projectmanager.model.Notification;
import ua.diploma.projectmanager.model.User;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

  List<Notification> findByUser(User user);
}
