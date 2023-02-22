package basepackage.stand.standbasisprojectonev1.payload.onboarding;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class TimetableRequest {
	    
	 	@NotNull
	    @Size(max = 100)
	    private String class_name, arm, tea_name, subject;
	    
	    @NotNull
	    private String time;
	    
	    @NotNull
	    private String day;
}
