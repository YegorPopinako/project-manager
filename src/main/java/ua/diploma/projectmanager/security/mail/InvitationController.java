package ua.diploma.projectmanager.security.mail;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.repository.UserRepository;
import ua.diploma.projectmanager.security.enums.AssignmentType;
import ua.diploma.projectmanager.service.UserService;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequestMapping("/api/project")
@RequiredArgsConstructor
@Slf4j
public class InvitationController {

    private final UserService userService;
    private final InvitationService invitationService;
    private final UserRepository userRepository;

    @GetMapping("/invite")
    @PreAuthorize("@userService.isUserAdminOfProject(#projectId, authentication.name)")
    public String showInviteForm(@RequestParam("projectId") Long projectId, Model model) {
        AssignUserDto dto = new AssignUserDto();
        dto.setId(projectId);
        model.addAttribute("assignUserDto", dto);
        return "invitation";
    }

    @PostMapping("/invite")
    @PreAuthorize("@userService.isUserAdminOfProject(#assignUserDto.id, authentication.name)")
    public String sendInvite(@ModelAttribute AssignUserDto assignUserDto, Model model) {
        String token = invitationService.generateSimpleToken(assignUserDto.getUserEmail(), assignUserDto.getId());
        invitationService.sendInvitationEmail(assignUserDto.getUserEmail(), token);

        model.addAttribute("message", "Invitation email sent to %s for project %d"
                .formatted(assignUserDto.getUserEmail(), assignUserDto.getId()));
        return "invitation";
    }

    @GetMapping("/invite/accept")
    public ResponseEntity<String> acceptInvite(@RequestParam("token") String token, Authentication auth) {
        String decoded;
        try {
            decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid token format");
        }

        String[] parts = decoded.split(":");
        if (parts.length != 2) {
            return ResponseEntity.badRequest().body("Malformed token");
        }

        String emailFromToken = parts[0];
        Long projectId = Long.parseLong(parts[1]);

        log.info("Accepting invite for user: {} and project: {}", emailFromToken, projectId);
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("You must be logged in"));

        if (!user.getEmail().equals(emailFromToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You can't accept someone else's invitation");
        }

        AssignUserDto dto = new AssignUserDto(projectId, emailFromToken, AssignmentType.PROJECT);
        userService.assignUser(dto);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/api/project/" + projectId))
                .build();
    }
}
