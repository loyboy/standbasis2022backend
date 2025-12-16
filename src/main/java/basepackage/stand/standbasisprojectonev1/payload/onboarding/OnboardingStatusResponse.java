package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class OnboardingStatusResponse {
    private long classCount;
    private long teacherCount;
    private long studentCount;
    private long timetableCount;
    
    private boolean schoolFilled;
    private boolean classroomFilled;
    private boolean teacherFilled;
    private boolean studentFilled;
    private boolean timetableFilled;
}
