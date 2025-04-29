package ua.diploma.projectmanager.controller;

import com.google.api.services.classroom.ClassroomScopes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GoogleClassroomAuthController {

    @Value("${CLIENT_ID}")
    private String clientId;

    private static final String REDIRECT_URI = "http://localhost:8080/oauth2/callback/classroom";

    private static final List<String> SCOPES = List.of(
            ClassroomScopes.CLASSROOM_COURSES_READONLY,
            ClassroomScopes.CLASSROOM_COURSEWORK_ME,
            ClassroomScopes.CLASSROOM_COURSEWORKMATERIALS_READONLY
    );

    @GetMapping("/classroom-auth")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String authUrl = UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", SCOPES))
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();

        response.sendRedirect(authUrl);
    }
}
