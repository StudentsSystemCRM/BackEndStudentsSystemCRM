package edutrack.activityLog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import edutrack.modul.activityLog.controller.ActivityLogController;
import edutrack.modul.activityLog.service.ActivityLogService;
import edutrack.modul.student.service.StudentService;
import edutrack.modul.user.repository.AccountRepository;
import edutrack.security.token.JwtTokenCreator;
import edutrack.security.token.JwtTokenValidator;
import edutrack.security.SecurityConfig;

@WebMvcTest(ActivityLogController.class)
@Import({JwtTokenValidator.class, JwtTokenCreator.class, SecurityConfig.class})
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
