package ua.diploma.projectmanager.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.CourseWorkMaterial;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoogleClassroomCallbackController {

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;
    private static final String REDIRECT_URI = "http://localhost:8080/oauth2/callback/classroom";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @GetMapping("/oauth2/callback/classroom")
    public String handleCallback(@RequestParam("code") String code, HttpSession session) throws Exception {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                transport,
                JSON_FACTORY,
                "https://oauth2.googleapis.com/token",
                clientId,
                clientSecret,
                code,
                REDIRECT_URI
        ).execute();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(transport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setFromTokenResponse(tokenResponse);

        session.setAttribute("googleCredential", credential);

        return "redirect:/classroom/courses";
    }

    @GetMapping("/classroom/courses")
    public String listCourses(HttpSession session, Model model) throws Exception {
        GoogleCredential credential = (GoogleCredential) session.getAttribute("googleCredential");

        if (credential == null) {
            return "redirect:/classroom-auth";
        }

        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        Classroom classroom = new Classroom.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName("Project Manager")
                .build();

        List<Course> courses = classroom.courses().list().execute().getCourses();
        if (courses == null) {
            courses = new ArrayList<>();
        }

        model.addAttribute("courses", courses);
        return "courses";
    }

    @GetMapping("/classroom/coursework/{courseId}")
    public String getCoursework(@PathVariable String courseId, Model model, HttpSession session) throws Exception {
        GoogleCredential credential = (GoogleCredential) session.getAttribute("googleCredential");

        if (credential == null) {
            return "redirect:/classroom-auth";
        }

        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        Classroom service = new Classroom.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName("Project Manager")
                .build();

        Course course = service.courses().get(courseId).execute();
        List<CourseWork> courseworks = service.courses().courseWork().list(courseId).execute().getCourseWork();
        List<CourseWorkMaterial> materials = service.courses().courseWorkMaterials().list(courseId).execute().getCourseWorkMaterial();
        if (courseworks == null) {
            courseworks = new ArrayList<>();
        }

        model.addAttribute("courseTitle", course.getName());
        model.addAttribute("courseworks", courseworks);
        model.addAttribute("materials", materials);

        return "coursework";
    }
}
