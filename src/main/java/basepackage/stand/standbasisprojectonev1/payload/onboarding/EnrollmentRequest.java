package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class EnrollmentRequest {
	    
	    private Long Calendar;	 	
	    
	    private String enrolDate;
	    
	    private Integer status; // This status is for the Student directly and not the Enrollment data
	    
	 	@NotNull
	    private Long classstream;
	    
	    @NotNull
	    private Long student;
	      
	    @NotNull
	    private Long enrolId;
}
