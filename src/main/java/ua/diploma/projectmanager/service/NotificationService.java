package ua.diploma.projectmanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.task.TaskFullInfoDto;
import ua.diploma.projectmanager.model.Notification;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.repository.NotificationRepository;
import ua.diploma.projectmanager.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TaskService taskService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 00 00 * * *")
    @Transactional
    public void generateDailyNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<TaskFullInfoDto> tasksDueTomorrow = taskService.findByEndPoint(tomorrow);
        for (TaskFullInfoDto task : tasksDueTomorrow) {
            Notification notification = new Notification();
            notification.setUser(task.getUser());
            notification.setMessage("Task: " + task.getTitle() + " in project: " + task.getProject().getTitle());
            notification.setCreatedAt(LocalDate.now());
            notification.setProjectId(task.getProject().getId());
            notificationRepository.save(notification);
        }
    }

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void clearNotifications() {
        notificationRepository.deleteAll();
    }

    public List<Notification> getUserNotifications(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return notificationRepository.findByUser(user);
    }
}

