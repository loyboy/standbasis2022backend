package basepackage.stand.standbasisprojectonev1.payload.onboarding;
import lombok.Data;
import java.util.List;

@Data
public class OnboardingStepTwoRequest {
   
    private List<ClassRequest> classRequest;
    private List<TeacherRequest> teaRequest;
    private List<StudentRequest> studentRequest;
    private List<TimetableRequest> timetableRequest;
}