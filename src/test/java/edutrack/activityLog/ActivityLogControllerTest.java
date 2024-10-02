package edutrack.activityLog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edutrack.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import edutrack.activityLog.controller.ActivityLogController;
import edutrack.activityLog.service.ActivityLogService;
import edutrack.student.service.StudentService;
import edutrack.user.repository.AccountRepository;
import edutrack.security.WebSecurityConfig;

@WebMvcTest(ActivityLogController.class)
@Import({JwtTokenProvider.class, WebSecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class ActivityLogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private ActivityLogService activityLogService;

    @MockBean
    private AccountRepository userRepository;

    @Test
    public void testAddStudentComment() throws Exception {
    	mockMvc.perform(post("/api/log_activites/comment")
                .contentType("application/json")
                .content("{\"studentId\":1,\"message\":\"Good student\"}"))
        		.andExpect(status().isOk());
    }

}
