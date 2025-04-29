package ua.diploma.projectmanager.security.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final JavaMailSender mailSender;

    public String generateSimpleToken(String email, Long projectId) {
        String raw = email + ":" + projectId;
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public void sendInvitationEmail(String email, String token) {
        String link = "http://localhost:8080/api/project/invite/accept?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("You've been invited to a project");
        message.setText("Click the link to accept the invitation: " + link);
        mailSender.send(message);
    }
}

